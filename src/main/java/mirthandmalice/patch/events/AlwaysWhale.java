package mirthandmalice.patch.events;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.neow.NeowEvent;
import javassist.CtBehavior;
import mirthandmalice.patch.enums.CharacterEnums;

@SpirePatch(
        clz = NeowEvent.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = { boolean.class }
)
public class AlwaysWhale {
    @SpireInsertPatch(
            locator = Locator.class,
            localvars = { "bossCount" }
    )
    public static void ALWAYS_WHALE(NeowEvent __instance, boolean isDone, @ByRef int[] bossCount)
    {
        if (!Settings.isEndless || AbstractDungeon.floorNum <= 1)
        {
            if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE)
                bossCount[0] = 1;
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(Settings.class, "isEndless");
            return new int[] { LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[2] };
        }
    }
}
