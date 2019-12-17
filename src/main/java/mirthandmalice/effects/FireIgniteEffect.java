package mirthandmalice.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.LightFlareParticleEffect;

public class FireIgniteEffect extends AbstractGameEffect {
    private float x;
    private float y;
    private int amount;

    public FireIgniteEffect(float x, float y, int amount) {
        this.x = x;
        this.y = y;
        this.amount = amount;
    }

    public void update() {
        for(int i = 0; i < amount; ++i) {
            AbstractDungeon.effectsQueue.add(new RedFireBurstParticleEffect(this.x, this.y));
            AbstractDungeon.effectsQueue.add(new LightFlareParticleEffect(this.x, this.y, randomFlareColor()));
        }

        this.isDone = true;
    }

    private Color randomFlareColor()
    {
        if (MathUtils.randomBoolean())
        {
            return Color.ORANGE;
        }
        else
        {
            if (MathUtils.randomBoolean())
            {
                return Color.YELLOW;
            }
            else
            {
                return Color.RED;
            }
        }
    }

    public void render(SpriteBatch sb) {
    }

    public void dispose() {
    }
}
