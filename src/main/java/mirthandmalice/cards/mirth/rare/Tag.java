package mirthandmalice.cards.mirth.rare;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.actions.cards.RemoveAllItAction;
import mirthandmalice.powers.ItPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Tag extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Tag",
            1,
            CardType.POWER,
            CardTarget.SELF,
            CardRarity.RARE);

    public static final String ID = makeID(cardInfo.cardName);


    public Tag() {
        super(cardInfo, true);

        setInnate(false, true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new RemoveAllItAction());
        applySelf(new ItPower(p));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Tag();
    }
}