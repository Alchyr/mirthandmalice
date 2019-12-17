package mirthandmalice.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import mirthandmalice.util.TextureLoader;

import static mirthandmalice.MirthAndMaliceMod.assetPath;
import static mirthandmalice.MirthAndMaliceMod.chat;

public class LobbyTextInput {
    private static final Texture inputArea = TextureLoader.getTexture(assetPath("img/ui/textBox.png"));
    private static final Texture white = TextureLoader.getTexture(assetPath("img/white.png"));

    private static final int WIDTH = 666;
    public static final int HEIGHT = 60;

    private static final int TEXT_CAP = 18;

    private static final float TEXT_OFFSET_X = 30.0f * Settings.scale;
    private static final float TEXT_OFFSET_Y = HEIGHT / 2.0f * Settings.scale;

    private static final float MARKER_OFFSET = 18.0f * Settings.scale;
    private static final float MARKER_WIDTH = FontHelper.getWidth(FontHelper.tipBodyFont, "-", 1);
    private static final float MARKER_HEIGHT = 2.0f * Settings.scale;

    private boolean active;

    private String text;

    private Hitbox hb;

    private float textX;
    private float textY;
    private float markerY;

    public LobbyTextInput(float x, float y)
    {
        this.hb = new Hitbox(x, y, WIDTH * Settings.scale, HEIGHT * Settings.scale);
        this.textX = x + TEXT_OFFSET_X;
        this.textY = y + TEXT_OFFSET_Y;
        this.markerY = y + MARKER_OFFSET;

        text = "";

        active = false;
    }

    public void reset()
    {
        this.text = "";
        this.active = false;
    }
    public void setText(String t)
    {
        this.text = t;
    }
    public String getText()
    {
        return text;
    }

    public void update()
    {
        hb.update();
        if (hb.hovered && InputHelper.justReleasedClickLeft)
        {
            InputHelper.justReleasedClickLeft = false;
            chat.active = false;
            active = true;
            mirthandmalice.patch.input.InputHelper.text = this.text;
        }
        else if (InputHelper.justReleasedClickLeft || InputHelper.isMouseDown)
        {
            mirthandmalice.patch.input.InputHelper.reset();
            active = false;
        }

        if (active)
        {
            this.text = mirthandmalice.patch.input.InputHelper.text;
            if (this.text.length() > TEXT_CAP)
            {
                this.text = this.text.substring(0, TEXT_CAP);
                mirthandmalice.patch.input.InputHelper.text = this.text;
            }
        }
    }

    public void render(SpriteBatch sb)
    {
        sb.setColor(Color.WHITE);
        sb.draw(inputArea, hb.x, hb.y, 0, 0, WIDTH, HEIGHT, Settings.scale, Settings.scale, 0, 0, 0, WIDTH, HEIGHT, false, false);

        FontHelper.renderFontLeft(sb, FontHelper.tipBodyFont, text, textX, textY, Color.WHITE);

        if (active)
        {
            sb.draw(white, textX + FontHelper.getWidth(FontHelper.tipBodyFont, text, 1), markerY, 0, 0, MARKER_WIDTH, MARKER_HEIGHT, 1.0f, 1.0f, 0, 0, 0, 1, 1, false, false);
        }
    }
}
