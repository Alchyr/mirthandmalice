package mirthandmalice.cards.mirth.uncommon;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.powers.SerendipityPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Serendipity extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Serendipity",
            2,
            CardType.POWER,
            CardTarget.NONE,
            CardRarity.UNCOMMON);
    // power

    public static final String ID = makeID(cardInfo.cardName);


    private static final int MAGIC = 1;
    private static final int UPG_MAGIC = 1;

    public Serendipity() {
        super(cardInfo, false);

        setMagic(MAGIC, UPG_MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        applySelf(new SerendipityPower(p, TrackCardSource.useOtherEnergy, this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Serendipity();
    }
}