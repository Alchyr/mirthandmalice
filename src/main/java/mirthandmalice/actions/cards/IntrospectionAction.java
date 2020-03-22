package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import mirthandmalice.abstracts.ReceiveSignalCardsAction;
import mirthandmalice.actions.character.WaitForSignalAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.util.MultiplayerHelper;

import static mirthandmalice.MirthAndMaliceMod.makeID;
import static mirthandmalice.util.MultiplayerHelper.partnerName;

public class IntrospectionAction extends AbstractGameAction {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("Introspection"));
    public static final String[] TEXT = uiStrings.TEXT;

    private boolean other;

    public IntrospectionAction(boolean other, int amount)
    {
        this.duration = Settings.ACTION_DUR_FASTER;
        this.actionType = ActionType.CARD_MANIPULATION;

        this.amount = amount;
        this.other = other;
    }

    @Override
    public void update() {
        if (other && AbstractDungeon.player instanceof MirthAndMalice) //played by other player.
        {
            MirthAndMalice p = (MirthAndMalice)AbstractDungeon.player;
            AbstractDungeon.actionManager.addToTop(new ReceiveIntrospectionCardsAction());
            AbstractDungeon.actionManager.addToTop(new WaitForSignalAction(TEXT[1] + partnerName + TEXT[2]));
            this.isDone = true;
        }
        else
        {
            if (this.duration == Settings.ACTION_DUR_FASTER) {
                if (AbstractDungeon.player.drawPile.size() <= this.amount) {
                    this.isDone = true;
                    MultiplayerHelper.sendP2PString("signal");
                }
                else
                {
                    AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.drawPile, this.amount, TEXT[0], false, false, false, false);
                    this.tickDuration();
                }

                return;
            }

            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards)
                {
                    MultiplayerHelper.sendP2PString(ReceiveSignalCardsAction.signalCardString(AbstractDungeon.player.drawPile.group.indexOf(c), AbstractDungeon.player.drawPile, true));

                    AbstractDungeon.player.drawPile.removeCard(c);
                    AbstractDungeon.player.drawPile.addToTop(c);
                }

                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }

            this.tickDuration();

            if (this.isDone)
                MultiplayerHelper.sendP2PString("signal");
        }
    }
}
