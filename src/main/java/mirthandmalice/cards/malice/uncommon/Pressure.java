package mirthandmalice.cards.malice.uncommon;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.powers.PressurePower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Pressure extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Pressure",
            1,
            CardType.POWER,
            CardTarget.SELF,
            CardRarity.UNCOMMON);

    public static final String ID = makeID(cardInfo.cardName);


    private static final int MAGIC = 3;
    private static final int UPG_MAGIC = 2;

    public Pressure() {
        super(cardInfo, false);

        setMagic(MAGIC, UPG_MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        applySelf(new PressurePower(p, TrackCardSource.useOtherEnergy, this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Pressure();
    }
}