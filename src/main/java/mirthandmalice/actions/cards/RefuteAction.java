package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import mirthandmalice.actions.character.WaitForSignalAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.MultiplayerHelper;

import java.util.HashMap;

import static mirthandmalice.util.MultiplayerHelper.partnerName;
import static mirthandmalice.MirthAndMaliceMod.makeID;

public class RefuteAction extends AbstractGameAction {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("Refute"));
    public static final String[] TEXT = uiStrings.TEXT;

    private HashMap<AbstractCard, Boolean> canExhaust = new HashMap<>();

    public RefuteAction()
    {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FASTER;
    }

    @Override
    public void update() {
        if (TrackCardSource.useOtherEnergy && AbstractDungeon.player instanceof MirthAndMalice) //played by other player.
        {
            MirthAndMalice p = (MirthAndMalice)AbstractDungeon.player;
            AbstractDungeon.actionManager.addToTop(new WaitForSignalAction(TEXT[1] + partnerName + TEXT[2]));
            this.isDone = true;
        }
        else if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (this.duration == Settings.ACTION_DUR_FASTER) {
                canExhaust.clear();

                for (AbstractCard c : ((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard.group)
                {
                    canExhaust.put(c, true);
                }
                for (AbstractCard c : AbstractDungeon.player.discardPile.group)
                {
                    canExhaust.put(c, false);
                }

                if (canExhaust.isEmpty()) {
                    this.isDone = true;
                    MultiplayerHelper.sendP2PString("signal");
                    return;
                }
                else if (canExhaust.size() == 1) {
                    for (AbstractCard c : canExhaust.keySet())
                    {
                        String signal = "signal";
                        int index = -1;
                        if (canExhaust.get(c))
                        {
                            index = ((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard.group.indexOf(c);
                            if (index != -1)
                            {
                                signal += "exhaustdiscard " + index;
                                ((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard.moveToExhaustPile(c);
                            }
                        }
                        else
                        {
                            index = AbstractDungeon.player.discardPile.group.indexOf(c);
                            if (index != -1)
                            {
                                signal += "exhaustother_discard " + index;
                                AbstractDungeon.player.discardPile.moveToExhaustPile(c);
                            }
                        }

                        this.isDone = true;
                        MultiplayerHelper.sendP2PString(signal);
                        return;
                    }
                }
                else {
                    CardGroup options = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                    options.group.addAll(canExhaust.keySet());
                    AbstractDungeon.gridSelectScreen.open(options, 1, TEXT[0], false, false, false, false);
                    this.tickDuration();
                    return;
                }
            }

            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);

                String signal = "signal";
                int index = -1;
                if (canExhaust.get(c))
                {
                    index = ((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard.group.indexOf(c);
                    if (index != -1)
                    {
                        signal += "exhaustdiscard " + index;
                        ((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard.moveToExhaustPile(c);
                    }
                }
                else
                {
                    index = AbstractDungeon.player.discardPile.group.indexOf(c);
                    if (index != -1)
                    {
                        signal += "exhaustother_discard " + index;
                        AbstractDungeon.player.discardPile.moveToExhaustPile(c);
                    }
                }
                this.isDone = true;
                MultiplayerHelper.sendP2PString(signal);

                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                AbstractDungeon.player.hand.refreshHandLayout();
            }
        }
    }
}
