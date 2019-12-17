package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.abstracts.ReceiveSignalCardsAction;
import mirthandmalice.actions.character.MakeTempCardInOtherHandAction;

public class ReceiveOriginateCardAction extends ReceiveSignalCardsAction {
    public ReceiveOriginateCardAction()
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
        if (signaledCards.size > 0)
        {
            AbstractCard c = signaledCards.removeFirst().makeStatEquivalentCopy();
            signaledGroups.removeFirst();
            c.modifyCostForCombat(-1);
            AbstractDungeon.actionManager.addToTop(new MakeTempCardInOtherHandAction(c));
            this.isDone = true;
        }
        this.tickDuration();
    }
}