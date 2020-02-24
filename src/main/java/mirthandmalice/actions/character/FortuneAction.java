package mirthandmalice.actions.character;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import mirthandmalice.actions.general.ImageAboveCreatureAction;
import mirthandmalice.interfaces.OnFortunePower;
import mirthandmalice.patch.fortune_misfortune.FortuneMisfortune;
import mirthandmalice.powers.ItPower;

import static mirthandmalice.patch.fortune_misfortune.FortuneMisfortune.FORTUNE_TEXTURE;

public class FortuneAction extends AbstractGameAction {
    private static final int FORTUNE_BLOCK = 3;

    private AbstractCard c;

    public FortuneAction(AbstractCard c, int amt)
    {
        this.c = c;
        this.amount = amt;
    }

    @Override
    public void update() {
        int fortuneAmount = FORTUNE_BLOCK;
        AbstractCreature itTarget = null;

        for (AbstractPower p : AbstractDungeon.player.powers)
        {
            if (p instanceof OnFortunePower)
            {
                fortuneAmount = ((OnFortunePower) p).onFortune(c);
            }

            if (p.ID.equals(ItPower.ID))
            {
                p.flash();
                itTarget = AbstractDungeon.player;
            }
        }

        int totalBlock = fortuneAmount * amount;

        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters)
        {
            if (!m.isDeadOrEscaped())
            {
                totalBlock += fortuneAmount * amount;

                if (itTarget != null)
                    break;

                for (AbstractPower p : m.powers)
                {
                    if (p.ID.equals(ItPower.ID))
                    {
                        itTarget = m;
                        break;
                    }
                }
                AbstractDungeon.actionManager.addToTop(new GainBlockAction(m, AbstractDungeon.player, fortuneAmount * this.amount, true));
                AbstractDungeon.actionManager.addToTop(new ImageAboveCreatureAction(m, FORTUNE_TEXTURE));
            }
        }

        if (itTarget != null)
        {
            AbstractDungeon.actionManager.addToTop(new GainBlockAction(itTarget, AbstractDungeon.player, totalBlock, false));
            AbstractDungeon.actionManager.addToTop(new ImageAboveCreatureAction(itTarget, FORTUNE_TEXTURE));
        }
        else
        {
            for (AbstractMonster m : AbstractDungeon.getMonsters().monsters)
            {
                if (!m.isDeadOrEscaped())
                {
                    AbstractDungeon.actionManager.addToTop(new GainBlockAction(m, AbstractDungeon.player, fortuneAmount * this.amount, true));
                    AbstractDungeon.actionManager.addToTop(new ImageAboveCreatureAction(m, FORTUNE_TEXTURE));
                }
            }

            AbstractDungeon.actionManager.addToTop(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, fortuneAmount * this.amount, true));
            AbstractDungeon.actionManager.addToTop(new ImageAboveCreatureAction(AbstractDungeon.player, FORTUNE_TEXTURE));
        }


        FortuneMisfortune.Fields.fortune.set(c, FortuneMisfortune.Fields.fortune.get(c) - this.amount);

        c.superFlash(Color.WHITE.cpy());

        this.isDone = true;
    }
}
