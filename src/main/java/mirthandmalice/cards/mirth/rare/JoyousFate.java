package mirthandmalice.cards.mirth.rare;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.actions.general.MarkRandomCardInDrawAction;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class JoyousFate extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "JoyousFate",
            1,
            CardType.SKILL,
            CardTarget.NONE,
            CardRarity.RARE);

    public static final String ID = makeID(cardInfo.cardName);


    private static final int MAGIC = 4;
    private static final int UPG_MAGIC = 2;

    public JoyousFate() {
        super(cardInfo, false);

        setMagic(MAGIC, UPG_MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new MarkRandomCardInDrawAction(this.magicNumber, true));
    }

    @Override
    public AbstractCard makeCopy() {
        return new JoyousFate();
    }
}