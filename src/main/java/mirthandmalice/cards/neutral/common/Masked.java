package mirthandmalice.cards.neutral.common;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.NeutralCard;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Masked extends NeutralCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Masked",
            1,
            CardType.SKILL,
            CardTarget.SELF,
            CardRarity.COMMON);

    public static final String ID = makeID(cardInfo.cardName);

    private static final int UPG_COST = 0;

    private static final int BLOCK = 5;

    public Masked() {
        super(cardInfo, false);

        setCostUpgrade(UPG_COST);
        setBlock(BLOCK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        block();
        relinquish();
    }

    @Override
    public AbstractCard makeCopy() {
        return new Masked();
    }
}