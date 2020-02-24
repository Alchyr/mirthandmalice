package mirthandmalice.cards.neutral.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.purple.SashWhip;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.NeutralCard;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.patch.manifestation.ManifestField;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Smack extends NeutralCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Smack",
            2,
            CardType.ATTACK,
            CardTarget.ENEMY,
            CardRarity.COMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int DAMAGE = 14;
    private static final int UPG_DAMAGE = 2;

    private static final int DEBUFF = 1;
    private static final int UPG_DEBUFF = 1;

    public Smack()
    {
        super(cardInfo, false);

        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(DEBUFF, UPG_DEBUFF);
    }

    @Override
    public void triggerOnGlowCheck() {
        if (ManifestField.inHandManifested(this))
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        else
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));

        if (TrackCardSource.useMyEnergy)
        {
            if (ManifestField.isManifested())
            {
                applySingle(m, getVuln(m, this.magicNumber));
            }
        }
        else
        {
            if (ManifestField.otherManifested())
            {
                applySingle(m, getVuln(m, this.magicNumber));
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Smack();
    }
}
