package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import mirthandmalice.abstracts.ReceiveSignalCardsAction;
import mirthandmalice.actions.character.MakeTempCardInOtherHandAction;
import mirthandmalice.actions.character.WaitForSignalAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.combat.HandCardSelectReordering;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.MultiplayerHelper;

import static mirthandmalice.util.MultiplayerHelper.partnerName;
import static mirthandmalice.MirthAndMaliceMod.makeID;

public class OriginateAction extends AbstractGameAction {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("Originate"));
    public static final String[] TEXT = uiStrings.TEXT;

    private boolean upgraded;

    public OriginateAction(boolean upgraded)
    {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FASTER;
        this.upgraded = upgraded;
    }

    @Override
    public void update() {
        if (upgraded)
        {
            if (TrackCardSource.useOtherEnergy && AbstractDungeon.player instanceof MirthAndMalice) //played by other player.
            {
                AbstractDungeon.actionManager.addToTop(new ReceiveOriginateCardAction());
                AbstractDungeon.actionManager.addToTop(new WaitForSignalAction(TEXT[1] + partnerName + TEXT[2]));
                //Wait for signal action will become current action, with ReceiveOriginateCardAction at top.
                //When hand card view screen is opened, a HandCardSelectAction will be added on top, before the receive action.
                this.isDone = true;
                return;
            }
            else if (AbstractDungeon.player instanceof MirthAndMalice) {
                if (this.duration == Settings.ACTION_DUR_FASTER) {
                    if (AbstractDungeon.player.hand.isEmpty()) {
                        this.isDone = true;
                        MultiplayerHelper.sendP2PString("signal");
                        return;
                    }

                    HandCardSelectReordering.saveHandPreOpenScreen();

                    AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false, false);
                    this.tickDuration();
                    return;
                }

                if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
                    for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group)
                    {
                        AbstractDungeon.player.hand.addToTop(c); //add back to hand

                        AbstractCard card = c.makeStatEquivalentCopy();
                        card.modifyCostForCombat(-1);
                        AbstractDungeon.actionManager.addToTop(new MakeTempCardInOtherHandAction(card));
                        MultiplayerHelper.sendP2PString(ReceiveSignalCardsAction.signalCardString(AbstractDungeon.player.hand.group.indexOf(c), AbstractDungeon.player.hand, true));
                    }
                    AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
                    AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
                }
            }

            this.tickDuration();

            if (this.isDone)
                MultiplayerHelper.sendP2PString("signal");
        }
        else //do random.
        {
            CardGroup source;
            if (TrackCardSource.useOtherEnergy && AbstractDungeon.player instanceof MirthAndMalice) //played by other player.
            {
                source = ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand;
            }
            else
            {
                source = AbstractDungeon.player.hand;
            }

            if (source.isEmpty())
            {
                this.isDone = true;
            }
            else
            {
                AbstractCard c = source.getRandomCard(AbstractDungeon.cardRandomRng).makeStatEquivalentCopy();

                c.modifyCostForCombat(-1);

                if (TrackCardSource.useOtherEnergy)
                {
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(c));
                }
                else
                {
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInOtherHandAction(c));
                }
                this.isDone = true;
            }
        }
    }
}
