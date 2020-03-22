package mirthandmalice.cards.mirth.common;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.actions.general.MarkCardAction;
import mirthandmalice.actions.general.MarkRandomCardInDrawAction;
import mirthandmalice.actions.general.MarkTopCardInDrawAction;
import mirthandmalice.patch.card_use.UseCardActionDestination;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Optimism extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Optimism",
            0,
            CardType.SKILL,
            CardTarget.NONE,
            CardRarity.COMMON);
    // skill

    public static final String ID = makeID(cardInfo.cardName);


    public Optimism() {
        super(cardInfo, true);

        UseCardActionDestination.CardFields.swapPiles.set(this, true);
        UseCardActionDestination.CardFields.returnDraw.set(this, true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new MarkTopCardInDrawAction(TrackCardSource.useOtherEnergy,true));

        if (upgraded)
        {
            addToBot(new MarkCardAction(this, true));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Optimism();
    }
}