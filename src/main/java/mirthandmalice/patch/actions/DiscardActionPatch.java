package mirthandmalice.patch.actions;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.unique.GamblingChipAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import javassist.CtBehavior;
import mirthandmalice.abstracts.ReceiveSignalCardsAction;
import mirthandmalice.actions.cards.ReceiveGamblingChipCardsAction;
import mirthandmalice.actions.character.ReceiveDiscardCardsAction;
import mirthandmalice.actions.character.WaitForSignalAction;
import mirthandmalice.patch.combat.HandCardSelectReordering;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.MultiplayerHelper;

import java.util.ArrayList;

import static mirthandmalice.MirthAndMaliceMod.makeID;
import static mirthandmalice.util.MultiplayerHelper.partnerName;

@SpirePatch(
        clz = DiscardAction.class,
        method = "update"
)
public class DiscardActionPatch {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("DiscardWait"));

    private static DiscardAction currentAction = null;
    private static int handIndex = -1;

    @SpirePrefixPatch
    public static void trackInstance(DiscardAction __instance)
    {
        if (currentAction != __instance) {
            currentAction = __instance;
            handIndex = -1;
        }
    }


    @SpireInsertPatch(
            locator = HandSelectOpenLocator.class
    )
    public static SpireReturn saveOnOpen(DiscardAction __instance)
    {
        if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
        {
            if (TrackCardSource.useOtherEnergy) //triggered by other player.
            {
                AbstractDungeon.actionManager.addToTop(new ReceiveDiscardCardsAction());
                AbstractDungeon.actionManager.addToTop(new WaitForSignalAction(uiStrings.TEXT[0] + partnerName + uiStrings.TEXT[1]));
                __instance.isDone = true;
                return SpireReturn.Return(null);
            }
            else
            {
                //Track card indexes in hand for later use
                HandCardSelectReordering.saveHandPreOpenScreen();
            }
        }
        return SpireReturn.Continue();
    }


    @SpireInsertPatch(
            locator = DiscardLocator.class,
            localvars = { "c" }
    )
    public static void reportDiscards(DiscardAction __instance, AbstractCard c)
    {
        if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
        {
            if (handIndex < 0)
            {
                handIndex = AbstractDungeon.player.hand.size();
            }

            MultiplayerHelper.sendP2PString(ReceiveSignalCardsAction.signalCardString(handIndex++, AbstractDungeon.player.hand, true));
        }
    }

    @SpirePostfixPatch
    public static void finish(DiscardAction __instance)
    {
        if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active && __instance.isDone)
        {
            MultiplayerHelper.sendP2PString("signal");
        }
    }


    private static class HandSelectOpenLocator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(HandCardSelectScreen.class, "open");
            return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
        }
    }
    private static class DiscardLocator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(GameActionManager.class, "incrementDiscard");
            ArrayList<Matcher> matchers = new ArrayList<>();
            matchers.add(finalMatcher);
            matchers.add(finalMatcher);
            return LineFinder.findInOrder(ctMethodToPatch, matchers, finalMatcher);
        }
    }
    private static class EndLocator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(HandCardSelectScreen.class, "wereCardsRetrieved");
            return new int[] { LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[1] };
        }
    }
}
