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

public class MaliceParticleEffect extends AbstractGameEffect {
    private static final float MIN_OFFSET = 90.0f * Settings.scale;
    private static final float MAX_OFFSET = 140.0f * Settings.scale;


    private float x;
    private float y;
    private float vY;
    private float dur_div2;
    private TextureAtlas.AtlasRegion img;

    public MaliceParticleEffect() {
        this.scale = Settings.scale;
        this.img = ImageMaster.EYE_ANIM_0;
        this.scale = MathUtils.random(1.0F, 1.5F);
        this.startingDuration = this.scale + 0.8F;
        this.duration = this.startingDuration;
        this.scale *= Settings.scale;
        this.dur_div2 = this.duration / 2.0F;
        this.color = new Color(MathUtils.random(0.3F, 0.6F), MathUtils.random(0.4F, 0.7F), MathUtils.random(0.3F, 0.6F), 0.0F);

        float distance = MathUtils.random(MIN_OFFSET, MAX_OFFSET) * (MathUtils.randomBoolean() ? 1 : -1);
        this.rotation = MathUtils.random(0, MathUtils.PI2);

        this.x = AbstractDungeon.player.hb.cX + MathUtils.cos(rotation) * distance;
        this.y = AbstractDungeon.player.hb.cY + MathUtils.sin(rotation) * distance;
        this.renderBehind = MathUtils.randomBoolean();

        this.x -= (float)this.img.packedWidth / 2.0F;
        this.y -= (float)this.img.packedHeight / 2.0F;
    }

    public void update() {
        if (this.duration > this.dur_div2) {// 50
            this.color.a = Interpolation.fade.apply(1.0F, 0.0F, (this.duration - this.dur_div2) / this.dur_div2);
        } else {
            this.color.a = Interpolation.fade.apply(0.0F, 1.0F, this.duration / this.dur_div2);
        }

        if (this.duration > this.startingDuration * 0.85F) {
            this.vY = 12.0F * Settings.scale;
            this.img = ImageMaster.EYE_ANIM_0;
        } else if (this.duration > this.startingDuration * 0.8F) {
            this.vY = 8.0F * Settings.scale;
            this.img = ImageMaster.EYE_ANIM_1;
        } else if (this.duration > this.startingDuration * 0.75F) {
            this.vY = 4.0F * Settings.scale;
            this.img = ImageMaster.EYE_ANIM_2;
        } else if (this.duration > this.startingDuration * 0.7F) {
            this.vY = 3.0F * Settings.scale;
            this.img = ImageMaster.EYE_ANIM_3;
        } else if (this.duration > this.startingDuration * 0.65F) {
            this.img = ImageMaster.EYE_ANIM_4;
        } else if (this.duration > this.startingDuration * 0.6F) {
            this.img = ImageMaster.EYE_ANIM_5;
        } else if (this.duration > this.startingDuration * 0.55F) {
            this.img = ImageMaster.EYE_ANIM_6;
        } else if (this.duration > this.startingDuration * 0.38F) {
            this.img = ImageMaster.EYE_ANIM_5;
        } else if (this.duration > this.startingDuration * 0.3F) {
            this.img = ImageMaster.EYE_ANIM_4;
        } else if (this.duration > this.startingDuration * 0.25F) {
            this.vY = 3.0F * Settings.scale;
            this.img = ImageMaster.EYE_ANIM_3;
        } else if (this.duration > this.startingDuration * 0.2F) {
            this.vY = 4.0F * Settings.scale;
            this.img = ImageMaster.EYE_ANIM_2;
        } else if (this.duration > this.startingDuration * 0.15F) {
            this.vY = 8.0F * Settings.scale;
            this.img = ImageMaster.EYE_ANIM_1;
        } else {
            this.vY = 12.0F * Settings.scale;
            this.img = ImageMaster.EYE_ANIM_0;
        }

        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
        }

    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.setBlendFunction(770, 1);
        sb.draw(this.img, this.x, this.y + this.vY, (float)this.img.packedWidth / 2.0F, (float)this.img.packedHeight / 2.0F, (float)this.img.packedWidth, (float)this.img.packedHeight, this.scale, this.scale, this.rotation);
        sb.setBlendFunction(770, 771);
    }

    public void dispose() {
    }
}