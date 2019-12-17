package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;

import java.util.ArrayList;

public class FamiliarityAction extends AbstractGameAction {
    public FamiliarityAction()
    {
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        ArrayList<AbstractCard> validCards = new ArrayList<>();
        ArrayList<AbstractCard> handCards = new ArrayList<>();
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            validCards.addAll(((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.group);
            validCards.addAll(((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.group);
            validCards.addAll(((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard.group);

            handCards.addAll(((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.group);
        }
        validCards.addAll(AbstractDungeon.player.drawPile.group);
        validCards.addAll(AbstractDungeon.player.hand.group);
        validCards.addAll(AbstractDungeon.player.discardPile.group);

        handCards.addAll(AbstractDungeon.player.hand.group);

        for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisCombat)
        {
            if (validCards.remove(c))
            {
                c.freeToPlayOnce = true;
                if (handCards.remove(c))
                {
                    c.superFlash();
                }
            }
        }

        this.isDone = true;
    }
}
