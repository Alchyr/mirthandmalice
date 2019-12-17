package mirthandmalice.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbInterface;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import mirthandmalice.effects.CalmFireEffect;
import mirthandmalice.effects.CasualFlameParticleEffect;

import java.util.ArrayList;

public class MokouOrb implements EnergyOrbInterface {
    private static final float CD = 0.05f;
    private static final float CALM_FIRE_OFFSET = -35.0F * Settings.scale;

    private ArrayList<AbstractGameEffect> flameEffects = new ArrayList<>();
    private ArrayList<AbstractGameEffect> baseEffects = new ArrayList<>();
    private int targetCount;
    private float maxOffset;
    private float calmFireCooldown = CD;

    public MokouOrb()
    {
        targetCount = 40;
        maxOffset = 90 * Settings.scale;
    }

    @Override
    public void renderOrb(SpriteBatch sb, boolean active, float current_x, float current_y) {
        for (AbstractGameEffect e : baseEffects)
        {
            e.render(sb);
        }
        for (AbstractGameEffect e : flameEffects)
        {
            e.render(sb);
        }

        int max = Math.min(targetCount / 4, targetCount - flameEffects.size());
        for (int i = 0; i < max; i++) {

            float distance = MathUtils.random(maxOffset);
            float direction = MathUtils.random(MathUtils.PI2);
            float xOffset = MathUtils.cos(direction) * distance;
            float yOffset = MathUtils.sin(direction) * distance;
            flameEffects.add(new CasualFlameParticleEffect(current_x + xOffset, current_y + yOffset));
        }
        if (calmFireCooldown < 0)
        {
            calmFireCooldown += CD;
            baseEffects.add(new CalmFireEffect(current_x, current_y + CALM_FIRE_OFFSET));
        }
    }

    @Override
    public void updateOrb(int energyCount) {
        for (AbstractGameEffect e : baseEffects) {
            e.update();
        }
        for (AbstractGameEffect e : flameEffects) {
            e.update();
        }
        targetCount = energyCount * 20;
        maxOffset = Math.min(160.0f * Settings.scale, energyCount * 40.0f * Settings.scale);
        baseEffects.removeIf((e)->e.isDone);
        flameEffects.removeIf((e)->e.isDone);
        calmFireCooldown -= Gdx.graphics.getDeltaTime();
    }
}
