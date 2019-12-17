package mirthandmalice.patch.screens;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.DiscardPileViewScreen;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.ui.panels.DiscardPilePanel;
import javassist.CtBehavior;

import java.util.ArrayList;

public class AltDiscardPileView {
    public static CardGroup altGroup = null;

    private static float padX;
    private static float padY;
    private static float drawStartX;
    private static float drawStartY;
    private static float scrollLowerBound;
    private static float scrollUpperBound;
    private static int previousDeckSize;

    @SpirePatch(
            clz = DiscardPileViewScreen.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class updateValuesOnCreation
    {
        @SpireInsertPatch(
            locator = Locator.class,
            localvars = { "padX", "padY", "drawStartX" }
        )
        public static void updateValues(DiscardPileViewScreen __instance, float padX, float padY, float drawStartX)
        {
            scrollLowerBound = -Settings.DEFAULT_SCROLL_LIMIT;
            scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;
            AltDiscardPileView.padX = padX;
            AltDiscardPileView.padY = padY;
            AltDiscardPileView.drawStartX = drawStartX;
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ScrollBar.class, "changeHeight");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = DiscardPilePanel.class,
            method = "openDiscardPile"
    )
    public static class removeAltGroup
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void onOpenNormalDiscard(DiscardPilePanel __instance)
        {
            altGroup = null;
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(DiscardPileViewScreen.class, "open");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = DiscardPileViewScreen.class,
            method = "open"
    )
    public static class openAlternate
    {
        @SpirePrefixPatch
        public static SpireReturn changeGroup(DiscardPileViewScreen __instance) {
            if (altGroup != null) {
                if (Settings.isControllerMode) {
                    Gdx.input.setCursorPosition(10, Settings.HEIGHT / 2);
                    ReflectionHacks.setPrivate(__instance, DiscardPileViewScreen.class, "controllerCard", null);
                }

                CardCrawlGame.sound.play("DECK_OPEN");
                AbstractDungeon.overlayMenu.showBlackScreen();
                ReflectionHacks.setPrivate(__instance, DiscardPileViewScreen.class, "currentDiffY", scrollLowerBound);
                ReflectionHacks.setPrivate(__instance, DiscardPileViewScreen.class, "grabStartY", scrollLowerBound);
                ReflectionHacks.setPrivate(__instance, DiscardPileViewScreen.class, "grabbedScreen", false);
                AbstractDungeon.isScreenUp = true;
                AbstractDungeon.screen = AbstractDungeon.CurrentScreen.DISCARD_VIEW;

                for (AbstractCard c : altGroup.group) {
                    c.setAngle(0.0F, true);
                    c.targetDrawScale = 0.75F;
                    c.drawScale = 0.75F;
                    c.lighten(true);
                }

                if (altGroup.group.size() <= 5) {
                    ReflectionHacks.setPrivate(__instance, DiscardPileViewScreen.class, "drawStartY", Settings.HEIGHT * 0.5F);
                    drawStartY = Settings.HEIGHT * 0.5F;
                } else {
                    ReflectionHacks.setPrivate(__instance, DiscardPileViewScreen.class, "drawStartY", Settings.HEIGHT * 0.66F);
                    drawStartY = Settings.HEIGHT * 0.66F;
                }

                int lineNum = 0;
                ArrayList<AbstractCard> cards = altGroup.group;

                for(int i = 0; i < cards.size(); ++i) {
                    int mod = i % 5;
                    if (mod == 0 && i != 0) {
                        ++lineNum;
                    }

                    cards.get(i).current_x = drawStartX + (float)mod * padX;
                    cards.get(i).current_y = drawStartY + scrollLowerBound - (float)lineNum * padY - MathUtils.random(100.0F * Settings.scale, 200.0F * Settings.scale);
                }


                AbstractDungeon.overlayMenu.cancelButton.show(DiscardPileViewScreen.TEXT[1]);

                if (altGroup.size() > 10) {
                    int scrollTmp = altGroup.size() / 5 - 2;

                    if (altGroup.size() % 5 != 0) {
                        ++scrollTmp;
                    }

                    ReflectionHacks.setPrivate(__instance, DiscardPileViewScreen.class, "scrollUpperBound", Settings.DEFAULT_SCROLL_LIMIT + (float)scrollTmp * padY);
                    scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT + (float)scrollTmp * padY;
                } else {
                    ReflectionHacks.setPrivate(__instance, DiscardPileViewScreen.class, "scrollUpperBound", Settings.DEFAULT_SCROLL_LIMIT);
                }

                ReflectionHacks.setPrivate(__instance, DiscardPileViewScreen.class, "prevDeckSize", AbstractDungeon.player.discardPile.size());
                previousDeckSize = altGroup.size();

                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = DiscardPileViewScreen.class,
            method = "updatePositions"
    )
    public static class updateAltDeckPositions
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = { "cards" }
        )
        public static void changeCards(DiscardPileViewScreen __instance, @ByRef(type="java.util.ArrayList") Object[] cards)
        {
            if (altGroup != null)
            {
                cards[0] = altGroup.group;
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "size");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = DiscardPileViewScreen.class,
            method = "updateScrolling"
    )
    public static class altUpdateScrolling
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn updateAlt(DiscardPileViewScreen __instance)
        {
            if (altGroup != null) {
                if (previousDeckSize != AbstractDungeon.player.discardPile.size()) {
                    if (altGroup.size() > 10) {
                        int scrollTmp = altGroup.size() / 5 - 2;

                        if (altGroup.size() % 5 != 0) {
                            ++scrollTmp;
                        }

                        ReflectionHacks.setPrivate(__instance, DiscardPileViewScreen.class, "scrollUpperBound", Settings.DEFAULT_SCROLL_LIMIT + (float)scrollTmp * padY);
                        scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT + (float)scrollTmp * padY;
                    } else {
                        ReflectionHacks.setPrivate(__instance, DiscardPileViewScreen.class, "scrollUpperBound", Settings.DEFAULT_SCROLL_LIMIT);
                    }

                    ReflectionHacks.setPrivate(__instance, DiscardPileViewScreen.class, "prevDeckSize", AbstractDungeon.player.discardPile.size());
                    previousDeckSize = altGroup.size();
                }
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "size");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = DiscardPileViewScreen.class,
            method = "render"
    )
    public static class altRender
    {
        private static CardGroup tempGroup = null;

        @SpirePrefixPatch
        public static void changeGroup(DiscardPileViewScreen __instance, SpriteBatch sb)
        {
            if (altGroup != null)
            {
                tempGroup = AbstractDungeon.player.discardPile;
                AbstractDungeon.player.discardPile = altGroup;
            }
        }

        @SpirePostfixPatch
        public static void returnGroup(DiscardPileViewScreen __instance, SpriteBatch sb)
        {
            if (tempGroup != null)
            {
                AbstractDungeon.player.discardPile = tempGroup;
                tempGroup = null;
            }
        }
    }
}