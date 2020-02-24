package mirthandmalice.actions.character;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
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

import static mirthandmalice.MirthAndMaliceMod.makeID;
import static mirthandmalice.util.MultiplayerHelper.partnerName;

public class OtherPlayerDiscardAction extends AbstractGameAction {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;

    private static final UIStrings otherStrings;
    public static final String[] otherText;

    private AbstractPlayer p;
    private boolean endTurn;
    private static final float DURATION;
    private boolean isRandom;

    public OtherPlayerDiscardAction(AbstractPlayer p, AbstractCreature source, int amount, boolean endTurn) {
        this(p, source, amount, true, endTurn);
    }

    public OtherPlayerDiscardAction(AbstractPlayer p, AbstractCreature source, int amount, boolean isRandom, boolean endTurn) {
        this.p = p;
        this.setValues(target, source, amount);
        this.actionType = ActionType.DISCARD;
        this.endTurn = endTurn;
        this.isRandom = isRandom;
        this.duration = DURATION;
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
            if (TrackCardSource.useMyEnergy && AbstractDungeon.player instanceof MirthAndMalice) //triggered by self.
            {
                AbstractDungeon.actionManager.addToTop(new ReceiveDiscardCardsAction());
                AbstractDungeon.actionManager.addToTop(new WaitForSignalAction(otherText[0] + partnerName + otherText[1]));
                this.isDone = true;
                return;
            }

            AbstractCard c;

            if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
                this.isDone = true;
                return;
            }


            int i;
            if (this.p.hand.size() <= this.amount) {
                this.amount = this.p.hand.size();
                i = this.p.hand.size();

                for(int n = 0; n < i; ++n) {
                    c = p.hand.getTopCard();
                    p.hand.moveToDiscardPile(c);
                    if (!this.endTurn) {
                        c.triggerOnManualDiscard();
                    }

                    GameActionManager.incrementDiscard(this.endTurn);
                }

                p.hand.applyPowers();
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
                if (AbstractDungeon.player.hand.isEmpty()) {
                    this.isDone = true;
                    MultiplayerHelper.sendP2PString("signal");
                    return;
                }

                HandCardSelectReordering.saveHandPreOpenScreen();
                AbstractDungeon.handCardSelectScreen.open(TEXT[0], this.amount, false);

                AbstractDungeon.player.hand.applyPowers();
                this.tickDuration();
                return;
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

        if (this.isDone)
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