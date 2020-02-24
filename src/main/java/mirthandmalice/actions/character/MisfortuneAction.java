package mirthandmalice.actions.character;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import mirthandmalice.actions.general.AllEnemyLoseHPAction;
import mirthandmalice.actions.general.ImageAboveCreatureAction;
import mirthandmalice.interfaces.OnMisfortunePower;
import mirthandmalice.patch.fortune_misfortune.FortuneMisfortune;

import static mirthandmalice.patch.fortune_misfortune.FortuneMisfortune.MISFORTUNE_TEXTURE;

public class MisfortuneAction extends AbstractGameAction {
    private static final int MISFORTUNE_LOSS = 5;

    private AbstractCard c;

    public MisfortuneAction(AbstractCard c, int amt)
    {
        this.c = c;
        this.amount = amt;
    }

    @Override
    public void update() {
        int misfortuneAmount = MISFORTUNE_LOSS;

        for (AbstractPower p : AbstractDungeon.player.powers)
        {
            if (p instanceof OnMisfortunePower)
            {
                misfortuneAmount = ((OnMisfortunePower) p).onMisfortune(c);
            }
        }

        AbstractDungeon.actionManager.addToTop(new AllEnemyLoseHPAction(AbstractDungeon.player, misfortuneAmount * this.amount, AbstractGameAction.AttackEffect.FIRE));
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters)
        {
            if (!m.isDeadOrEscaped() && !m.halfDead)
            {
                AbstractDungeon.actionManager.addToTop(new ImageAboveCreatureAction(m, MISFORTUNE_TEXTURE));
            }
        }

        FortuneMisfortune.Fields.misfortune.set(c, FortuneMisfortune.Fields.misfortune.get(c) - this.amount);

        c.superFlash(Color.BLACK.cpy());

        this.isDone = true;
    }
}