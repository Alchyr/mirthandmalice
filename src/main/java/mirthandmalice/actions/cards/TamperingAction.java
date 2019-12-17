package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;

import java.util.ArrayList;

public class TamperingAction extends AbstractGameAction {
    public TamperingAction(int amt)
    {
        this.amount = amt;
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        ArrayList<AbstractCard> selfValidCards = new ArrayList<>();
        ArrayList<AbstractCard> otherValidCards = new ArrayList<>();

        boolean isMultiplayer = AbstractDungeon.player instanceof MirthAndMalice;

        for (AbstractCard c : AbstractDungeon.player.hand.group)
        {
            if (c.canUpgrade())
                selfValidCards.add(c);
        }
        if (isMultiplayer)
        {
            for (AbstractCard c : ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.group)
            {
                if (c.canUpgrade())
                    otherValidCards.add(c);
            }
        }

        boolean upgradedSelf = false;
        //to ensure rng is consistent, cards in mokou's hand will always be upgraded first
        if (isMultiplayer && ((MirthAndMalice) AbstractDungeon.player).isMirth)
        {
            for (int i = 0; i < this.amount && !selfValidCards.isEmpty(); ++i)
            {
                AbstractCard c = selfValidCards.remove(AbstractDungeon.cardRandomRng.random(selfValidCards.size() - 1));

                c.upgrade();
                c.superFlash();
            }
            upgradedSelf = true;
        }
        else if (isMultiplayer) //player is keine, upgrade other player first
        {
            for (int i = 0; i < this.amount && !otherValidCards.isEmpty(); ++i)
            {
                AbstractCard c = otherValidCards.remove(AbstractDungeon.cardRandomRng.random(otherValidCards.size() - 1));

                c.upgrade();
                c.superFlash();
            }
        }

        //upgrade the other player.
        if (upgradedSelf) //can only be true in multiplayer
        {
            for (int i = 0; i < this.amount && !otherValidCards.isEmpty(); ++i)
            {
                AbstractCard c = otherValidCards.remove(AbstractDungeon.cardRandomRng.random(otherValidCards.size() - 1));

                c.upgrade();
                c.superFlash();
            }
        }
        else //whether other player was upgraded first, or playing in single player and somehow got this card
        {
            for (int i = 0; i < this.amount && !selfValidCards.isEmpty(); ++i)
            {
                AbstractCard c = selfValidCards.remove(AbstractDungeon.cardRandomRng.random(selfValidCards.size() - 1));

                c.upgrade();
                c.superFlash();
            }
        }

        this.isDone = true;
    }
}
