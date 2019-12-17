package mirthandmalice.patch.input;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.helpers.input.InputAction;

import static mirthandmalice.MirthAndMaliceMod.chat;

public class DisableInputAction {
    @SpirePatch(
            clz = InputAction.class,
            method = "isJustPressed"
    )
    public static class onInitialPress
    {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> preventInitialPress(InputAction __instance)
        {
            if (chat != null && (chat.active || chat.skipNextInput))
            {
                return SpireReturn.Return(false);
            }
            return SpireReturn.Continue();
        }
    }
    @SpirePatch(
            clz = InputAction.class,
            method = "isPressed"
    )
    public static class onPress
    {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> preventPress(InputAction __instance)
        {
            if (chat != null && (chat.active || chat.skipNextInput))
            {
                return SpireReturn.Return(false);
            }
            return SpireReturn.Continue();
        }
    }
}
