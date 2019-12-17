package mirthandmalice.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.HashMap;

import static basemod.abstracts.CustomCard.imgMap;
import static mirthandmalice.MirthAndMaliceMod.assetPath;
import static mirthandmalice.MirthAndMaliceMod.logger;

public class TextureLoader
{
    private static HashMap<String, Texture> textures = new HashMap<>();

    public static boolean hasTexture(String key)
    {
        return textures.containsKey(key);
    }

    public static void addTexture(String key, Texture t)
    {
        textures.put(key, t);
    }

    /**
     * @param textureString - String path to the texture you want to load relative to resources,
     * Example: "assetPath(img/MissingImage.png)"
     * @return <b>com.badlogic.gdx.graphics.Texture</b> - The texture from the path provided
     */
    public static Texture getTexture(final String textureString) {
        if (textures.get(textureString) == null) {
            try {
                loadTexture(textureString, true);
            } catch (GdxRuntimeException e) {
                try
                {
                    return getTexture(assetPath("img/MissingImage.png"));
                }
                catch (GdxRuntimeException ex) {
                    logger.info("The MissingImage is missing!");
                    return null;
                }
            }
        }
        return textures.get(textureString);
    }
    public static Texture getTextureNull(final String textureString) {
        if (textures.get(textureString) == null) {
            try {
                loadTexture(textureString);
            } catch (GdxRuntimeException e) {
                return null;
            }
        }
        return textures.get(textureString);
    }

    public static String getCardTextureString(final String cardName, final AbstractCard.CardType cardType)
    {
        String textureString;

        switch (cardType)
        {
            case ATTACK:
                textureString = assetPath("img/cards/attacks/" + cardName + ".png");
                break;
            case SKILL:
                textureString = assetPath("img/cards/skills/" + cardName + ".png");
                break;
            case POWER:
                textureString = assetPath("img/cards/powers/" + cardName + ".png");
                break;
            default:
                textureString = assetPath("img/cards/UnknownCard.png");
                break;
        }
        //no exception, file exists
        return textureString;
    }

    public static String getAndLoadCardTextureString(final String cardName, final AbstractCard.CardType cardType)
    {
        String textureString = getCardTextureString(cardName, cardType);
        String originalString = textureString;

        if (textures.get(textureString) == null) {
            try {
                loadTexture(textureString);
            } catch (GdxRuntimeException e) {
                switch (cardType) {
                    case ATTACK:
                        textureString = assetPath("img/cards/attacks/default.png");
                        break;
                    case SKILL:
                        textureString = assetPath("img/cards/skills/default.png");
                        break;
                    case POWER:
                        textureString = assetPath("img/cards/powers/default.png");
                        break;
                    default:
                        textureString = assetPath("img/MissingImage.png");
                        break;
                }

                loadCardTexture(originalString, textureString, true);
            }
        }
        //no exception, file exists
        return originalString;
    }

    private static void loadTexture(final String textureString) throws GdxRuntimeException {
        loadTexture(textureString, true);
    }

    private static void loadTexture(final String textureString, boolean linearFilter) throws GdxRuntimeException {
        Texture texture =  new Texture(textureString);
        if (linearFilter)
        {
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        else
        {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        textures.put(textureString, texture);
    }

    private static void loadCardTexture(final String textureKey, final String textureString, boolean linearFilter) throws GdxRuntimeException {
        if (!textures.containsKey(textureString))
        {
            Texture texture = new Texture(textureString);
            if (linearFilter)
            {
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            }
            else
            {
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            }
            if (!imgMap.containsKey(textureKey))
                imgMap.put(textureKey, texture);
            textures.put(textureKey, texture);
        }
        else
        {
            if (!imgMap.containsKey(textureKey))
                imgMap.put(textureKey, textures.get(textureString));
            textures.put(textureKey, textures.get(textureString));
        }
    }

    public static Texture getPowerTexture(final String powerName)
    {
        String textureString = assetPath(PowerPath(powerName));
        return getTexture(textureString);
    }
    public static Texture getHiDefPowerTexture(final String powerName)
    {
        String textureString = assetPath(HiDefPowerPath(powerName));
        return getTextureNull(textureString);
    }
    public static String PowerPath(String powerName)
    {
        return "img/powers/" + powerName + ".png";
    }
    public static String HiDefPowerPath(String powerName)
    {
        return "img/powers/hidef/" + powerName + ".png";
    }

    public static boolean testTexture(String filePath)
    {
        return Gdx.files.internal(filePath).exists();
        }
}