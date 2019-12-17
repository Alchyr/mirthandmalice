package mirthandmalice.patch.lobby;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import javassist.CtBehavior;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.HandleMatchmaking;
import mirthandmalice.util.MultiplayerHelper;

import static mirthandmalice.MirthAndMaliceMod.*;

@SpirePatch(
        clz = CharacterSelectScreen.class,
        method = "updateButtons"
)
public class UseMultiplayerQueue {
    public static UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("LobbySearch"));

    public static boolean inQueue;

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static SpireReturn useQueue(CharacterSelectScreen __instance)
    {
        MultiplayerHelper.active = false; //ensure set to false upon embarking with other character
        gameStarted = false;
        if (CardCrawlGame.chosenCharacter != null && CardCrawlGame.chosenCharacter == CharacterEnums.MIRTHMALICE)
        {
            __instance.confirmButton.hb.clicked = false;
            inQueue = true;
            __instance.confirmButton.hide();
            __instance.cancelButton.show(uiStrings.TEXT[0]);

            logger.info("Unlocking everything, for consistency.");

            RelicLibrary.unlockAndSeeAllRelics();
            CardLibrary.unlockAndSeeAllCards();

            BaseMod.setRichPresence(uiStrings.TEXT[1]);

            /*for (int i = 0; i < CardCrawlGame.characterManager.getAllCharacters().size(); ++i)
            {
                if (CardCrawlGame.characterManager.getAllCharacters().get(i) instanceof MokouKeine)
                {
                    HandleMatchmaking.isMokou = ((MokouKeine) CardCrawlGame.characterManager.getAllCharacters().get(i)).isMokou;
                }
            }*/

            HandleMatchmaking.startFindLobby();

            lobbyMenu.show(true);

            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(Settings.class, "seed");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    @SpireInsertPatch(
            locator = SecondLocator.class
    )
    public static void leaveMultiQueue(CharacterSelectScreen __instance)
    {
        if (inQueue && (__instance.cancelButton.hb.clicked || InputHelper.pressedEscape))
        {
            leaveQueue();
            __instance.confirmButton.isDisabled = false;
            __instance.confirmButton.show();
        }
    }

    private static class SecondLocator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(InputHelper.class, "pressedEscape");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    public static void leaveQueue()
    {
        InputHelper.pressedEscape = false;
        CardCrawlGame.mainMenuScreen.charSelectScreen.cancelButton.hb.clicked = false;
        CardCrawlGame.mainMenuScreen.charSelectScreen.cancelButton.show(CharacterSelectScreen.TEXT[5]);
        inQueue = false;
        HandleMatchmaking.stop();
        CardCrawlGame.publisherIntegration.setRichPresenceDisplayInMenu();
        /*if (chat != null)
        {
            chat.receiveMessage("Left queue.");
        }*/
        lobbyMenu.hide();
    }
}
