package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import mirthandmalice.abstracts.ReceiveSignalCardsAction;
import mirthandmalice.actions.character.WaitForSignalAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.combat.HandCardSelectReordering;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.MultiplayerHelper;

import static mirthandmalice.util.MultiplayerHelper.partnerName;
import static mirthandmalice.MirthAndMaliceMod.makeID;

public class CounterfeitAction extends AbstractGameAction {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("Counterfeit"));
    public static final String[] TEXT = uiStrings.TEXT;

    //private String exhaustString;

    public CounterfeitAction(int amount)
    {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FASTER;
        this.amount = amount;
        //exhaustString = "";
    }

    @Override
    public void update() {
        if (TrackCardSource.useOtherEnergy && AbstractDungeon.player instanceof MirthAndMalice) //played by other player.
        {
            AbstractDungeon.actionManager.addToTop(new ReceiveCounterfeitCardAction(this.amount));
            AbstractDungeon.actionManager.addToTop(new WaitForSignalAction(TEXT[1] + partnerName + TEXT[2]));
            this.isDone = true;
            return;
        } else if (AbstractDungeon.player instanceof MirthAndMalice) {
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
                    AbstractDungeon.player.hand.addToTop(c);

                    AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(c.makeCopy(), this.amount));
                    AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(c, AbstractDungeon.player.hand));
                    //exhaustString = "exhaustother_hand " + AbstractDungeon.player.hand.group.indexOf(c);
                    MultiplayerHelper.sendP2PString(ReceiveSignalCardsAction.signalCardString(AbstractDungeon.player.hand.group.indexOf(c), AbstractDungeon.player.hand, true));
                }
                AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            }
        }

        this.tickDuration();

        if (this.isDone)
            MultiplayerHelper.sendP2PString("signal");// + exhaustString); //this will add exhaust action to top, which will resolve before the queued ReceiveCounterfeitCardAction
    }
}
