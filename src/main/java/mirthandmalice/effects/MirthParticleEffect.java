package mirthandmalice.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class MirthParticleEffect extends AbstractGameEffect {
    private static final float MIN_OFFSET = 90.0f * Settings.scale;
    private static final float MAX_OFFSET = 140.0f * Settings.scale;

    private float x;
    private float y;
    private float vY;
    private float dur_div2;
    private TextureAtlas.AtlasRegion img;

    public MirthParticleEffect() {
        this.img = ImageMaster.GLOW_SPARK;
        this.duration = MathUtils.random(1.3F, 1.8F);
        this.scale = MathUtils.random(0.4F, 0.7F) * Settings.scale;
        this.dur_div2 = this.duration / 2.0F;
        float v = MathUtils.random(0.45F, 0.6F);
        this.color = new Color(MathUtils.random(0.8F, 1.0F), v, v, 0.0F);

        float distance = MathUtils.random(MIN_OFFSET, MAX_OFFSET) * (MathUtils.randomBoolean() ? 1 : -1);
        this.rotation = MathUtils.random(-MathUtils.PI, MathUtils.PI);

        this.x = AbstractDungeon.player.hb.cX + MathUtils.cos(rotation) * distance;
        this.y = AbstractDungeon.player.hb.cY + MathUtils.sin(rotation) * distance;

        this.x -= (float)this.img.packedWidth / 2.0F;
        this.y -= (float)this.img.packedHeight / 2.0F;
        this.renderBehind = MathUtils.randomBoolean(0.2F + (this.scale - 0.4F));
    }

    public void update() {
        if (this.duration > this.dur_div2) {
            this.color.a = Interpolation.fade.apply(1.0F, 0.0F, (this.duration - this.dur_div2) / this.dur_div2);
        } else {
            this.color.a = Interpolation.fade.apply(0.0F, 1.0F, this.duration / this.dur_div2);
        }

        this.vY += Gdx.graphics.getDeltaTime() * 40.0F * Settings.scale;
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.setBlendFunction(770, 1);
        sb.draw(this.img, this.x, this.y + this.vY, (float)this.img.packedWidth / 2.0F, (float)this.img.packedHeight / 2.0F, (float)this.img.packedWidth, (float)this.img.packedHeight, this.scale * 0.8F, (0.1F + (this.dur_div2 * 2.0F - this.duration) * 2.0F * this.scale) * Settings.scale, this.rotation);
        sb.setBlendFunction(770, 771);
    }

    public void dispose() {
    }
}