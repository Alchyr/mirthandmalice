package mirthandmalice.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import mirthandmalice.character.MirthAndMalice;

public class ShowSmallCardBrieflyEffect extends AbstractGameEffect {
    private static final float EFFECT_DUR = 2.5F;
    private static final float EFFECT_FAST_DUR = 1.5F;
    private AbstractCard card;
    private static final float PADDING = 30.0F * Settings.scale;
    private static final float VERTICAL_PADDING = 50.0F * Settings.scale;

    private CardLocation cardLocation;

    private enum CardLocation {
        DRAW,
        DISCARD,
        OTHER
    }

    public ShowSmallCardBrieflyEffect(AbstractCard card) {
        this.card = card;
        this.duration = Settings.FAST_MODE ? EFFECT_FAST_DUR : EFFECT_DUR;
        this.startingDuration = Settings.FAST_MODE ? EFFECT_FAST_DUR : EFFECT_DUR;
        card.drawScale = 0.01F;
        this.identifySpawnLocation();
        card.targetDrawScale = 0.6666666666F;
    }

    private void identifySpawnLocation() {
        int effectCount = 0;

        if (AbstractDungeon.player.drawPile.contains(card) || (AbstractDungeon.player instanceof MirthAndMalice && ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.contains(card)))
        {
            //draw
            this.cardLocation = CardLocation.DRAW;

            this.card.target_x = this.card.current_x + 100.0f * Settings.scale;
            this.card.target_y = (float)Settings.HEIGHT * 0.4F;

            for (AbstractGameEffect e : AbstractDungeon.effectList) {
                if (e instanceof ShowSmallCardBrieflyEffect && ((ShowSmallCardBrieflyEffect) e).cardLocation == CardLocation.DRAW) {
                    ++effectCount;
                    ((ShowSmallCardBrieflyEffect) e).card.target_y += effectCount * VERTICAL_PADDING;
                }
            }
        }
        else if (AbstractDungeon.player.discardPile.contains(card) || (AbstractDungeon.player instanceof MirthAndMalice && ((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard.contains(card)))
        {
            //discard
            this.cardLocation = CardLocation.DISCARD;

            this.card.target_x = this.card.current_x - 100.0f * Settings.scale;
            this.card.target_y = (float)Settings.HEIGHT * 0.4F;

            for (AbstractGameEffect e : AbstractDungeon.effectList) {
                if (e instanceof ShowSmallCardBrieflyEffect && ((ShowSmallCardBrieflyEffect) e).cardLocation == CardLocation.DISCARD) {
                    ++effectCount;
                    ((ShowSmallCardBrieflyEffect) e).card.target_y += effectCount * VERTICAL_PADDING;
                }
            }
        }
        else
        {
            //other
            this.cardLocation = CardLocation.OTHER;

            for (AbstractGameEffect e : AbstractDungeon.effectList) {
                if (e instanceof ShowSmallCardBrieflyEffect && ((ShowSmallCardBrieflyEffect) e).cardLocation == CardLocation.OTHER) {
                    ++effectCount;
                }
            }


            this.card.current_x = (float)Settings.WIDTH / 2.0F;
            this.card.current_y = (float)Settings.HEIGHT / 2.0F;
            this.card.target_y = (float)Settings.HEIGHT * 0.5F;
            switch(effectCount) {
                case 0:
                    this.card.target_x = (float)Settings.WIDTH * 0.5F;
                    break;
                case 1:
                    this.card.target_x = (float)Settings.WIDTH * 0.5F - PADDING - AbstractCard.IMG_WIDTH;
                    break;
                case 2:
                    this.card.target_x = (float)Settings.WIDTH * 0.5F + PADDING + AbstractCard.IMG_WIDTH;
                    break;
                case 3:
                    this.card.target_x = (float)Settings.WIDTH * 0.5F - (PADDING + AbstractCard.IMG_WIDTH) * 2.0F;
                    break;
                case 4:
                    this.card.target_x = (float)Settings.WIDTH * 0.5F + (PADDING + AbstractCard.IMG_WIDTH) * 2.0F;
                    break;
                default:
                    this.card.target_x = MathUtils.random((float)Settings.WIDTH * 0.1F, (float)Settings.WIDTH * 0.9F);
                    this.card.target_y = MathUtils.random((float)Settings.HEIGHT * 0.2F, (float)Settings.HEIGHT * 0.8F);
            }
        }
    }

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.55F) {
            this.card.fadingOut = true;
        }

        this.card.update();
        if (this.duration < 0.0F) {
            this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        if (!this.isDone) {
            this.card.render(sb);
        }
    }

    public void dispose() {
    }
}