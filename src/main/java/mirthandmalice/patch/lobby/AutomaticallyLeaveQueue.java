package mirthandmalice.patch.lobby;

import basemod.CustomCharacterSelectScreen;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CtBehavior;
import mirthandmalice.patch.enums.CharacterEnums;

import static mirthandmalice.patch.lobby.UseMultiplayerQueue.leaveQueue;

public class AutomaticallyLeaveQueue {
    @SpirePatch(
            clz = CustomCharacterSelectScreen.class,
            method = "setCurrentOptions"
    )
    public static class leaveOnChangeOptions
    {
        @SpirePrefixPatch
        public static void onChange(CustomCharacterSelectScreen __instance, boolean direction)
        {
            if (UseMultiplayerQueue.inQueue)
            {
                leaveQueue();
            }
        }
    }

    @SpirePatch(
            clz = CharacterSelectScreen.class,
            method = "update"
    )
    public static class leaveOnChangeCharacter
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void onChange(CharacterSelectScreen __instance)
        {
            if (UseMultiplayerQueue.inQueue)
            {
                if (CardCrawlGame.chosenCharacter != null && CardCrawlGame.chosenCharacter != CharacterEnums.MIRTHMALICE)
                {
                    leaveQueue();
                }
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(UnlockTracker.class, "isAscensionUnlocked");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
