package mirthandmalice.patch.events;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.ui.buttons.LargeDialogOptionButton;
import javassist.CtBehavior;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.util.HandleMatchmaking;
import mirthandmalice.util.MultiplayerHelper;
import mirthandmalice.util.SmartTextHelper;
import mirthandmalice.util.TextureLoader;

import static mirthandmalice.util.HandleMatchmaking.isHost;
import static mirthandmalice.MirthAndMaliceMod.*;

public class RoomEventVoting {
    private static final Texture yinSelect = TextureLoader.getTexture(assetPath("img/ui/yinSelect.png"));
    private static final Texture yangSelect = TextureLoader.getTexture(assetPath("img/ui/yangSelect.png"));

    public static int otherPlayerSelected = -1;
    private static LargeDialogOptionButton otherSelected = null;
    public static boolean choseOption = false;
    private static LargeDialogOptionButton mySelected = null;

    private static boolean voteComplete = false;

    public static void receiveVote(int index)
    {
        if (index >= 0 && index < RoomEventDialog.optionList.size())
        {
            otherPlayerSelected = index;
            otherSelected = RoomEventDialog.optionList.get(index);

            if (isHost && choseOption)
            {
                handleSelection();
            }
        }
        else
        {
            logger.error("Received invalid event selection index! !Possible! !desync! !alert!");
        }
    }

    public static void reset()
    {
        choseOption = false;
        otherPlayerSelected = -1;
        otherSelected = null;
        mySelected = null;
        voteComplete = false;
    }

    @SpirePatch(
            clz = RoomEventDialog.class,
            method = "clear"
    )
    @SpirePatch(
            clz = RoomEventDialog.class,
            method = "show",
            paramtypez = {}
    )
    @SpirePatch(
            clz = RoomEventDialog.class,
            method = "clearRemainingOptions"
    )
    @SpirePatch(
            clz = RoomEventDialog.class,
            method = "updateDialogOption"
    )
    @SpirePatch(
            clz = RoomEventDialog.class,
            method = "addDialogOption",
            paramtypez = { String.class }
    )
    @SpirePatch(
            clz = RoomEventDialog.class,
            method = "addDialogOption",
            paramtypez = { String.class, AbstractCard.class}
    )
    @SpirePatch(
            clz = RoomEventDialog.class,
            method = "addDialogOption",
            paramtypez = { String.class, boolean.class}
    )
    @SpirePatch(
            clz = RoomEventDialog.class,
            method = "addDialogOption",
            paramtypez = { String.class, boolean.class, AbstractCard.class}
    )
    @SpirePatch(
            clz = RoomEventDialog.class,
            method = "removeDialogOption"
    )
    public static class ResetOnModify
    {
        @SpirePrefixPatch
        public static void onChange(RoomEventDialog __instance)
        {
            reset();
        }
    }

    @SpirePatch(
            clz = RoomEventDialog.class,
            method = "update"
    )
    public static class OnChooseOption
    {
        @SpirePostfixPatch
        public static void changeSelectionToVote(RoomEventDialog __instance)
        {
            if (AbstractDungeon.player instanceof MirthAndMalice && MultiplayerHelper.active)
            {
                if (!RoomEventDialog.waitForInput && !voteComplete) //this means an option has been chosen
                {
                    MultiplayerHelper.sendP2PString("room_option" + RoomEventDialog.selectedOption);
                    if (!choseOption || (mySelected != null && !mySelected.equals(RoomEventDialog.optionList.get(RoomEventDialog.selectedOption))))
                        HandleMatchmaking.sendMessage(CardCrawlGame.playerName + " chose " + SmartTextHelper.clearSmartText(RoomEventDialog.optionList.get(RoomEventDialog.selectedOption).msg));
                    choseOption = true;
                    mySelected = RoomEventDialog.optionList.get(RoomEventDialog.selectedOption);
                    RoomEventDialog.waitForInput = true; //prevent option input

                    if (otherPlayerSelected >= 0 && isHost)
                    {
                        handleSelection();
                    }
                }
                else if (voteComplete)
                {
                    reset();
                }
            }
        }
    }


    @SpirePatch(
            clz = LargeDialogOptionButton.class,
            method = "render"
    )
    public static class RenderSelectedOptions
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = { "scale", "x", "y" }
        )
        public static void onRender(LargeDialogOptionButton __instance, SpriteBatch sb, float scale, float x, float y)
        {
            if (AbstractDungeon.player instanceof MirthAndMalice) {
                //This should be safe even if they're null.
                if (__instance.equals(mySelected)) {
                    if (((MirthAndMalice) AbstractDungeon.player).isMirth)
                    {
                        sb.setColor(Color.WHITE.cpy());
                        sb.draw(yinSelect, x - 445.0F, y - 38.5F, 445.0F, 38.5F, 890.0F, 77.0F, scale, scale, 0.0F, 0, 0, 890, 77, false, false);
                    }
                    else
                    {
                        sb.setColor(Color.WHITE.cpy());
                        sb.draw(yangSelect, x - 445.0F, y - 38.5F, 445.0F, 38.5F, 890.0F, 77.0F, scale, scale, 0.0F, 0, 0, 890, 77, false, false);
                    }
                }
                if (__instance.equals(otherSelected)) {
                    if (((MirthAndMalice) AbstractDungeon.player).isMirth)
                    {
                        sb.setColor(Color.WHITE.cpy());
                        sb.draw(yangSelect, x - 445.0F, y - 38.5F, 445.0F, 38.5F, 890.0F, 77.0F, scale, scale, 0.0F, 0, 0, 890, 77, false, false);
                    }
                    else
                    {
                        sb.setColor(Color.WHITE.cpy());
                        sb.draw(yinSelect, x - 445.0F, y - 38.5F, 445.0F, 38.5F, 890.0F, 77.0F, scale, scale, 0.0F, 0, 0, 890, 77, false, false);
                    }
                }
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(Hitbox.class, "render");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }


    public static void resolveConflict()
    {
        if (isHost)
        {
            MultiplayerHelper.sendP2PMessage("Resolving...");

            if (MathUtils.randomBoolean() && otherPlayerSelected >= 0)
            {
                selectOption(otherPlayerSelected);
                MultiplayerHelper.sendP2PString("room_option_choose" + otherPlayerSelected);
            }
            else
            {
                selectOption(RoomEventDialog.selectedOption);
                MultiplayerHelper.sendP2PString("room_option_choose" + RoomEventDialog.selectedOption);
            }
        }
    }

    private static void handleSelection()
    {
        if (otherPlayerSelected >= 0 && choseOption)
        {
            if (RoomEventDialog.selectedOption != otherPlayerSelected)
            {
                MultiplayerHelper.sendP2PMessage("Players disagree. Conflict will be automatically resolved in " + 10 + " seconds.");
                startEventChooseTimer(10.0f);
            }
            else
            {
                chat.receiveMessage("Option " + RoomEventDialog.selectedOption + ", " + SmartTextHelper.clearSmartText(RoomEventDialog.optionList.get(RoomEventDialog.selectedOption).msg) + " chosen!");
                RoomEventDialog.waitForInput = false;
                stopEventChooseTimer();
                voteComplete = true;
                MultiplayerHelper.sendP2PString("room_option_choose" + RoomEventDialog.selectedOption);
            }
        }
    }
    public static void selectOption(int index)
    {
        RoomEventDialog.selectedOption = index;
        chat.receiveMessage("Option " + RoomEventDialog.selectedOption + ", " + SmartTextHelper.clearSmartText(RoomEventDialog.optionList.get(RoomEventDialog.selectedOption).msg) + " chosen!");
        RoomEventDialog.waitForInput = false;
        voteComplete = true;
    }
}
