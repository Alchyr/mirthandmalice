package mirthandmalice.cards.mirth.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.effects.FlameBurstEffect;
import mirthandmalice.patch.combat.BurstActive;
import mirthandmalice.patch.enums.CustomCardTags;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Scorch extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Scorch",
            2,
            AbstractCard.CardType.ATTACK,
            AbstractCard.CardTarget.ENEMY,
            AbstractCard.CardRarity.COMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int DAMAGE = 5;
    private static final int HIT_COUNT = 2;
    private static final int COST_UPG = 1;

    public Scorch()
    {
        super(cardInfo, false);

        setDamage(DAMAGE);
        setMagic(HIT_COUNT);

        setCostUpgrade(COST_UPG);

        tags.add(CustomCardTags.MK_BURST);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < this.magicNumber; i++)
        {
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.FIRE));
        }
        if (m != null)
        {
            if (BurstActive.active.get(m))
            {
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new FlameBurstEffect(m.hb.cX, m.hb.cY, 30)));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.FIRE));
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Scorch();
    }
}