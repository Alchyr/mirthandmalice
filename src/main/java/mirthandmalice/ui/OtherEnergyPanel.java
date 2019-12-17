package mirthandmalice.ui;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.panels.AbstractPanel;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.RefreshEnergyEffect;
import mirthandmalice.character.MirthAndMalice;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class OtherEnergyPanel extends AbstractPanel {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("EnergyPanel"));
    private static final int RAW_W = 256;
    private static final Color ENERGY_TEXT_COLOR;
    public static float fontScale;
    public static final float FONT_POP_SCALE = 2.0F;
    public static int totalCount;
    private Hitbox tipHitbox;
    private Texture gainEnergyImg;
    private float energyVfxAngle;
    private float energyVfxScale;
    private Color energyVfxColor;
    public static float energyVfxTimer;
    public static final float ENERGY_VFX_TIME = 2.0F;
    private static final float VFX_ROTATE_SPEED = -30.0F;

    public OtherEnergyPanel() {
        super(Settings.WIDTH - 198.0F * Settings.scale, Settings.HEIGHT / 2.0f, Settings.WIDTH + 480.0F * Settings.scale, Settings.HEIGHT / 2.0f, 12.0F * Settings.scale, -12.0F * Settings.scale, null, true);
        this.tipHitbox = new Hitbox(0.0F, 0.0F, 120.0F * Settings.scale, 120.0F * Settings.scale);
        this.energyVfxAngle = 0.0F;
        this.energyVfxScale = Settings.scale;
        this.energyVfxColor = Color.WHITE.cpy();
        if (AbstractDungeon.player instanceof MirthAndMalice)
            this.gainEnergyImg = ((MirthAndMalice) AbstractDungeon.player).getOtherEnergyImage();
        else
            this.gainEnergyImg = AbstractDungeon.player.getEnergyImage();
    }

    public static void setEnergy(int energy) {
        totalCount = energy;
        RefreshEnergyEffect e = new RefreshEnergyEffect();
        TextureAtlas.AtlasRegion img = (TextureAtlas.AtlasRegion)ReflectionHacks.getPrivate(e, RefreshEnergyEffect.class, "img");
        ReflectionHacks.setPrivate(e, RefreshEnergyEffect.class, "x", Settings.WIDTH - 198.0F * Settings.scale - (float)img.packedWidth / 2.0F);
        AbstractDungeon.effectsQueue.add(e);

        energyVfxTimer = 2.0F;
        fontScale = 2.0F;
    }

    public static void addEnergy(int e) {
        totalCount += e;
        if (totalCount >= 9) {
            UnlockTracker.unlockAchievement("ADRENALINE");
        }

        if (totalCount > 999) {
            totalCount = 999;
        }

        AbstractDungeon.effectsQueue.add(new RefreshEnergyEffect());
        fontScale = 2.0F;
        energyVfxTimer = 2.0F;
    }

    public static void useEnergy(int e) {
        totalCount -= e;
        if (totalCount < 0) {
            totalCount = 0;
        }

        if (e != 0) {
            fontScale = 2.0F;
        }

    }

    public void update() {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            ((MirthAndMalice) AbstractDungeon.player).updateOtherOrb(totalCount);
            this.updateVfx();
            if (fontScale != 1.0F) {
                fontScale = MathHelper.scaleLerpSnap(fontScale, 1.0F);
            }

            this.tipHitbox.update();
            if (this.tipHitbox.hovered && !AbstractDungeon.isScreenUp) {
                AbstractDungeon.overlayMenu.hoveredTip = true;
            }

            if (Settings.isDebug) {
                if (InputHelper.scrolledDown) {
                    addEnergy(1);
                } else if (InputHelper.scrolledUp && totalCount > 0) {
                    useEnergy(1);
                }
            }
        }
    }

    private void updateVfx() {
        if (energyVfxTimer != 0.0F) {
            this.energyVfxColor.a = Interpolation.exp10In.apply(0.5F, 0.0F, 1.0F - energyVfxTimer / 2.0F);
            this.energyVfxAngle += Gdx.graphics.getDeltaTime() * -30.0F;
            this.energyVfxScale = Settings.scale * Interpolation.exp10In.apply(1.0F, 0.1F, 1.0F - energyVfxTimer / 2.0F);
            energyVfxTimer -= Gdx.graphics.getDeltaTime();
            if (energyVfxTimer < 0.0F) {
                energyVfxTimer = 0.0F;
                this.energyVfxColor.a = 0.0F;
            }
        }

    }

    public void render(SpriteBatch sb) {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            MirthAndMalice p = (MirthAndMalice)AbstractDungeon.player;
            this.tipHitbox.move(this.current_x, this.current_y);
            p.renderOtherOrb(sb, false, this.current_x, this.current_y);
            this.renderVfx(sb);
            String energyMsg = totalCount + "/" + AbstractDungeon.player.energy.energy;
            p.getOtherEnergyNumFont().getData().setScale(fontScale);
            FontHelper.renderFontCentered(sb, p.getOtherEnergyNumFont(), energyMsg, this.current_x, this.current_y, ENERGY_TEXT_COLOR);
            this.tipHitbox.render(sb);
            if (this.tipHitbox.hovered && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.isScreenUp) {
                TipHelper.renderGenericTip(Settings.WIDTH - 200.0F * Settings.scale, 380.0F * Settings.scale, EnergyPanel.LABEL[0], p.getOtherPlayerName() + uiStrings.TEXT[0]);
            }
        }
    }

    private void renderVfx(SpriteBatch sb) {
        if (energyVfxTimer != 0.0F) {
            sb.setBlendFunction(770, 1);
            sb.setColor(this.energyVfxColor);
            sb.draw(this.gainEnergyImg, this.current_x - 128.0F, this.current_y - 128.0F, 128.0F, 128.0F, 256.0F, 256.0F, this.energyVfxScale, this.energyVfxScale, -this.energyVfxAngle + 50.0F, 0, 0, 256, 256, true, false);
            sb.draw(this.gainEnergyImg, this.current_x - 128.0F, this.current_y - 128.0F, 128.0F, 128.0F, 256.0F, 256.0F, this.energyVfxScale, this.energyVfxScale, this.energyVfxAngle, 0, 0, 256, 256, false, false);
            sb.setBlendFunction(770, 771);
        }
    }

    public static int getCurrentEnergy() {
        return AbstractDungeon.player == null ? 0 : totalCount;
    }

    static {
        ENERGY_TEXT_COLOR = new Color(1.0F, 1.0F, 0.86F, 1.0F);
        fontScale = 1.0F;
        totalCount = 0;
        energyVfxTimer = 0.0F;
    }
}
