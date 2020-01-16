package mirthandmalice.patch.debug;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
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
                StringBuilder queueString = new StringBuilder("Queue:\n");
                for (AbstractGameAction gameAction : __instance.actions)
                {
                    queueString.append(position++).append(": ").append(gameAction.getClass().getName());
                }

                logger.info(queueString.toString());
            }
        }
    }

    @SpirePatch(
            clz = GameActionManager.class,
            method = "update"
    )
    public static class COMPLETION
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void TRACE(GameActionManager __instance)
        {
            if (FULL_DEBUG && __instance.previousAction != null)
            {
                logger.info("Action complete: " + __instance.previousAction.getClass().getName() + ". Remaining actions: " + __instance.actions.size());
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
