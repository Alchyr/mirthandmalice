package mirthandmalice.actions.character;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import mirthandmalice.abstracts.ReceiveSignalCardsAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.combat.HandCardSelectReordering;
import mirthandmalice.patch.combat.SoulAltDiscard;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.MultiplayerHelper;

import static mirthandmalice.MirthAndMaliceMod.makeID;
import static mirthandmalice.util.MultiplayerHelper.partnerName;


//THIS IS TO BE USED IF:
//  A. The card has the other player discard cards.
//  B. A neutral situation where without a card being played, cards are discarded from the other hand.
//
//  If a card discards a card from the one who plays it, use a normal discard action.
//  If this is used in a card, bonus logic for deciding which action to use should not exist.
//  If this is used in a neutral situation, there BETTER be logic.

public class OtherPlayerDiscardAction extends AbstractGameAction {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;

    private static final UIStrings otherStrings;
    public static final String[] otherText;

    private MirthAndMalice p;
    private boolean endTurn;
    private static final float DURATION;
    private boolean isRandom;

    private boolean waiting;

    public OtherPlayerDiscardAction(MirthAndMalice p, AbstractCreature source, int amount, boolean endTurn) {
        this(p, source, amount, true, endTurn);
    }

    public OtherPlayerDiscardAction(MirthAndMalice p, AbstractCreature source, int amount, boolean isRandom, boolean endTurn) {
        this.p = p;
        this.setValues(target, source, amount);
        this.actionType = ActionType.DISCARD;
        this.endTurn = endTurn;
        this.isRandom = isRandom;
        this.duration = DURATION;

        this.waiting = false;
    }

    public static void moveToAltDiscard(CardGroup source, CardGroup discardPile, AbstractCard c)
    {
        if (AbstractDungeon.player.hoveredCard == c) {
            AbstractDungeon.player.releaseCard();
        }
        AbstractDungeon.actionManager.removeFromQueue(c);
        c.unhover();
        c.untip();
        c.stopGlowing();
        source.group.remove(c);

        c.shrink();
        c.darken(false);
        SoulAltDiscard.altGroup = discardPile;
        AbstractDungeon.getCurrRoom().souls.discard(c);
        AbstractDungeon.player.onCardDrawOrDiscard();
    }

    public void update() {
        if (this.duration == DURATION) {
            AbstractCard c;

            if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
                this.isDone = true;
                return;
            }

            if (TrackCardSource.useOtherEnergy) //Played by other player, which means it is my discard.
            {
                int i;

                if (this.p.hand.size() <= this.amount) {
                    this.amount = this.p.hand.size();

                    for(int n = 0; n < this.amount; ++n) {
                        c = p.hand.getTopCard();
                        p.hand.moveToDiscardPile(c);

                        if (!this.endTurn) {
                            c.triggerOnManualDiscard();
                        }

                        GameActionManager.incrementDiscard(this.endTurn);
                    }

                    p.hand.applyPowers();
                    this.tickDuration();
                    return;
                }

                if (this.isRandom)
                {
                    for(i = 0; i < this.amount; ++i) {
                        c = p.hand.getRandomCard(true);
                        p.hand.moveToDiscardPile(c);
                        c.triggerOnManualDiscard();
                        GameActionManager.incrementDiscard(this.endTurn);
                    }
                }
                else
                {
                    waiting = true;
                    HandCardSelectReordering.saveHandPreOpenScreen();
                    AbstractDungeon.handCardSelectScreen.open(TEXT[0], this.amount, false);

                    AbstractDungeon.player.hand.applyPowers();
                    this.tickDuration();
                    return;
                }
            }
            else //Played by self or is neutral. The other player discards.
            {
                int i;
                if (this.p.otherPlayerHand.size() <= this.amount) {
                    this.amount = this.p.otherPlayerHand.size();
                    i = this.p.otherPlayerHand.size();

                    for(int n = 0; n < i; ++n) {
                        c = p.otherPlayerHand.getTopCard();
                        moveToAltDiscard(p.otherPlayerHand, p.otherPlayerDiscard, c);
                        if (!this.endTurn) {
                            c.triggerOnManualDiscard();
                        }

                        GameActionManager.incrementDiscard(this.endTurn);
                    }

                    p.hand.applyPowers();
                    this.tickDuration();
                    return;
                }

                if (this.isRandom)
                {
                    for(i = 0; i < this.amount; ++i) {
                        c = p.otherPlayerHand.getRandomCard(true);
                        moveToAltDiscard(p.otherPlayerHand, p.otherPlayerDiscard, c);
                        c.triggerOnManualDiscard();
                        GameActionManager.incrementDiscard(this.endTurn);
                    }
                }
                else
                {
                    if (TrackCardSource.useMyEnergy) //Forcing a wait in a neutral case would cause softlock, as both players run this path.
                    {
                        AbstractDungeon.actionManager.addToTop(new ReceiveDiscardCardsAction());
                        AbstractDungeon.actionManager.addToTop(new WaitForSignalAction(otherText[0] + partnerName + otherText[1]));
                    }
                    this.isDone = true;
                    return;
                }
            }
        }

        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            int handIndex = AbstractDungeon.player.hand.size(); //the next card added would be at this index, which makes it the correct index.

            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group)
            {
                p.hand.moveToDiscardPile(c);
                c.triggerOnManualDiscard();
                GameActionManager.incrementDiscard(this.endTurn);

                MultiplayerHelper.sendP2PString(ReceiveSignalCardsAction.signalCardString(handIndex++, AbstractDungeon.player.hand, true));
            }
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
        }

        this.tickDuration();

        if (this.isDone && waiting)
            MultiplayerHelper.sendP2PString("signal");
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString("DiscardAction");
        otherStrings = CardCrawlGame.languagePack.getUIString(makeID("DiscardWait"));
        TEXT = uiStrings.TEXT;
        otherText = otherStrings.TEXT;
        DURATION = Settings.ACTION_DUR_XFAST;
    }
}