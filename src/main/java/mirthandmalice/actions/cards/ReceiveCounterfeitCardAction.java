package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.abstracts.ReceiveSignalCardsAction;
import mirthandmalice.actions.character.MakeTempCardInOtherHandAction;
import mirthandmalice.character.MirthAndMalice;

public class ReceiveCounterfeitCardAction extends ReceiveSignalCardsAction {
    public ReceiveCounterfeitCardAction(int amount)
    {
        this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        this.amount = amount;
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
            AbstractCard c = signaledCards.removeFirst();
            AbstractDungeon.actionManager.addToTop(new MakeTempCardInOtherHandAction(c.makeCopy(), this.amount));
            if (AbstractDungeon.player instanceof MirthAndMalice)
            {
                AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(c, ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand));
            }
            signaledGroups.removeFirst();
            this.isDone = true;
        }
        tickDuration();
    }
}