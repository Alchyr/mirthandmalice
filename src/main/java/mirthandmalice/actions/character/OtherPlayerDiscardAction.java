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
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.combat.SoulAltDiscard;

public class OtherPlayerDiscardAction extends AbstractGameAction {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;
    private MirthAndMalice p;
    private boolean endTurn;
    private AbstractCard toDiscard = null;
    private static final float DURATION;

    public OtherPlayerDiscardAction(MirthAndMalice target, AbstractCreature source, int amount, boolean endTurn) {
        this.p = target;
        this.setValues(target, source, amount);
        this.actionType = ActionType.DISCARD;
        this.endTurn = endTurn;
        this.duration = DURATION;
    }

    public OtherPlayerDiscardAction(MirthAndMalice target, AbstractCard toDiscard) {
        this.p = target;
        this.actionType = ActionType.DISCARD;
        this.endTurn = false;
        this.duration = DURATION;
        this.toDiscard = toDiscard;
    }

    private void moveToAltDiscard(CardGroup source, CardGroup discardPile, AbstractCard c)
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
        AbstractCard c;
        if (this.duration == DURATION) {
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

            for(i = 0; i < this.amount; ++i) {
                c = p.otherPlayerHand.getRandomCard(true);
                moveToAltDiscard(p.otherPlayerHand, p.otherPlayerDiscard, c);
                c.triggerOnManualDiscard();
                GameActionManager.incrementDiscard(this.endTurn);
            }
        }

        this.tickDuration();
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString("DiscardAction");
        TEXT = uiStrings.TEXT;
        DURATION = Settings.ACTION_DUR_XFAST;
    }
}