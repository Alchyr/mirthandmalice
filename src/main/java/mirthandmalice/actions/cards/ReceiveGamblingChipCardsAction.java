package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.abstracts.ReceiveSignalCardsAction;
import mirthandmalice.actions.character.ForceDrawAction;
import mirthandmalice.actions.character.OtherPlayerDiscardAction;
import mirthandmalice.character.MirthAndMalice;

public class ReceiveGamblingChipCardsAction extends ReceiveSignalCardsAction {
    public ReceiveGamblingChipCardsAction()
    {
        this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        this.duration = 10.0f;
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


                this.addToTop(new ForceDrawAction(AbstractDungeon.player,true, amt));

                OtherPlayerDiscardAction.moveToAltDiscard(source, p.otherPlayerDiscard, c);
                c.triggerOnManualDiscard();
                GameActionManager.incrementDiscard(false);

                p.otherPlayerHand.applyPowers();
                p.hand.applyPowers();
            }
            this.isDone = true;
        }
        tickDuration();
    }
}