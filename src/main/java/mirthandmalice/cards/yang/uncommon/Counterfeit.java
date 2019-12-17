package mirthandmalice.cards.yang.uncommon;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.actions.cards.CounterfeitAction;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Counterfeit extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Counterfeit",
            0,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.UNCOMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int COPIES = 2;
    private static final int UPG_COPIES = 1;

    public Counterfeit()
    {
        super(cardInfo, false);

        setMagic(COPIES, UPG_COPIES);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new CounterfeitAction(this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Counterfeit();
    }
}