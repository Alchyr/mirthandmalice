package mirthandmalice.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDrawPileEffect;
import com.megacrit.cardcrawl.vfx.combat.CardPoofEffect;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.combat.SoulAltOnToDeck;

import java.util.Iterator;

public class ShowCardAndAddToOtherDrawPileEffect extends AbstractGameEffect {
    private static final float EFFECT_DUR = 1.5F;
    private AbstractCard card;
    private static final float PADDING;
    private boolean randomSpot;
    private boolean cardOffset;

    public ShowCardAndAddToOtherDrawPileEffect(AbstractCard srcCard, float x, float y, boolean randomSpot, boolean cardOffset, boolean toBottom) {
        this.randomSpot = false;
        this.cardOffset = false;
        this.card = srcCard.makeStatEquivalentCopy();// 28
        this.cardOffset = cardOffset;// 29
        this.duration = EFFECT_DUR;// 30
        this.randomSpot = randomSpot;// 31
        if (cardOffset) {// 33
            this.identifySpawnLocation(x, y);// 34
        } else {
            this.card.target_x = x;// 36
            this.card.target_y = y;// 37
        }

        AbstractDungeon.effectsQueue.add(new CardPoofEffect(this.card.target_x, this.card.target_y));// 40
        this.card.drawScale = 0.01F;// 41
        this.card.targetDrawScale = 1.0F;// 42
        CardCrawlGame.sound.play("CARD_OBTAIN");// 43
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (toBottom) {// 77
                ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.addToBottom(srcCard);// 78
            } else if (randomSpot) {// 80
                ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.addToRandomSpot(srcCard);// 81
            } else {
                ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.addToTop(srcCard);// 83
            }
        }
        else
        {
            if (toBottom) {// 77
                AbstractDungeon.player.drawPile.addToBottom(srcCard);// 78
            } else if (randomSpot) {// 80
                AbstractDungeon.player.drawPile.addToRandomSpot(srcCard);// 81
            } else {
                AbstractDungeon.player.drawPile.addToTop(srcCard);// 83
            }
        }

        srcCard.triggerWhenCopied();
    }// 53

    public ShowCardAndAddToOtherDrawPileEffect(AbstractCard srcCard, float x, float y, boolean randomSpot, boolean cardOffset) {
        this(srcCard, x, y, randomSpot, cardOffset, false);// 61
    }// 62

    public ShowCardAndAddToOtherDrawPileEffect(AbstractCard srcCard, float x, float y, boolean randomSpot) {
        this(srcCard, x, y, randomSpot, false);// 65
    }// 66

    public ShowCardAndAddToOtherDrawPileEffect(AbstractCard srcCard, boolean randomSpot, boolean toBottom) {
        this.randomSpot = false;// 17
        this.cardOffset = false;// 18
        this.card = srcCard.makeStatEquivalentCopy();// 69
        this.duration = EFFECT_DUR;// 70
        this.randomSpot = randomSpot;// 71
        this.card.target_x = MathUtils.random((float) Settings.WIDTH * 0.1F, (float)Settings.WIDTH * 0.9F);// 72
        this.card.target_y = MathUtils.random((float)Settings.HEIGHT * 0.8F, (float)Settings.HEIGHT * 0.2F);// 73
        AbstractDungeon.effectsQueue.add(new CardPoofEffect(this.card.target_x, this.card.target_y));// 74
        this.card.drawScale = 0.01F;// 75
        this.card.targetDrawScale = 1.0F;// 76
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (toBottom) {// 77
                ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.addToBottom(srcCard);// 78
            } else if (randomSpot) {// 80
                ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.addToRandomSpot(srcCard);// 81
            } else {
                ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.addToTop(srcCard);// 83
            }
        }
        else
        {
            if (toBottom) {// 77
                AbstractDungeon.player.drawPile.addToBottom(srcCard);// 78
            } else if (randomSpot) {// 80
                AbstractDungeon.player.drawPile.addToRandomSpot(srcCard);// 81
            } else {
                AbstractDungeon.player.drawPile.addToTop(srcCard);// 83
            }
        }

        srcCard.triggerWhenCopied();
    }// 86

    private void identifySpawnLocation(float x, float y) {
        int effectCount = 0;// 89
        if (this.cardOffset) {// 90
            effectCount = 1;// 91
        }

        Iterator var4 = AbstractDungeon.effectList.iterator();// 93

        while(var4.hasNext()) {
            AbstractGameEffect e = (AbstractGameEffect)var4.next();
            if (e instanceof ShowCardAndAddToDrawPileEffect || e instanceof ShowCardAndAddToOtherDrawPileEffect) {// 94
                ++effectCount;// 95
            }
        }

        this.card.current_x = x;// 99
        this.card.current_y = y;// 100
        this.card.target_y = (float)Settings.HEIGHT * 0.5F;// 101
        switch(effectCount) {// 103
            case 0:
                this.card.target_x = (float)Settings.WIDTH * 0.5F;// 105
                break;// 106
            case 1:
                this.card.target_x = (float)Settings.WIDTH * 0.5F - PADDING - AbstractCard.IMG_WIDTH;// 108
                break;// 109
            case 2:
                this.card.target_x = (float)Settings.WIDTH * 0.5F + PADDING + AbstractCard.IMG_WIDTH;// 111
                break;// 112
            case 3:
                this.card.target_x = (float)Settings.WIDTH * 0.5F - (PADDING + AbstractCard.IMG_WIDTH) * 2.0F;// 114
                break;// 115
            case 4:
                this.card.target_x = (float)Settings.WIDTH * 0.5F + (PADDING + AbstractCard.IMG_WIDTH) * 2.0F;// 117
                break;// 118
            default:
                this.card.target_x = MathUtils.random((float)Settings.WIDTH * 0.1F, (float)Settings.WIDTH * 0.9F);// 120
                this.card.target_y = MathUtils.random((float)Settings.HEIGHT * 0.2F, (float)Settings.HEIGHT * 0.8F);// 121
        }

    }// 125

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        this.card.update();
        if (this.duration < 0.0F) {
            this.isDone = true;
            this.card.shrink();
            SoulAltOnToDeck.reposition = true;
            AbstractDungeon.getCurrRoom().souls.onToDeck(this.card, this.randomSpot, true);
        }

    }// 136

    public void render(SpriteBatch sb) {
        if (!this.isDone) {// 140
            this.card.render(sb);// 141
        }

    }// 143

    public void dispose() {
    }// 148

    static {
        PADDING = 30.0F * Settings.scale;// 16
    }
}