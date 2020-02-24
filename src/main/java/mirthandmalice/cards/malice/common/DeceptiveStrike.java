package mirthandmalice.cards.malice.common;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.patch.manifestation.ManifestField;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class DeceptiveStrike extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "DeceptiveStrike",
            1,
            CardType.ATTACK,
            CardTarget.ENEMY,
            CardRarity.COMMON);

    public static final String ID = makeID(cardInfo.cardName);


    private static final int DAMAGE = 6;
    private static final int UPG_DAMAGE = 2;

    private static final int MAGIC = 2;

    public DeceptiveStrike() {
        super(cardInfo, false);

        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(MAGIC);
    }

    @Override
    public void triggerOnGlowCheck() {
        if (ManifestField.isManifested())
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        else
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        damageSingle(m, AbstractGameAction.AttackEffect.BLUNT_HEAVY);

        if (TrackCardSource.useMyEnergy)
        {
            if (!ManifestField.isManifested())
            {
                for (int i = 0; i < this.magicNumber; ++i)
                    damageSingle(m, MathUtils.randomBoolean() ? AbstractGameAction.AttackEffect.SLASH_HORIZONTAL : AbstractGameAction.AttackEffect.SLASH_VERTICAL);
            }
        }
        else
        {
            if (ManifestField.isManifested())
            {
                for (int i = 0; i < this.magicNumber; ++i)
                    damageSingle(m, MathUtils.randomBoolean() ? AbstractGameAction.AttackEffect.SLASH_HORIZONTAL : AbstractGameAction.AttackEffect.SLASH_VERTICAL);
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new DeceptiveStrike();
    }
}