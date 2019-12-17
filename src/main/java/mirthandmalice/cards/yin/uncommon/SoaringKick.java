package mirthandmalice.cards.yin.uncommon;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.CardInfo;
import mirthandmalice.util.OtherPlayerCardQueueItem;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class SoaringKick extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "SoaringKick",
            1,
            AbstractCard.CardType.ATTACK,
            AbstractCard.CardTarget.ENEMY,
            AbstractCard.CardRarity.UNCOMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int DAMAGE = 2;
    private static final int UPG_DAMAGE = 1;

    private boolean isEcho;

    public SoaringKick()
    {
        super(cardInfo, false);

        setDamage(DAMAGE, UPG_DAMAGE);
        isEcho = false;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null && !m.isDeadOrEscaped())
        {
            float yOffset = isEcho ? 0 : 20 * Settings.scale;
            float xOffset = isEcho ? 0 : -10 * Settings.scale;
            AbstractDungeon.actionManager.addToBottom(new VFXAction(new FlashAtkImgEffect(m.hb.cX + xOffset, m.hb.cY + yOffset, AbstractGameAction.AttackEffect.SMASH, false)));
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.NONE));
            yOffset -= 10;
            xOffset += 5;
            AbstractDungeon.actionManager.addToBottom(new VFXAction(new FlashAtkImgEffect(m.hb.cX + xOffset, m.hb.cY + yOffset, AbstractGameAction.AttackEffect.SMASH, false)));
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.NONE));

            if (!isEcho)
            {
                AbstractCard copy = this.makeSameInstanceOf();
                if (copy instanceof SoaringKick)
                {
                    ((SoaringKick)copy).isEcho = true;
                }

                p.limbo.addToBottom(copy);

                copy.current_x = this.current_x;
                copy.target_x = this.target_x;
                copy.current_y = this.current_y;
                copy.target_y = this.target_y;

                copy.freeToPlayOnce = true;
                copy.purgeOnUse = true;

                copy.calculateCardDamage(m);

                if (TrackCardSource.useOtherEnergy)
                {
                    AbstractDungeon.actionManager.cardQueue.add(new OtherPlayerCardQueueItem(copy, m, this.energyOnUse));
                }
                else
                {
                    AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(copy, m, this.energyOnUse));
                }
            }
        }

    }

    @Override
    public AbstractCard makeCopy() {
        return new SoaringKick();
    }
}