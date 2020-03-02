package mirthandmalice.patch.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.ui.buttons.EndTurnButton;
import mirthandmalice.patch.combat.RequireDoubleEndTurn;
import mirthandmalice.util.MultiplayerHelper;
import mirthandmalice.util.TextureLoader;

import static mirthandmalice.MirthAndMaliceMod.assetPath;

@SpirePatch(
        clz = EndTurnButton.class,
        method = "render"
)
public class EndTurnState {
    private static final float X1 = 1540.0F * Settings.scale;
    private static final float X2 = 1700.0F * Settings.scale;
    private static final float Y = 260.0F * Settings.scale;
    private static final int SIZE = 40;
    private static final int ORIGIN = SIZE / 2;
    private static Texture xTexture = TextureLoader.getTexture(assetPath("img/ui/x.png"));
    private static Texture checkTexture = TextureLoader.getTexture(assetPath("img/ui/check.png"));


    @SpirePostfixPatch
    public static void renderAfter(EndTurnButton __instance, SpriteBatch sb, boolean ___isHidden)
    {
        if (!Settings.hideEndTurn && !___isHidden && MultiplayerHelper.active)
        {
            sb.draw(RequireDoubleEndTurn.ended ? checkTexture : xTexture, X1, Y, ORIGIN, ORIGIN, SIZE, SIZE, Settings.scale, Settings.scale, 0, 0, 0, SIZE, SIZE, false, false);
            sb.draw(RequireDoubleEndTurn.otherPlayerEnded ? checkTexture : xTexture, X2, Y, ORIGIN, ORIGIN, SIZE, SIZE, Settings.scale, Settings.scale, 0, 0, 0, SIZE, SIZE, false, false);
        }
    }
}
