package mirthandmalice.patch.ui;

import basemod.ReflectionHacks;
import basemod.patches.com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar.ColorTabBarFix;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar;
import javassist.CtBehavior;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.enums.CharacterEnums;

import java.util.ArrayList;

import static mirthandmalice.MirthAndMaliceMod.logger;

@SpirePatch(
        clz = ColorTabBarFix.Render.class,
        method = "Insert"
)
public class LocalizedColorNamesInLibrary {
    private static ArrayList<ColorTabBarFix.ModColorTab> modTabs = null;
    @SpireInsertPatch(
            locator = Locator.class,
            localvars = { "i", "tabName" }
    )
    @SuppressWarnings("unchecked")
    public static void changeTabName(ColorTabBar __instance, SpriteBatch sb, float y, ColorTabBar.CurrentTab tab, int i, @ByRef String[] tabName)
    {
        if (modTabs == null)
        {
            modTabs = (ArrayList<ColorTabBarFix.ModColorTab>)ReflectionHacks.getPrivateStatic(ColorTabBarFix.Fields.class, "modTabs");
        }
        if (modTabs.get(i).color == CharacterEnums.MIRTHMALICE_MIRTH)
        {
            tabName[0] = MirthAndMalice.characterStrings.NAMES[1];
        }
        else if (modTabs.get(i).color == CharacterEnums.MIRTHMALICE_MALICE)
        {
            tabName[0] = MirthAndMalice.characterStrings.NAMES[2];
        }
        else if (modTabs.get(i).color == CharacterEnums.MIRTHMALICE_NEUTRAL)
        {
            tabName[0] = MirthAndMalice.characterStrings.NAMES[0];
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(FontHelper.class, "renderFontCentered");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
