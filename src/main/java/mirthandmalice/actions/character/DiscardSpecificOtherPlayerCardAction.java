package mirthandmalice.actions.character;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.combat.SoulAltDiscard;

public class DiscardSpecificOtherPlayerCardAction extends AbstractGameAction
{
    private AbstractCard targetCard;
    private CardGroup group;

    public DiscardSpecificOtherPlayerCardAction(AbstractCard targetCard) {
        this.targetCard = targetCard;
        this.actionType = AbstractGameAction.ActionType.DISCARD;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    public DiscardSpecificOtherPlayerCardAction(AbstractCard targetCard, CardGroup group) {
        this.targetCard = targetCard;
        this.group = group;
        this.actionType = AbstractGameAction.ActionType.DISCARD;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (this.group == null) {
                if (AbstractDungeon.player instanceof MirthAndMalice)
                    this.group = ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand;
                else
                    this.group = AbstractDungeon.player.hand;
            }

            if (this.group.contains(this.targetCard)) {
                moveToAltDiscard(this.group, AbstractDungeon.player instanceof MirthAndMalice ? ((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard : AbstractDungeon.player.discardPile, this.targetCard);
                GameActionManager.incrementDiscard(false);
                this.targetCard.triggerOnManualDiscard();
            }
        }

        this.tickDuration();
    }

    public static void moveToAltDiscard(CardGroup source, CardGroup discardPile, AbstractCard c)
    {
        if (AbstractDungeon.player.hoveredCard == c) { // Copy of resetCardBeforeMoving in CardGroup
            AbstractDungeon.player.releaseCard();
        }
        AbstractDungeon.actionManager.removeFromQueue(c);
        c.unhover();
        c.untip();
        c.stopGlowing();
        source.group.remove(c); // End of copy

        c.shrink(); // Copy of CardGroup moveToDiscard code, with additional code to move it to alternate pile
        c.darken(false);
        SoulAltDiscard.altGroup = discardPile;
        AbstractDungeon.getCurrRoom().souls.discard(c);
        AbstractDungeon.player.onCardDrawOrDiscard();
    }
}
