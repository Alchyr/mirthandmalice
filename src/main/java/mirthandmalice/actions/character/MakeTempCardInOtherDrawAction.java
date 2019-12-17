package mirthandmalice.actions.character;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import mirthandmalice.effects.ShowCardAndAddToOtherDrawPileEffect;

public class MakeTempCardInOtherDrawAction extends AbstractGameAction {
    private AbstractCard cardToMake;
    private boolean randomSpot;
    private boolean autoPosition;
    private boolean toBottom;
    private float x;
    private float y;

    public MakeTempCardInOtherDrawAction(AbstractCard card, int amount, boolean randomSpot, boolean autoPosition, boolean toBottom, float cardX, float cardY) {
        UnlockTracker.markCardAsSeen(card.cardID);// 26
        this.setValues(this.target, this.source, amount);// 27
        this.actionType = ActionType.CARD_MANIPULATION;// 28
        this.duration = 0.5F;// 29
        this.cardToMake = card;// 30
        this.randomSpot = randomSpot;// 31
        this.autoPosition = autoPosition;// 32
        this.toBottom = toBottom;// 33
        this.x = cardX;// 34
        this.y = cardY;// 35
    }// 36

    public MakeTempCardInOtherDrawAction(AbstractCard card, int amount, boolean randomSpot, boolean autoPosition, boolean toBottom) {
        this(card, amount, randomSpot, autoPosition, toBottom, (float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F);// 44
    }// 45

    public MakeTempCardInOtherDrawAction(AbstractCard card, int amount, boolean shuffleInto, boolean autoPosition) {
        this(card, amount, shuffleInto, autoPosition, false);// 48
    }// 49

    public void update() {
        if (this.duration == 0.5F) {
            int i;
            if (this.amount < 6) {
                for(i = 0; i < this.amount; ++i) {
                    AbstractDungeon.effectList.add(new ShowCardAndAddToOtherDrawPileEffect(cardToMake, this.x, this.y, this.randomSpot, this.autoPosition, this.toBottom));
                }
            } else {
                for(i = 0; i < this.amount; ++i) {
                    AbstractDungeon.effectList.add(new ShowCardAndAddToOtherDrawPileEffect(cardToMake, this.randomSpot, this.toBottom));
                }
            }

            this.duration -= Gdx.graphics.getDeltaTime();
        }

        this.tickDuration();
    }
}
