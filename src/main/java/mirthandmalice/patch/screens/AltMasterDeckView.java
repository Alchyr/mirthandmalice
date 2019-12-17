package mirthandmalice.patch.screens;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputAction;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CtBehavior;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.enums.CharacterEnums;

import java.util.ArrayList;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class AltMasterDeckView {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("MasterDeckView"));
    private static boolean openAlt = false;

    @SpirePatch(
            clz = TopPanel.class,
            method = "renderTopRightIcons"
    )
    public static class UseAltTip
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void renderAltTip(TopPanel __instance, SpriteBatch sb)
        {
            if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && AbstractDungeon.player instanceof MirthAndMalice)
            {
                //rendering a tip prevents the other tip from being rendered.
                TipHelper.renderGenericTip(1550.0F * Settings.scale, (float)Settings.HEIGHT - 120.0F * Settings.scale, TopPanel.LABEL[1] + " (" + InputActionSet.masterDeck.getKeyString() + ")", uiStrings.TEXT[1] + ((MirthAndMalice) AbstractDungeon.player).getOtherPlayerName() + uiStrings.TEXT[2]);
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(TipHelper.class, "renderGenericTip");
                return new int[] { LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[1] };
            }
        }
    }

    @SpirePatch(
            clz = TopPanel.class,
            method = "updateDeckViewButtonLogic"
    )
    public static class OpenAlt
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = { "clickedDeckButton" }
        )
        public static void rightClickOpen(TopPanel __instance, @ByRef boolean[] clicked)
        {
            if (!clicked[0] && AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && InputHelper.justClickedRight && __instance.deckHb.hovered)
            {
                clicked[0] = true;
                openAlt = true;
            }
            else if (clicked[0])
            {
                openAlt = false;
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(InputAction.class, "isJustPressed");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = MasterDeckViewScreen.class,
            method = "updateControllerInput"
    )
    public static class ControllerUseAlt
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = { "deck" }
        )
        public static void viewAlt(MasterDeckViewScreen __instance, @ByRef(type="com.megacrit.cardcrawl.cards.CardGroup") Object[] deck)
        {
            if (openAlt && AbstractDungeon.player instanceof MirthAndMalice)
            {
                deck[0] = ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck;
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardGroup.class, "group");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = MasterDeckViewScreen.class,
            method = "updatePositions"
    )
    public static class UpdateAltPositions
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = { "cards" }
        )
        public static void updateAlt(MasterDeckViewScreen __instance, @ByRef(type="java.util.ArrayList") Object[] cards)
        {
            if (openAlt && AbstractDungeon.player instanceof MirthAndMalice)
            {
                cards[0] = ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.group;
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
            clz = MasterDeckViewScreen.class,
            method = "updateClicking"
    )
    public static class UpdateAltClick
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = { "hoveredCard", "clickStartedCard" }
        )
        public static SpireReturn openAlt(MasterDeckViewScreen __instance, AbstractCard hovered, @ByRef(type="com.megacrit.cardcrawl.cards.AbstractCard") Object[] clickStartedCard)
        {
            if (openAlt && AbstractDungeon.player instanceof MirthAndMalice)
            {
                CardCrawlGame.cardPopup.open(hovered, ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck);
                clickStartedCard[0] = null;
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(SingleCardViewPopup.class, "open");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = MasterDeckViewScreen.class,
            method = "calculateScrollBounds"
    )
    public static class CalcAltBounds
    {
        @SpirePrefixPatch
        public static SpireReturn calcAlt(MasterDeckViewScreen __instance)
        {
            if (openAlt && AbstractDungeon.player instanceof MirthAndMalice)
            {
                if (((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.size() > 10) {
                    int scrollTmp = ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.size() / 5 - 2;
                    if (((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.size() % 5 != 0) {
                        ++scrollTmp;
                    }

                    ReflectionHacks.setPrivate(__instance, MasterDeckViewScreen.class, "scrollUpperBound", Settings.DEFAULT_SCROLL_LIMIT + (float)scrollTmp * (AbstractCard.IMG_HEIGHT * 0.75F + Settings.CARD_VIEW_PAD_Y));
                } else {
                    ReflectionHacks.setPrivate(__instance, MasterDeckViewScreen.class, "scrollUpperBound", Settings.DEFAULT_SCROLL_LIMIT);
                }

                ReflectionHacks.setPrivate(__instance, MasterDeckViewScreen.class, "prevDeckSize", ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.size());
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = MasterDeckViewScreen.class,
            method = "hideCards"
    )
    public static class HideAltCards
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = { "cards" }
        )
        public static void hideAlt(MasterDeckViewScreen __instance, @ByRef(type="java.util.ArrayList") Object[] cards)
        {
            if (openAlt && AbstractDungeon.player instanceof MirthAndMalice)
            {
                cards[0] = ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.group;
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
            clz = MasterDeckViewScreen.class,
            method = "render"
    )
    public static class RenderAlt
    {
        @SpirePrefixPatch
        public static SpireReturn rendering(MasterDeckViewScreen __instance, SpriteBatch sb)
        {
            if (openAlt && AbstractDungeon.player instanceof MirthAndMalice) {
                AbstractCard hoveredCard = (AbstractCard)ReflectionHacks.getPrivate(__instance, MasterDeckViewScreen.class, "hoveredCard");
                if (hoveredCard == null) {
                    ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.renderMasterDeck(sb);
                } else {
                    ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.renderMasterDeckExceptOneCard(sb, hoveredCard);
                    hoveredCard.renderHoverShadow(sb);
                    hoveredCard.render(sb);
                }

                ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.renderTip(sb);// 345
                FontHelper.renderDeckViewTip(sb, ((MirthAndMalice) AbstractDungeon.player).getOtherPlayerName() + uiStrings.TEXT[0], 96.0F * Settings.scale, Settings.CREAM_COLOR);
                if ((float)ReflectionHacks.getPrivate(__instance, MasterDeckViewScreen.class, "scrollUpperBound") > 500.0F * Settings.scale ) {
                    ((ScrollBar)ReflectionHacks.getPrivate(__instance, MasterDeckViewScreen.class, "scrollBar")).render(sb);
                }
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
