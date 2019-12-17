package mirthandmalice.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import mirthandmalice.ui.OtherDiscardPilePanel;

public class AltDiscardGlowEffect extends AbstractGameEffect {
    private static final float EFFECT_DUR = 0.2F;
    private float effectDuration;
    private float x;
    private float y;
    private float vY;
    private float rotator;
    private float scaleJitter = 1.0F;
    private static final float SCALE_JITTER_AMT = 0.1F;
    private Color shadowColor;
    private TextureAtlas.AtlasRegion img;
    private boolean isAdditive;

    public AltDiscardGlowEffect(boolean isAbove) {
        this.shadowColor = Color.BLACK.cpy();
        this.img = this.getImg();
        this.setPosition(isAbove);
        this.x -= (float)(this.img.packedWidth / 2);
        this.y -= (float)(this.img.packedHeight / 2);
        this.y += OtherDiscardPilePanel.ALT_DISCARD_OFFSET;
        this.effectDuration = MathUtils.random(0.4F, 0.9F);
        this.duration = this.effectDuration;
        this.startingDuration = this.effectDuration;
        this.scaleJitter = MathUtils.random(this.scaleJitter - 0.1F, this.scaleJitter + 0.1F);
        this.vY = MathUtils.random(30.0F * Settings.scale, 60.0F * Settings.scale);
        this.color = Settings.DISCARD_COLOR.cpy();
        Color var10000 = this.color;
        var10000.r -= MathUtils.random(0.0F, 0.1F);
        var10000 = this.color;
        var10000.g += MathUtils.random(0.0F, 0.1F);
        var10000 = this.color;
        var10000.b += MathUtils.random(0.0F, 0.1F);
        this.isAdditive = MathUtils.randomBoolean();
        this.rotator = MathUtils.random(-180.0F, 180.0F);
    }

    private TextureAtlas.AtlasRegion getImg() {
        int roll = MathUtils.random(0, 5);
        switch(roll) {
            case 0:
                return ImageMaster.DECK_GLOW_1;
            case 1:
                return ImageMaster.DECK_GLOW_2;
            case 2:
                return ImageMaster.DECK_GLOW_3;
            case 3:
                return ImageMaster.DECK_GLOW_4;
            case 4:
                return ImageMaster.DECK_GLOW_5;
            default:
                return ImageMaster.DECK_GLOW_6;
        }
    }

    private void setPosition(boolean isAbove) {
        int roll = MathUtils.random(0, 9);
        if (isAbove) {
            switch(roll) {
                case 0:
                    this.x = 1886.0F * Settings.scale;
                    this.y = 86.0F * Settings.scale;
                    return;
                case 1:
                    this.x = 1883.0F * Settings.scale;
                    this.y = 80.0F * Settings.scale;
                    return;
                case 2:
                    this.x = 1881.0F * Settings.scale;
                    this.y = 67.0F * Settings.scale;
                    return;
                case 3:
                    this.x = 1876.0F * Settings.scale;
                    this.y = 54.0F * Settings.scale;
                    return;
                case 4:
                    this.x = 1873.0F * Settings.scale;
                    this.y = 45.0F * Settings.scale;
                    return;
                case 5:
                    this.x = 1865.0F * Settings.scale;
                    this.y = 36.0F * Settings.scale;
                    return;
                case 6:
                    this.x = 1849.0F * Settings.scale;
                    this.y = 32.0F * Settings.scale;
                    return;
                case 7:
                    this.x = 1841.0F * Settings.scale;
                    this.y = 36.0F * Settings.scale;
                    return;
                case 8:
                    this.x = 1830.0F * Settings.scale;
                    this.y = 36.0F * Settings.scale;
                    return;
                default:
                    this.x = 1819.0F * Settings.scale;
                    this.y = 43.0F * Settings.scale;
            }
        } else {
            switch(roll) {
                case 0:
                    this.x = 1810.0F * Settings.scale;
                    this.y = 84.0F * Settings.scale;
                    return;
                case 1:
                    this.x = 1820.0F * Settings.scale;
                    this.y = 88.0F * Settings.scale;
                    return;
                case 2:
                    this.x = 1830.0F * Settings.scale;
                    this.y = 94.0F * Settings.scale;
                    return;
                case 3:
                    this.x = 1834.0F * Settings.scale;
                    this.y = 96.0F * Settings.scale;
                    return;
                case 4:
                    this.x = 1837.0F * Settings.scale;
                    this.y = 96.0F * Settings.scale;
                    return;
                case 5:
                    this.x = 1841.0F * Settings.scale;
                    this.y = 98.0F * Settings.scale;
                    return;
                case 6:
                    this.x = 1854.0F * Settings.scale;
                    this.y = 99.0F * Settings.scale;
                    return;
                case 7:
                    this.x = 1859.0F * Settings.scale;
                    this.y = 91.0F * Settings.scale;
                    return;
                case 8:
                    this.x = 1871.0F * Settings.scale;
                    this.y = 87.0F * Settings.scale;
                    return;
                default:
                    this.x = 1877.0F * Settings.scale;
                    this.y = 84.0F * Settings.scale;
            }
        }
    }

    public void update() {
        this.rotation += this.rotator * Gdx.graphics.getDeltaTime();
        this.y += this.vY * Gdx.graphics.getDeltaTime();
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.1F) {
            this.scale = Settings.scale * this.duration / this.effectDuration * 2.0F + Settings.scale / 2.0F;
        }

        if (this.duration < 0.25F) {
            this.color.a = this.duration * 4.0F;
        }

        if (this.duration < 0.0F) {
            this.isDone = true;
        }

        this.shadowColor.a = this.color.a / 2.0F;
    }

    public void render(SpriteBatch sb, float x2, float y2) {
        if (this.isAdditive) {
            sb.setBlendFunction(770, 1);
        }

        sb.setColor(this.color);
        sb.draw(this.img, this.x + x2, this.y + y2, (float)this.img.packedWidth / 2.0F, (float)this.img.packedHeight / 2.0F, (float)this.img.packedWidth, (float)this.img.packedHeight, this.scale * this.scaleJitter, this.scale * this.scaleJitter, this.rotation);
        if (this.isAdditive) {
            sb.setBlendFunction(770, 771);
        }

    }

    public void dispose() {
    }

    public void render(SpriteBatch sb) {
    }
}