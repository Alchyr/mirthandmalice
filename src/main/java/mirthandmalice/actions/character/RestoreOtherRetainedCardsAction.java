package mirthandmalice.actions.character;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.Iterator;

public class RestoreOtherRetainedCardsAction extends AbstractGameAction {
    private CardGroup limbo;
    private CardGroup hand;

    public RestoreOtherRetainedCardsAction(CardGroup limbo, CardGroup hand)
    {
        this.setValues(AbstractDungeon.player, this.source, -1);
        this.limbo = limbo;
        this.hand = hand;
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        Iterator c = this.limbo.group.iterator();

        while(c.hasNext()) {
            AbstractCard e = (AbstractCard)c.next();
            if (e.retain) {
                hand.addToTop(e);
                e.retain = false;
                c.remove();
            }
        }

        hand.refreshHandLayout();

        this.isDone = true;
    }
}
