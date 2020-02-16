package mirthandmalice.cards.malice.uncommon;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.actions.cards.PlottingAction;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Plotting extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Plotting",
            0,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.UNCOMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int UPG = 2;
    private static final int UPG_UPG = 1;

    public Plotting()
    {
        super(cardInfo, false);

        setMagic(UPG, UPG_UPG);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new PlottingAction(this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Plotting();
    }
}