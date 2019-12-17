package mirthandmalice.patch.hooks;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;
import mirthandmalice.MirthAndMaliceMod;

@SpirePatch(
        clz = GameActionManager.class,
        method = "getNextAction"
)
public class PreMonsterTurn {
    @SpireInsertPatch(
            locator = Locator.class,
            localvars = { "m" }
    )
    public static void preMonsterTakeTurn(GameActionManager __instance, AbstractMonster m)
    {
        MirthAndMaliceMod.preMonsterTurn(m);
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractMonster.class, "takeTurn");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
