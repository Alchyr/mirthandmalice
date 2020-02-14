package mirthandmalice.cards.mirth.common;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.actions.general.MarkCardAction;
import mirthandmalice.actions.general.MarkRandomCardInHandAction;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Gift extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Gift",
            1,
            CardType.SKILL,
            CardTarget.NONE,
            CardRarity.COMMON);
    // skill

    public static final String ID = makeID(cardInfo.cardName);

    private static final int BLOCK = 6;

    public Gift() {
        super(cardInfo, true);

        setBlock(BLOCK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        block();

        addToBot(new MarkCardAction(this, true));

        if (upgraded)
        {
            addToBot(new MarkRandomCardInHandAction(true));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Gift();
    }
}