package mirthandmalice.patch.combat;

import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.Soul;
import javassist.CtBehavior;
import mirthandmalice.ui.OtherDrawPilePanel;

@SpirePatch(
        clz = Soul.class,
        method = "onToDeck",
        paramtypez = { AbstractCard.class, boolean.class, boolean.class }
)
public class SoulAltOnToDeck {
    public static CardGroup altGroup = null;
    public static boolean reposition = false;

    @SpireInsertPatch(
            locator = Locator.class,
            localvars = { "group" }
    )
    public static void changeRandomSpotGroup(Soul __instance, AbstractCard c, boolean randomSpot, boolean isInvisible, @ByRef(type="com.megacrit.cardcrawl.cards.CardGroup") Object[] group)
    {
        if (altGroup != null)
        {
            group[0] = altGroup;
            altGroup = null;
            reposition = true;
        }
    }
    @SpireInsertPatch(
            locator = AltLocator.class,
            localvars = { "group" }
    )
    public static void changeOnTopGroup(Soul __instance, AbstractCard c, boolean randomSpot, boolean isInvisible, @ByRef(type="com.megacrit.cardcrawl.cards.CardGroup") Object[] group)
    {
        if (altGroup != null)
        {
            group[0] = altGroup;
            altGroup = null;
            reposition = true;
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "addToRandomSpot");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
    private static class AltLocator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "addToTop");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }


    @SpireInsertPatch(
            locator = SecondLocator.class,
            localvars = { "pos", "target" }
    )
    public static void changePositions(Soul __instance, AbstractCard c, boolean randomSpot, boolean isInvisible, @ByRef(type="com.badlogic.gdx.math.Vector2") Object[] pos, @ByRef(type="com.badlogic.gdx.math.Vector2") Object[] target)
    {
        if (reposition)
        {
            ((Vector2)pos[0]).y += OtherDrawPilePanel.OTHER_DRAW_OFFSET;
            ((Vector2)target[0]).y += OtherDrawPilePanel.OTHER_DRAW_OFFSET;
            reposition = false;
        }
    }

    private static class SecondLocator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(Soul.class, "setSharedVariables");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
