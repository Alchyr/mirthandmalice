package mirthandmalice.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.CardPoofEffect;

import java.util.Iterator;

//Does not trigger onCopy, which ShowCardAndAddToDiscardEffect does because of patch.
public class AltShowCardAndAddToDiscardEffect extends AbstractGameEffect {
    private static final float EFFECT_DUR = 1.5F;
    private AbstractCard card;
    private static final float PADDING;

    public AltShowCardAndAddToDiscardEffect(AbstractCard srcCard, float x, float y) {
        this.card = srcCard.makeStatEquivalentCopy();// 19
        this.duration = EFFECT_DUR;// 20
        this.card.target_x = x;// 21
        this.card.target_y = y;// 22
        AbstractDungeon.effectsQueue.add(new CardPoofEffect(this.card.target_x, this.card.target_y));// 23
        this.card.drawScale = 0.75F;// 24
        this.card.targetDrawScale = 0.75F;// 25
        CardCrawlGame.sound.play("CARD_OBTAIN");// 26
        AbstractDungeon.player.discardPile.addToTop(srcCard);// 27
    }// 28

    public AltShowCardAndAddToDiscardEffect(AbstractCard card) {
        this.card = card;// 31
        this.duration = EFFECT_DUR;// 32
        this.identifySpawnLocation((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F);// 33
        AbstractDungeon.effectsQueue.add(new CardPoofEffect(card.target_x, card.target_y));// 34
        card.drawScale = 0.01F;// 35
        card.targetDrawScale = 1.0F;// 36
        AbstractDungeon.player.discardPile.addToTop(card);// 37
    }// 38

    private void identifySpawnLocation(float x, float y) {
        int effectCount = 0;// 41
        Iterator var4 = AbstractDungeon.effectList.iterator();// 42

        while(var4.hasNext()) {
            AbstractGameEffect e = (AbstractGameEffect)var4.next();
            if (e instanceof AltShowCardAndAddToDiscardEffect) {// 43
                ++effectCount;// 44
            }
        }

        this.card.target_y = (float)Settings.HEIGHT * 0.5F;// 48
        switch(effectCount) {// 50
            case 0:
                this.card.target_x = (float)Settings.WIDTH * 0.5F;// 52
                break;// 53
            case 1:
                this.card.target_x = (float)Settings.WIDTH * 0.5F - PADDING - AbstractCard.IMG_WIDTH;// 55
                break;// 56
            case 2:
                this.card.target_x = (float)Settings.WIDTH * 0.5F + PADDING + AbstractCard.IMG_WIDTH;// 58
                break;// 59
            case 3:
                this.card.target_x = (float)Settings.WIDTH * 0.5F - (PADDING + AbstractCard.IMG_WIDTH) * 2.0F;// 61
                break;// 62
            case 4:
                this.card.target_x = (float)Settings.WIDTH * 0.5F + (PADDING + AbstractCard.IMG_WIDTH) * 2.0F;// 64
                break;// 65
            default:
                this.card.target_x = MathUtils.random((float)Settings.WIDTH * 0.1F, (float)Settings.WIDTH * 0.9F);// 67
                this.card.target_y = MathUtils.random((float)Settings.HEIGHT * 0.2F, (float)Settings.HEIGHT * 0.8F);// 68
        }

    }// 72

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();// 75
        this.card.update();// 76
        if (this.duration < 0.0F) {// 78
            this.isDone = true;// 79
            this.card.shrink();// 80
            AbstractDungeon.getCurrRoom().souls.discard(this.card, true);// 81
        }

    }// 83

    public void render(SpriteBatch sb) {
        if (!this.isDone) {// 87
            this.card.render(sb);// 88
        }

    }// 90

    public void dispose() {
    }// 95

    static {
        PADDING = 30.0F * Settings.scale;// 16
    }
}