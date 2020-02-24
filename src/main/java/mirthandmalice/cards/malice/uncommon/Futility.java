package mirthandmalice.cards.malice.uncommon;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.patch.manifestation.ManifestField;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Futility extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Futility",
            0,
            CardType.SKILL,
            CardTarget.SELF,
            CardRarity.UNCOMMON);
    // skill

    public final static String ID = makeID(cardInfo.cardName);

    private static final int MAGIC = 1;
    private static final int UPG_MAGIC = 1;

    public Futility() {
        super(cardInfo, true);

        setMagic(MAGIC, UPG_MAGIC);
    }

    @Override
    public void applyPowers() {
        super.applyPowers();

        if (ManifestField.isManifested())
        {
            this.target = CardTarget.ALL_ENEMY;
        }
        else
        {
            this.target = CardTarget.SELF;
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (TrackCardSource.useMyEnergy && ManifestField.isManifested() ||
        TrackCardSource.useOtherEnergy && ManifestField.otherManifested())
        {
            for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters)
            {
                applySingle(mo, getWeak(mo, this.magicNumber));
            }
        }

        applySelf(new WeakPower(p, this.magicNumber, false));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Futility();
    }
}