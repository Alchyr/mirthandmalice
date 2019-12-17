package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.util.CombatDeckHelper;

public class UnearthAction extends AbstractGameAction {
    private boolean other;

    public UnearthAction(int amount, boolean other)
    {
        this.actionType = ActionType.WAIT;
        this.amount = amount;

        this.other = other;
    }

    @Override
    public void update() {
        if (other && AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (!((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.isEmpty()) {
                AbstractCard card = ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.getTopCard();
                if (!CombatDeckHelper.inOtherInitialDeck(card)) {
                    AbstractDungeon.actionManager.addToTop(new GainEnergyAction(this.amount));
                }
            }
        }
        else
        {
            if (!AbstractDungeon.player.drawPile.isEmpty()) {
                AbstractCard card = AbstractDungeon.player.drawPile.getTopCard();
                if (!CombatDeckHelper.inInitialDeck(card)) {
                    AbstractDungeon.actionManager.addToTop(new GainEnergyAction(this.amount));
                }
            }
        }
        this.isDone = true;
    }
}
