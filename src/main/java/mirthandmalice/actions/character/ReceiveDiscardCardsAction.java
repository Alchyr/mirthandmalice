package mirthandmalice.actions.character;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.abstracts.ReceiveSignalCardsAction;
import mirthandmalice.character.MirthAndMalice;


public class ReceiveDiscardCardsAction extends ReceiveSignalCardsAction {
    public ReceiveDiscardCardsAction()
    {
        this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        this.duration = 10.0f; //shouldn't take that long, as this action only starts updating once the signal that discard is complete is received.
    }

    @Override
    public void update() {
        if (!(AbstractDungeon.player instanceof MirthAndMalice))
        {
            this.isDone = true;
            return;
        }

        if (signals.size > 0)
        {
            processCardStrings();
        }

        if (signaledCards.size > 0)
        {
            int amt = signaledCards.size;
            MirthAndMalice p = (MirthAndMalice) AbstractDungeon.player;

            for (int i = 0; i < amt; ++i)
            {
                CardGroup source = signaledGroups.removeFirst();
                AbstractCard c = signaledCards.removeFirst();

                OtherPlayerDiscardAction.moveToAltDiscard(source, p.otherPlayerDiscard, c);
                c.triggerOnManualDiscard();
                GameActionManager.incrementDiscard(false);

                p.hand.applyPowers();
            }
            this.isDone = true;
        }
        tickDuration();
    }
}