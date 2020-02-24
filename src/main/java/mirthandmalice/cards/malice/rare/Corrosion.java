package mirthandmalice.cards.malice.rare;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.powers.CorrosionPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Corrosion extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Corrosion",
            1,
            CardType.POWER,
            CardTarget.SELF,
            CardRarity.RARE);
    // power

    public static final String ID = makeID(cardInfo.cardName);

    private static final int UPG_COST = 0;

    private static final int MAGIC = 1;

    public Corrosion() {
        super(cardInfo, false);

        setCostUpgrade(UPG_COST);
        setMagic(MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        applySelf(new CorrosionPower(p, this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Corrosion();
    }
}