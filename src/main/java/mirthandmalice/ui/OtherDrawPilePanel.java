package mirthandmalice.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.FrozenEye;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.panels.AbstractPanel;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BobEffect;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.effects.AltGameDeckGlowEffect;
import mirthandmalice.patch.screens.AltGameDeckView;
import mirthandmalice.util.TextureLoader;

import java.util.ArrayList;
import java.util.Iterator;

import static mirthandmalice.MirthAndMaliceMod.assetPath;
import static mirthandmalice.MirthAndMaliceMod.makeID;

public class OtherDrawPilePanel extends AbstractPanel {
    private static final UIStrings uiStrings;

    private static final Texture deckTexture = TextureLoader.getTexture(assetPath("img/ui/otherDeckBase.png"));

    public static final float OTHER_DRAW_OFFSET = 148.0F * Settings.scale;

    public static final String[] TEXT;
    private float scale = 1.0F;
    private static final float COUNT_CIRCLE_W;
    private static final float OTHER_DECK_X;
    private static final float OTHER_DECK_Y;
    private static final float COUNT_X;
    private static final float COUNT_Y;
    private static final float COUNT_OFFSET_X;
    private static final float COUNT_OFFSET_Y;
    private Color glowColor;
    private float glowAlpha;
    private GlyphLayout gl;
    private static final float DECK_TIP_X;
    private static final float DECK_TIP_Y;
    private BobEffect bob;
    private ArrayList<AltGameDeckGlowEffect> vfxBelow;
    private static final float HITBOX_W;
    private static final float HITBOX_W2;
    private Hitbox hb;
    private Hitbox bannerHb;

    public OtherDrawPilePanel() {
        super(0.0F, 0.0F * Settings.scale, -300.0F * Settings.scale, -300.0F * Settings.scale, null, true);
        this.glowColor = Color.WHITE.cpy();
        this.glowAlpha = 0.0F;
        this.gl = new GlyphLayout();
        this.bob = new BobEffect(1.0F);
        this.vfxBelow = new ArrayList<>();
        this.hb = new Hitbox(0.0F, OTHER_DRAW_OFFSET, HITBOX_W, HITBOX_W);
        this.bannerHb = new Hitbox(0.0F, OTHER_DRAW_OFFSET, HITBOX_W2, HITBOX_W);
    }

    public void updatePositions(boolean wasOpen) {
        super.updatePositions();
        this.bob.update();
        this.updateVfx();
        if (!this.isHidden) {
            this.hb.update();
            this.bannerHb.update();
            this.updatePop();
        }

        if (this.hb.hovered && (!wasOpen || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GAME_DECK_VIEW || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.HAND_SELECT || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.CARD_REWARD && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT)) {
            AbstractDungeon.overlayMenu.hoveredTip = true;
            if (InputHelper.justClickedLeft) {
                this.hb.clickStarted = true;
            }
        }

        if (this.hb.clicked && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GAME_DECK_VIEW) {
            this.hb.clicked = false;
            this.hb.hovered = false;
            this.bannerHb.hovered = false;
            CardCrawlGame.sound.play("DECK_CLOSE");
            if (AbstractDungeon.previousScreen == AbstractDungeon.CurrentScreen.GAME_DECK_VIEW) {
                AbstractDungeon.previousScreen = null;
            }

            AbstractDungeon.closeCurrentScreen();
        } else {
            this.glowAlpha += Gdx.graphics.getDeltaTime() * 3.0F;
            if (this.glowAlpha < 0.0F) {
                this.glowAlpha *= -1.0F;
            }

            float tmp = MathUtils.cos(this.glowAlpha);
            if (tmp < 0.0F) {
                this.glowColor.a = -tmp / 2.0F;
            } else {
                this.glowColor.a = tmp / 2.0F;
            }

            if (this.hb.clicked && AbstractDungeon.overlayMenu.combatPanelsShown && AbstractDungeon.getMonsters() != null && !AbstractDungeon.getMonsters().areMonstersDead() && !AbstractDungeon.player.isDead) {
                this.hb.clicked = false;
                this.hb.hovered = false;
                this.bannerHb.hovered = false;
                AbstractDungeon.dynamicBanner.hide();
                if (AbstractDungeon.isScreenUp) {
                    if (AbstractDungeon.previousScreen == null) {
                        AbstractDungeon.previousScreen = AbstractDungeon.screen;
                    }
                } else {
                    AbstractDungeon.previousScreen = null;
                }

                this.openOtherDrawPile();
            }

        }
    }

    private void openOtherDrawPile() {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            MirthAndMalice p = (MirthAndMalice)AbstractDungeon.player;
            if (!p.otherPlayerDraw.isEmpty()) {
                AltGameDeckView.altGroup = p.otherPlayerDraw;
                AbstractDungeon.gameDeckViewScreen.open();
            } else {
                AbstractDungeon.effectList.add(new ThoughtBubble(p.dialogX, p.dialogY, 3.0F, p.getOtherPlayerName() + TEXT[0], true));
            }
        }

        this.hb.hovered = false;
        InputHelper.justClickedLeft = false;
    }

    private void updateVfx() {
        Iterator i = this.vfxBelow.iterator();

        while(i.hasNext()) {
            AbstractGameEffect e = (AbstractGameEffect)i.next();
            e.update();
            if (e.isDone) {
                i.remove();
            }
        }

        if (this.vfxBelow.size() < 25 && !Settings.DISABLE_EFFECTS) {
            this.vfxBelow.add(new AltGameDeckGlowEffect(false));
        }

    }

    private void updatePop() {
        if (this.scale != 1.0F) {
            this.scale = MathUtils.lerp(this.scale, 1.0F, Gdx.graphics.getDeltaTime() * 8.0F);
            if (Math.abs(this.scale - 1.0F) < 0.003F) {
                this.scale = 1.0F;
            }
        }

    }

    public void pop() {
        this.scale = 1.75F * Settings.scale;
    }

    public void render(SpriteBatch sb) {
        if (AbstractDungeon.player instanceof MirthAndMalice) {
            MirthAndMalice p = (MirthAndMalice) AbstractDungeon.player;

            if (this.hb.hovered || this.bannerHb.hovered && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GAME_DECK_VIEW) {
                this.scale = 1.2F;
            }

            for (AltGameDeckGlowEffect e : vfxBelow)
            {
                e.render(sb, this.current_x, this.current_y + this.bob.y * 0.5F);
            }

            sb.setColor(Color.WHITE);
            sb.draw(deckTexture, this.current_x + OTHER_DECK_X, this.current_y + OTHER_DECK_Y + this.bob.y / 2.0F, 64.0F, 64.0F, 128.0F, 128.0F, this.scale * Settings.scale, this.scale * Settings.scale, 0.0F, 0, 0, 128, 128, false, false);
            String msg = Integer.toString(p.otherPlayerDraw.size());
            this.gl.setText(FontHelper.speech_font, msg);
            sb.setColor(Color.WHITE);
            sb.draw(ImageMaster.DECK_COUNT_CIRCLE, this.current_x + COUNT_OFFSET_X, this.current_y + COUNT_OFFSET_Y, COUNT_CIRCLE_W, COUNT_CIRCLE_W);

            FontHelper.renderFontCentered(sb, FontHelper.speech_font, msg, this.current_x + COUNT_X, this.current_y + COUNT_Y);
            if (!this.isHidden) {
                this.hb.render(sb);
                if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GAME_DECK_VIEW) {
                    this.bannerHb.render(sb);
                }
            }

            if (this.hb.hovered && !AbstractDungeon.isScreenUp && AbstractDungeon.getMonsters() != null && !AbstractDungeon.getMonsters().areMonstersDead()) {
                if (!AbstractDungeon.player.hasRelic(FrozenEye.ID)) {
                    TipHelper.renderGenericTip(DECK_TIP_X, DECK_TIP_Y, p.getOtherPlayerName() + TEXT[1], TEXT[2] + MathUtils.ceil(AbstractDungeon.player.gameHandSize / 2.0f) + TEXT[3] + p.getOtherPlayerName() + TEXT[4]);
                } else {
                    TipHelper.renderGenericTip(DECK_TIP_X, DECK_TIP_Y, p.getOtherPlayerName() + TEXT[1], TEXT[2] + MathUtils.ceil(AbstractDungeon.player.gameHandSize / 2.0f) + TEXT[3] + p.getOtherPlayerName() + TEXT[5]);
                }
            }
        }
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(makeID("DrawPilePanel"));
        TEXT = uiStrings.TEXT;
        COUNT_CIRCLE_W = 128.0F * Settings.scale;
        OTHER_DECK_X = 76.0F * Settings.scale - 64.0F;
        OTHER_DECK_Y = 74.0F * Settings.scale - 64.0F + OTHER_DRAW_OFFSET;
        COUNT_X = 118.0F * Settings.scale;
        COUNT_Y = 48.0F * Settings.scale + OTHER_DRAW_OFFSET;
        COUNT_OFFSET_X = 54.0F * Settings.scale;
        COUNT_OFFSET_Y = -18.0F * Settings.scale + OTHER_DRAW_OFFSET;
        DECK_TIP_X = 50.0F * Settings.scale;
        DECK_TIP_Y = 470.0F * Settings.scale;
        HITBOX_W = 120.0F * Settings.scale;
        HITBOX_W2 = 450.0F * Settings.scale;
    }
}