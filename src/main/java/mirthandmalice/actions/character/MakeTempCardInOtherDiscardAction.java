package mirthandmalice.actions.character;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.effects.ShowCardAndAddToOtherDiscardEffect;

public class MakeTempCardInOtherDiscardAction extends AbstractGameAction {
    private AbstractCard cardToMake;
    private int numCards;
    private boolean sameUUID;

    public MakeTempCardInOtherDiscardAction(AbstractCard card, int amount) {
        UnlockTracker.markCardAsSeen(card.cardID);
        this.numCards = amount;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
        this.cardToMake = card;
        this.sameUUID = false;
    }

    public MakeTempCardInOtherDiscardAction(AbstractCard card, boolean sameUUID) {
        this(card, 1);
        this.sameUUID = sameUUID;
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            int effectCap = Math.min(this.numCards, 5);
            int extraCards = this.numCards - effectCap;

            effectCap += extraCards / 2;
            extraCards -= extraCards / 2;

            for(int i = 0; i < effectCap; ++i) {
                AbstractDungeon.effectList.add(new ShowCardAndAddToOtherDiscardEffect(this.makeNewCard()));
            }
            if (extraCards > 0)
            {
                for (int i = 0; i < extraCards; ++i) {
                    AbstractCard copy = this.makeNewCard();

                    if (AbstractDungeon.player instanceof MirthAndMalice)
                    {
                        ((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard.addToTop(copy);
                    }
                    else
                    {
                        AbstractDungeon.player.discardPile.addToTop(copy);
                    }

                    copy.triggerWhenCopied();
                }
            }

            this.duration -= Gdx.graphics.getDeltaTime();
        }

        this.tickDuration();
    }// 43

    private AbstractCard makeNewCard() {
        return this.sameUUID ? this.cardToMake.makeSameInstanceOf() : this.cardToMake.makeStatEquivalentCopy();
    }
}
