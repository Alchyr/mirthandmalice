package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import mirthandmalice.abstracts.ReceiveSignalCardsAction;

public class ReceiveIntrospectionCardsAction extends ReceiveSignalCardsAction {
    public ReceiveIntrospectionCardsAction()
    {
        this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        this.duration = 10.0f;
    }

    @Override
    public void update() {
        if (signals.size > 0)
        {
            processCardStrings();
        }
        while (signaledCards.size > 0)
        {
            AbstractCard c = signaledCards.removeFirst();
            CardGroup hopefullyDrawPile = signaledGroups.removeFirst();

            hopefullyDrawPile.removeCard(c);
            hopefullyDrawPile.addToTop(c);

            this.isDone = true;
        }
        this.tickDuration();
    }
}