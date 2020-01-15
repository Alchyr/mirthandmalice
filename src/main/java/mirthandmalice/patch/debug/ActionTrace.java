package mirthandmalice.patch.debug;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;

import static mirthandmalice.MirthAndMaliceMod.FULL_DEBUG;
import static mirthandmalice.MirthAndMaliceMod.logger;

public class ActionTrace {
    @SpirePatch(
            clz = GameActionManager.class,
            method = "addToTop"
    )
    @SpirePatch(
            clz = GameActionManager.class,
            method = "addToBottom"
    )
    public static class ADD_AND_REMOVE
    {
        @SpirePostfixPatch
        public static void TRACE(GameActionManager __instance, AbstractGameAction action)
        {
            if (FULL_DEBUG)
            {
                logger.debug("Action Queue:");
                int position = 0;
                for (AbstractGameAction gameAction : __instance.actions)
                {
                    logger.debug(position+++ ": " + gameAction.getClass().getName());
                }
            }
        }
    }

    public static class COMPLETION
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void TRACE(GameActionManager __instance)
        {
            if (FULL_DEBUG && __instance.previousAction != null)
            {
                logger.debug("Action complete: " + __instance.previousAction.getClass().getName());
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(GameActionManager.class, "getNextAction");
                return new int[] { LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[1] };
            }
        }
    }
}
