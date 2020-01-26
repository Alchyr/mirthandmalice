package mirthandmalice.patch.ui;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.options.ExitGameButton;
import com.megacrit.cardcrawl.screens.options.OptionsPanel;
import mirthandmalice.character.MirthAndMalice;

import java.lang.reflect.Field;

import static mirthandmalice.MirthAndMaliceMod.logger;

@SpirePatch(
        clz = OptionsPanel.class,
        method = "refresh"
)
public class SaveAndQuitButton {
    private static Field exitButtonField;

    static
    {
        try
        {
            exitButtonField = OptionsPanel.class.getDeclaredField("exitBtn");
            exitButtonField.setAccessible(true);
        }
        catch (Exception e)
        {
            logger.error("Failed to access exitBtn field of OptionsPanel class.", e);
        }
    }

    @SpirePostfixPatch
    public static void noSaving(OptionsPanel __instance) throws IllegalAccessException {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            ExitGameButton button = (ExitGameButton) exitButtonField.get(__instance);
            button.updateLabel(OptionsPanel.TEXT[15]);
        }
    }
}
