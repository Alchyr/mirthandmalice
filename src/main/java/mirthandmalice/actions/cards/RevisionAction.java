package mirthandmalice.actions.cards;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import mirthandmalice.abstracts.ReceiveSignalCardsAction;
import mirthandmalice.actions.character.WaitForSignalAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.effects.ShowCardAndAddToOtherDiscardEffect;
import mirthandmalice.effects.ShowCardAndAddToOtherHandEffect;
import mirthandmalice.patch.combat.HandCardSelectReordering;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.MultiplayerHelper;

import static mirthandmalice.util.MultiplayerHelper.partnerName;
import static mirthandmalice.MirthAndMaliceMod.makeID;

public class RevisionAction extends AbstractGameAction {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("Revision"));
    public static final String[] TEXT = uiStrings.TEXT;

    public RevisionAction()
    {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FASTER;
    }

    @Override
    public void update() {
        if (TrackCardSource.useOtherEnergy && AbstractDungeon.player instanceof MirthAndMalice) //played by other player.
        {
            AbstractDungeon.actionManager.addToTop(new ReceiveRevisionCardsAction());
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
                AbstractDungeon.handCardSelectScreen.open(TEXT[0], AbstractDungeon.player.hand.size(), true, true);
                this.tickDuration();
                return;
            }

            if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
                int otherHandSpace = BaseMod.MAX_HAND_SIZE - ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.size();
                int handIndex = AbstractDungeon.player.hand.size(); //the next card added would be at this index, which makes it the correct index.
                for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group)
                {
                    if (otherHandSpace > 0)
                    {
                        AbstractDungeon.effectList.add(new ShowCardAndAddToOtherHandEffect(c, false));
                        --otherHandSpace;
                    }
                    else
                    {
                        AbstractDungeon.effectList.add(new ShowCardAndAddToOtherDiscardEffect(c));
                    }
                    MultiplayerHelper.sendP2PString(ReceiveSignalCardsAction.signalCardString(handIndex++, AbstractDungeon.player.hand, true));
                }
                AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            }
        }

        this.tickDuration();

        if (this.isDone)
            MultiplayerHelper.sendP2PString("signal");
    }
}
