package mirthandmalice.cards.neutral.uncommon;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.EquilibriumPower;
import mirthandmalice.abstracts.NeutralCard;
import mirthandmalice.powers.PatiencePower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Patience extends NeutralCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Patience",
            1,
            CardType.SKILL,
            CardTarget.SELF,
            CardRarity.UNCOMMON);
    // skill

    public final static String ID = makeID(cardInfo.cardName);

    private static final int UPG_COST = 0;


    public Patience() {
        super(cardInfo, false);

        setCostUpgrade(UPG_COST);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        applySelf(new PatiencePower(p, 1));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Patience();
    }
}