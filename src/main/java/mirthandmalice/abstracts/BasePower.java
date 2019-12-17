package mirthandmalice.abstracts;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import mirthandmalice.util.TextureLoader;

import java.util.HashMap;
import java.util.Map;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public abstract class BasePower extends AbstractPower {
    private static PowerStrings getPowerStrings(String ID)
    {
        return CardCrawlGame.languagePack.getPowerStrings(ID);
    }
    protected AbstractCreature source;

    protected static Map<String, PowerStrings> powerStrings = new HashMap<>();

    protected String[] descriptions()
    {
        return powerStrings.get(this.ID).DESCRIPTIONS;
    }

    public BasePower(String NAME, PowerType powerType, boolean isTurnBased, AbstractCreature owner, AbstractCreature source, int amount) {
        this(NAME, powerType, isTurnBased, owner, source, amount, "");
    }
    public BasePower(String NAME, PowerType powerType, boolean isTurnBased, AbstractCreature owner, AbstractCreature source, int amount, String IDModifier) {
        this.ID = makeID(NAME);
        this.isTurnBased = isTurnBased;

        if (!powerStrings.containsKey(this.ID))
            powerStrings.put(this.ID, getPowerStrings(this.ID));
        this.name = powerStrings.get(this.ID).NAME;

        this.owner = owner;
        this.source = source;
        this.amount = amount;
        this.type = powerType;

        Texture normalTexture = TextureLoader.getPowerTexture(NAME);
        Texture hiDefImage = TextureLoader.getHiDefPowerTexture(NAME);
        if (hiDefImage != null)
        {
            region128 = new TextureAtlas.AtlasRegion(hiDefImage, 0, 0, hiDefImage.getWidth(), hiDefImage.getHeight());
            if (normalTexture != null)
                region48 = new TextureAtlas.AtlasRegion(normalTexture, 0, 0, normalTexture.getWidth(), normalTexture.getHeight());
        }
        else if (normalTexture != null)
        {
            this.img = normalTexture;
            region48 = new TextureAtlas.AtlasRegion(normalTexture, 0, 0, normalTexture.getWidth(), normalTexture.getHeight());
        }

        this.ID += IDModifier;

        this.updateDescription();
    }
    public BasePower(String NAME, PowerType powerType, boolean isTurnBased, AbstractCreature owner, AbstractCreature source, int amount, boolean loadImage) {
        this.ID = makeID(NAME);
        this.isTurnBased = isTurnBased;

        if (!powerStrings.containsKey(this.ID))
            powerStrings.put(this.ID, getPowerStrings(this.ID));
        this.name = powerStrings.get(this.ID).NAME;

        this.owner = owner;
        this.source = source;
        this.amount = amount;
        this.type = powerType;

        if (loadImage)
        {
            Texture normalTexture = TextureLoader.getPowerTexture(NAME);
            Texture hiDefImage = TextureLoader.getHiDefPowerTexture(NAME);
            if (hiDefImage != null)
            {
                region128 = new TextureAtlas.AtlasRegion(hiDefImage, 0, 0, hiDefImage.getWidth(), hiDefImage.getHeight());
                if (normalTexture != null)
                    region48 = new TextureAtlas.AtlasRegion(normalTexture, 0, 0, normalTexture.getWidth(), normalTexture.getHeight());
            }
            else if (normalTexture != null)
            {
                this.img = normalTexture;
                region48 = new TextureAtlas.AtlasRegion(normalTexture, 0, 0, normalTexture.getWidth(), normalTexture.getHeight());
            }
        }

        this.updateDescription();
    }
}