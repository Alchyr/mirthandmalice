package mirthandmalice.patch.updateOtherCardGroup;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;
import mirthandmalice.character.MirthAndMalice;

public class UpdateOtherHand
{
    @SpirePatch(
            clz = OverlayMenu.class,
            method = "update"
    )
    public static class normalUpdate {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = { "player" }
        )
        public static void onNormalUpdate(OverlayMenu __instance, AbstractPlayer player)
        {
            if (player instanceof MirthAndMalice)
                ((MirthAndMalice) player).otherPlayerHand.update();
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "update");

                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = OverlayMenu.class,
            method = "showCombatPanels"
    )
    public static class refreshOnShow
    {
        @SpirePostfixPatch
        public static void refresh(OverlayMenu __instance)
        {
            if (AbstractDungeon.player instanceof MirthAndMalice)
                ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.refreshHandLayout();
        }
    }

    @SpirePatch(
            clz = GameActionManager.class,
            method = "update"
    )
    public static class refreshOnWaitForPlayer
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void onRefreshInUpdate(GameActionManager __instance)
        {
            if (AbstractDungeon.player instanceof MirthAndMalice)
                ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.update();
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "refreshHandLayout");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = DrawCardAction.class,
            method = "update"
    )
    public static class refreshOnDraw
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void afterDraw(DrawCardAction __instance)
        {
            if (AbstractDungeon.player instanceof MirthAndMalice)
                ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.update();
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "refreshHandLayout");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
