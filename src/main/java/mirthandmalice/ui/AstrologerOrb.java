package mirthandmalice.ui;

import basemod.abstracts.CustomEnergyOrb;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static mirthandmalice.MirthAndMaliceMod.assetPath;

public class AstrologerOrb extends CustomEnergyOrb {
    private static final String[] orbTextures = {
            assetPath("img/Character/orb/layer1.png"),
            assetPath("img/Character/orb/layer2.png"),
            assetPath("img/Character/orb/layer3.png"),
            assetPath("img/Character/orb/layer4.png"),
            assetPath("img/Character/orb/layer5.png"),
            assetPath("img/Character/orb/layer6.png"),
            assetPath("img/Character/orb/layer1d.png"),
            assetPath("img/Character/orb/layer2d.png"),
            assetPath("img/Character/orb/layer3d.png"),
            assetPath("img/Character/orb/layer4d.png"),
            assetPath("img/Character/orb/layer5d.png") };

    private static final String VFXTexture = assetPath("img/Character/orb/vfx.png");

    private static final float[] layerSpeeds = new float[] {10.0F, 30.0F, 15.0F, -20.0F, 0.0F};

    public AstrologerOrb()
    {
        super(orbTextures, VFXTexture, layerSpeeds);
    }

    @Override
    public void updateOrb(int energyCount) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        if (energyCount == 0) {
            for (int i = 0; i < Math.min(this.angles.length, layerSpeeds.length); ++i)
            {
                this.angles[i] += deltaTime * layerSpeeds[i] / 4.0F;
            }
        } else {
            for (int i = 0; i < Math.min(this.angles.length, layerSpeeds.length); ++i)
            {
                this.angles[i] += deltaTime * layerSpeeds[i];
            }
        }
    }

    @Override
    public void renderOrb(SpriteBatch sb, boolean enabled, float current_x, float current_y) {
        super.renderOrb(sb, enabled, current_x, current_y);
    }
}