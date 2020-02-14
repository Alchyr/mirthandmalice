package mirthandmalice.actions.character;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import mirthandmalice.abstracts.ReceiveSignalCardsAction;
import mirthandmalice.actions.cards.ReceiveRevisionCardsAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.effects.ShowCardAndAddToOtherDiscardEffect;
import mirthandmalice.effects.ShowCardAndAddToOtherHandEffect;
import mirthandmalice.patch.combat.HandCardSelectReordering;
import mirthandmalice.patch.combat.SoulAltDiscard;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.MultiplayerHelper;

import static mirthandmalice.util.MultiplayerHelper.partnerName;

public class OtherPlayerDiscardAction extends AbstractGameAction {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;

    private static final UIStrings otherStrings;
    public static final String[] otherText;

    private MirthAndMalice p;
    private boolean endTurn;
    private AbstractCard toDiscard = null;
    private static final float DURATION;
    private boolean isRandom;
    private boolean mustSignal = false;

    public OtherPlayerDiscardAction(MirthAndMalice target, AbstractCreature source, int amount, boolean endTurn) {
        this(target, source, amount, true, endTurn);
    }

    public OtherPlayerDiscardAction(MirthAndMalice target, AbstractCreature source, int amount, boolean isRandom, boolean endTurn) {
        this.p = target;
        this.setValues(target, source, amount);
        this.actionType = ActionType.DISCARD;
        this.endTurn = endTurn;
        this.isRandom = isRandom;
        this.duration = DURATION;
    }

    public OtherPlayerDiscardAction(MirthAndMalice target, AbstractCard toDiscard) {
        this.p = target;
        this.actionType = ActionType.DISCARD;
        this.endTurn = false;
        this.duration = DURATION;
        this.toDiscard = toDiscard;
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

            if (this.toDiscard != null)
            {
                moveToAltDiscard(p.otherPlayerHand, p.otherPlayerDiscard, toDiscard);
                toDiscard.triggerOnManualDiscard();
                GameActionManager.incrementDiscard(false);

                p.otherPlayerHand.applyPowers();
                p.hand.applyPowers();

                this.tickDuration();
                return;
            }

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

                p.otherPlayerHand.applyPowers();
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
                if (TrackCardSource.useOtherEnergy && AbstractDungeon.player instanceof MirthAndMalice) //played by other player.
                {
                    AbstractDungeon.actionManager.addToTop(new ReceiveDiscardCardsAction());
                    AbstractDungeon.actionManager.addToTop(new WaitForSignalAction(otherText[0] + partnerName + otherText[1]));
                    this.isDone = true;
                    return;
                }

                if (AbstractDungeon.player.hand.isEmpty()) {
                    this.isDone = true;
                    MultiplayerHelper.sendP2PString("signal");
                    return;
                }

                HandCardSelectReordering.saveHandPreOpenScreen();
                AbstractDungeon.handCardSelectScreen.open(TEXT[0], this.amount, false);

                mustSignal = true;

                AbstractDungeon.player.hand.applyPowers();
                this.tickDuration();
                return;
            }
        }

        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            int handIndex = AbstractDungeon.player.hand.size(); //the next card added would be at this index, which makes it the correct index.

            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group)
            {
                moveToAltDiscard(p.otherPlayerHand, p.otherPlayerDiscard, c);
                c.triggerOnManualDiscard();
                GameActionManager.incrementDiscard(this.endTurn);

                MultiplayerHelper.sendP2PString(ReceiveSignalCardsAction.signalCardString(handIndex++, AbstractDungeon.player.hand, true));
            }
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
        }

        this.tickDuration();

        if (this.isDone && mustSignal)
            MultiplayerHelper.sendP2PString("signal");
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString("DiscardAction");
        otherStrings = CardCrawlGame.languagePack.getUIString("DiscardWait");
        TEXT = uiStrings.TEXT;
        otherText = otherStrings.TEXT;
        DURATION = Settings.ACTION_DUR_XFAST;
    }
}