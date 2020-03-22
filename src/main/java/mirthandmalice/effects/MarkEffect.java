package mirthandmalice.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.fortune_misfortune.FortuneMisfortune;

import static mirthandmalice.ui.OtherDrawPilePanel.OTHER_DRAW_OFFSET;

public class MarkEffect extends AbstractGameEffect {
    private static float DISCARD_X = (float)Settings.WIDTH * 0.96F;
    private static float DRAW_PILE_X = (float)Settings.WIDTH * 0.04F;
    private static float PILE_Y = (float)Settings.HEIGHT * 0.06F;

    private static final int SIZE = 30;
    private static final float CENTER = SIZE / 2.0f;

    private float x, sX, tX;
    private float y, sY, tY;

    private float spin;
    private float spinAccel;

    private Texture t;

    public MarkEffect(boolean fortune, Vector2 pos, CardGroup targetGroup)
    {
        this(fortune, pos.x, pos.y, targetGroup);
    }
    public MarkEffect(boolean fortune, Vector2 pos, AbstractCard c)
    {
        this(fortune, pos.x, pos.y, c);
    }
    public MarkEffect(boolean fortune, float x, float y, CardGroup targetGroup)
    {
        this.color = Color.WHITE.cpy();

        this.t = fortune ? FortuneMisfortune.FORTUNE_TEXTURE : FortuneMisfortune.MISFORTUNE_TEXTURE;

        this.x = this.sX = x;
        this.y = this.sY = y;
        this.tY = PILE_Y;

        switch (targetGroup.type)
        {
            case DRAW_PILE:
                this.tX = DRAW_PILE_X;
                if (AbstractDungeon.player instanceof MirthAndMalice && ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw == targetGroup)
                {
                    this.tY += OTHER_DRAW_OFFSET;
                }
                break;
            default:
                this.tX = DISCARD_X;
                if (AbstractDungeon.player instanceof MirthAndMalice && ((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard == targetGroup)
                {
                    this.tY += OTHER_DRAW_OFFSET;
                }
                break;
        }

        this.rotation = MathUtils.random(-36.0f, 36.0f);
        this.spin = this.tX < sX ? -5 : 5;
        spinAccel = this.spin * 5;

        this.duration = 1.5f;
    }

    public MarkEffect(boolean fortune, float x, float y, AbstractCard target)
    {
        this.color = Color.WHITE.cpy();

        this.t = fortune ? FortuneMisfortune.FORTUNE_TEXTURE : FortuneMisfortune.MISFORTUNE_TEXTURE;

        this.x = this.sX = x;
        this.y = this.sY = y;

        this.tX = target.current_x;
        this.tY = target.current_y;

        this.spin = this.tX < sX ? -5 : 5;
        spinAccel = this.spin * 5;

        this.duration = 1.2f;
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();

        if (this.duration < this.startingDuration / 4.0F) {
            this.color.a = this.duration / (this.startingDuration / 4.0F);
        }


        this.x = Interpolation.circleIn.apply(sX, tX, 1 - (this.duration / this.startingDuration));
        this.y = Interpolation.circleIn.apply(sY, tY, 1 - (this.duration / this.startingDuration));

        this.rotation += this.spin;
        this.spin += Gdx.graphics.getDeltaTime() + spinAccel;

        if (this.duration < 0.0F) {
            this.isDone = true;
            this.color.a = 0.0F;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.draw(
                this.t,
                x - CENTER,
                y - CENTER,
                CENTER,
                CENTER,
                SIZE,
                SIZE,
                Settings.scale,
                Settings.scale,
                rotation,
                0,
                0,
                SIZE,
                SIZE,
                false,
                false
        );
    }

    @Override
    public void dispose() {

    }
}
