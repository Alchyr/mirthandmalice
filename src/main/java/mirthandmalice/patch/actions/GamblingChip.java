package mirthandmalice.patch.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.unique.GamblingChipAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import javassist.CtBehavior;
import mirthandmalice.abstracts.ReceiveSignalCardsAction;
import mirthandmalice.actions.cards.ReceiveGamblingChipCardsAction;
import mirthandmalice.actions.character.DontUseSpecificEnergyAction;
import mirthandmalice.actions.character.ReceiveDiscardCardsAction;
import mirthandmalice.actions.character.UseSpecificEnergyAction;
import mirthandmalice.actions.character.WaitForSignalAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.combat.HandCardSelectReordering;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.MultiplayerHelper;

import java.util.ArrayList;
import java.util.HashMap;

import static mirthandmalice.util.MultiplayerHelper.partnerName;
import static mirthandmalice.MirthAndMaliceMod.makeID;

@SpirePatch(
        clz = GamblingChipAction.class,
        method = "update"
)
public class GamblingChip {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("DiscardWait"));

    private static GamblingChipAction currentAction = null;
    private static int handIndex = -1;
    private static boolean mustSignal = false;

    @SpirePrefixPatch
    public static SpireReturn waitForPlayer(GamblingChipAction __instance)
    {
        if (currentAction != __instance)
        {
            currentAction = __instance;
            handIndex = -1;
            mustSignal = false;

            if (AbstractDungeon.player instanceof MirthAndMalice && MultiplayerHelper.active)
            {
                if (TrackCardSource.useOtherEnergy) //triggered by other player.
                {
                    AbstractDungeon.actionManager.addToTop(new ReceiveGamblingChipCardsAction());
                    AbstractDungeon.actionManager.addToTop(new WaitForSignalAction(uiStrings.TEXT[0] + partnerName + uiStrings.TEXT[1]));
                    __instance.isDone = true;
                    return SpireReturn.Return(null);
                }
                else if (TrackCardSource.useMyEnergy)
                {
                    //Track card indexes in hand for later use
                    HandCardSelectReordering.saveHandPreOpenScreen();
                    mustSignal = true;
                }
                else //This is the initial gambling chip action.
                {
                    boolean notChip = (boolean) ReflectionHacks.getPrivate(__instance, GamblingChipAction.class, "notchip");
                    if (((MirthAndMalice) AbstractDungeon.player).isMirth)
                    {
                        TrackCardSource.useMyEnergy = true;
                        __instance.isDone = true;
                        AbstractDungeon.actionManager.addToTop(new DontUseSpecificEnergyAction()); //reset
                        AbstractDungeon.actionManager.addToTop(new GamblingChipAction(__instance.source, notChip)); //other gambling chip action, will happen second.
                        AbstractDungeon.actionManager.addToTop(new UseSpecificEnergyAction(true)); //switch to other
                        AbstractDungeon.actionManager.addToTop(new GamblingChipAction(__instance.source, notChip)); //my gambling chip action, will happen first.
                    }
                    else
                    {
                        TrackCardSource.useOtherEnergy = true;
                        __instance.isDone = true;
                        AbstractDungeon.actionManager.addToTop(new DontUseSpecificEnergyAction()); //reset
                        AbstractDungeon.actionManager.addToTop(new GamblingChipAction(__instance.source, notChip)); //my gambling chip action, will happen second.
                        AbstractDungeon.actionManager.addToTop(new UseSpecificEnergyAction(false)); //switch to self
                        AbstractDungeon.actionManager.addToTop(new GamblingChipAction(__instance.source, notChip)); //other gambling chip action, will happen first.
                    }
                    return SpireReturn.Return(null);
                }
            }
        }
        return SpireReturn.Continue();
    }

    /*@SpireInsertPatch(
        locator = DrawLocator.class
    )
    public static void reportDraw(GamblingChipAction __instance)
    {
        if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
        {
            //number of drawn cards:
            AbstractDungeon.handCardSelectScreen.selectedCards.group.size()
        }
    }*/

    @SpireInsertPatch(
        locator = DiscardLocator.class,
            localvars = { "c" }
    )
    public static void reportDiscards(GamblingChipAction __instance, AbstractCard c)
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
    public static void finish(GamblingChipAction __instance)
    {
        if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active && mustSignal && __instance.isDone)
        {
            MultiplayerHelper.sendP2PString("signal");
        }
    }


    private static class DrawLocator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.NewExprMatcher(DrawCardAction.class);
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
    private static class DiscardLocator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(GameActionManager.class, "incrementDiscard");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
