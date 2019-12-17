package mirthandmalice.patch.ui;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.ui.AltOverlayMenu;

import java.util.ArrayList;

public class UseAltOverlayMenu {
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = { String.class, String.class, AbstractPlayer.class, ArrayList.class }
    )
    public static class normalConstructor
    {
        @SpirePostfixPatch
        public static void onConstruct(AbstractDungeon __instance, String name, String levelID, AbstractPlayer p, ArrayList<String> data)
        {
            if (p instanceof MirthAndMalice)
            {
                AbstractDungeon.overlayMenu = new AltOverlayMenu(p);
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = { String.class, AbstractPlayer.class, SaveFile.class }
    )
    public static class loadSaveConstructor
    {
        @SpirePostfixPatch
        public static void onConstruct(AbstractDungeon __instance, String name, AbstractPlayer p, SaveFile data)
        {
            if (p instanceof MirthAndMalice)
            {
                AbstractDungeon.overlayMenu = new AltOverlayMenu(p);
            }
        }
    }
}
