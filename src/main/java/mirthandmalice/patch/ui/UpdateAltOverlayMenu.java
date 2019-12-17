package mirthandmalice.patch.ui;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.ui.panels.DrawPilePanel;
import javassist.CtBehavior;
import mirthandmalice.ui.AltOverlayMenu;

@SpirePatch(
        clz = OverlayMenu.class,
        method = "update"
)
public class UpdateAltOverlayMenu {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void update(OverlayMenu __instance)
    {
        if (__instance instanceof AltOverlayMenu)
        {
            ((AltOverlayMenu) __instance).otherPlayerEnergy.updatePositions();
            ((AltOverlayMenu) __instance).otherPlayerEnergy.update();
            ((AltOverlayMenu) __instance).otherPlayerDrawPile.updatePositions(((AltOverlayMenu) __instance).wasOpen);
            ((AltOverlayMenu) __instance).otherDiscardPilePanel.updatePositions();
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(DrawPilePanel.class, "updatePositions");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
