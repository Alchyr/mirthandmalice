package mirthandmalice.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class ImageAboveCreatureEffect extends AbstractGameEffect {
    private static final float TEXT_DURATION = 1.5F;
    private static final float STARTING_OFFSET_Y;
    private static final float TARGET_OFFSET_Y;
    private static final float LERP_RATE = 5.0F;

    private float x;
    private float y;

    private float width, height, xOffset, yOffset;
    private int srcWidth, srcHeight;

    private float offsetY;
    private Color outlineColor = new Color(0.0F, 0.0F, 0.0F, 0.0F);
    private Color shineColor = new Color(1.0F, 1.0F, 1.0F, 0.0F);

    private Texture img;

    public ImageAboveCreatureEffect(float x, float y, Texture img) {
        this.duration = TEXT_DURATION;
        this.startingDuration = TEXT_DURATION;

        this.img = img;

        this.width = img.getWidth();
        this.height = img.getHeight();
        this.xOffset = width / 2.0f;
        this.yOffset = height / 2.0f;

        this.srcWidth = (int) this.width;
        this.srcHeight = (int) this.height;

        this.x = x;
        this.y = y;
        this.color = Color.WHITE.cpy();
        this.offsetY = STARTING_OFFSET_Y;
        this.scale = Settings.scale;
    }

    public void update() {
        if (this.duration > 1.0F) {
            this.color.a = Interpolation.exp5In.apply(1.0F, 0.0F, (this.duration - 1.0F) * 2.0F);
        }

        super.update();
        if (AbstractDungeon.player.chosenClass == AbstractPlayer.PlayerClass.DEFECT) {
            this.offsetY = MathUtils.lerp(this.offsetY, TARGET_OFFSET_Y + 80.0F * Settings.scale, Gdx.graphics.getDeltaTime() * LERP_RATE);
        } else {
            this.offsetY = MathUtils.lerp(this.offsetY, TARGET_OFFSET_Y, Gdx.graphics.getDeltaTime() * LERP_RATE);
        }

        this.y += Gdx.graphics.getDeltaTime() * 12.0F * Settings.scale;
    }

    public void render(SpriteBatch sb) {
        this.outlineColor.a = this.color.a / 2.0F;
        sb.setColor(this.color);
        sb.draw(img, this.x - xOffset, this.y - yOffset + this.offsetY, xOffset, yOffset, width, height, this.scale * (2.5F - this.duration), this.scale * (2.5F - this.duration), this.rotation, 0, 0, srcWidth, srcHeight, false, false);// 76
        sb.setBlendFunction(770, 1);
        this.shineColor.a = this.color.a / 4.0F;
        sb.setColor(this.shineColor);
        sb.draw(img, this.x - xOffset, this.y - yOffset + this.offsetY, xOffset, yOffset, width, height, this.scale * (2.7F - this.duration), this.scale * (2.7F - this.duration), this.rotation, 0, 0, srcWidth, srcHeight, false, false);// 96
        sb.draw(img, this.x - xOffset, this.y - yOffset + this.offsetY, xOffset, yOffset, width, height, this.scale * (3.0F - this.duration), this.scale * (3.0F - this.duration), this.rotation, 0, 0, srcWidth, srcHeight, false, false);// 114
        sb.setBlendFunction(770, 771);
    }

    public void dispose() {
    }

    static {
        STARTING_OFFSET_Y = 0.0F * Settings.scale;
        TARGET_OFFSET_Y = 60.0F * Settings.scale;
    }
}