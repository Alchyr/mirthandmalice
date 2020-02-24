package mirthandmalice.cards.mirth.uncommon;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.powers.NoHoldsBarredPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class NoHoldsBarred extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "NoHoldsBarred",
            1,
            CardType.POWER,
            CardTarget.SELF,
            CardRarity.UNCOMMON);
    // power

    public static final String ID = makeID(cardInfo.cardName);


    private static final int MAGIC = 3;
    private static final int UPG_MAGIC = 2;

    public NoHoldsBarred() {
        super(cardInfo, false);

        setMagic(MAGIC, UPG_MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        applySelf(new DexterityPower(p, -1));
        applySelf(new NoHoldsBarredPower(p, this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new NoHoldsBarred();
    }
}