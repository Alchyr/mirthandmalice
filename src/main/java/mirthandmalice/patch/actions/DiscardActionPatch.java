package mirthandmalice.patch.actions;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.unique.GamblingChipAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import javassist.CtBehavior;
import mirthandmalice.abstracts.ReceiveSignalCardsAction;
import mirthandmalice.actions.cards.ReceiveGamblingChipCardsAction;
import mirthandmalice.actions.character.ReceiveDiscardCardsAction;
import mirthandmalice.actions.character.WaitForSignalAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.combat.HandCardSelectReordering;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.MultiplayerHelper;

import java.util.ArrayList;

import static mirthandmalice.MirthAndMaliceMod.makeID;
import static mirthandmalice.actions.character.OtherPlayerDiscardAction.moveToAltDiscard;
import static mirthandmalice.util.MultiplayerHelper.partnerName;

@SpirePatch(
        clz = DiscardAction.class,
        method = "update"
)
public class DiscardActionPatch {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("DiscardWait"));

    private static DiscardAction currentAction = null;
    private static boolean enabled = false;
    private static boolean waiting = false;
    private static int handIndex = -1;

    @SpirePrefixPatch
    public static void trackInstance(DiscardAction __instance)
    {
        if (currentAction != __instance) {
            enabled = AbstractDungeon.player instanceof MirthAndMalice;
            currentAction = __instance;
            waiting = false;
            handIndex = -1;
        }
    }

    @SpireInsertPatch(
            locator = DiscardStartLocator.class,
            localvars = { "endTurn", "isRandom" }
    )
    public static SpireReturn alternateDiscard(DiscardAction __instance, boolean endTurn, boolean isRandom)
    {
        if (enabled)
        {
            if (TrackCardSource.useOtherEnergy && AbstractDungeon.player instanceof MirthAndMalice) //triggered by other player, so it is their discard
            {
                MirthAndMalice p = (MirthAndMalice)AbstractDungeon.player;
                int i;
                AbstractCard c;

                if (p.otherPlayerHand.size() <= __instance.amount) {
                    __instance.amount = p.otherPlayerHand.size();
                    i = p.otherPlayerHand.size();

                    for(int n = 0; n < i; ++n) {
                        c = p.otherPlayerHand.getTopCard();
                        moveToAltDiscard(p.otherPlayerHand, p.otherPlayerDiscard, c);
                        if (!endTurn) {
                            c.triggerOnManualDiscard();
                        }

                        GameActionManager.incrementDiscard(endTurn);
                    }

                    p.hand.applyPowers();


                    float newDuration = (float)ReflectionHacks.getPrivate(__instance, AbstractGameAction.class, "duration") - Gdx.graphics.getDeltaTime();
                    ReflectionHacks.setPrivate(__instance, AbstractGameAction.class, "duration", newDuration);
                    if (newDuration < 0.0f)
                        __instance.isDone = true;
                    return SpireReturn.Return(null);
                }

                if (isRandom)
                {
                    for(i = 0; i < __instance.amount; ++i) {
                        c = p.otherPlayerHand.getRandomCard(true);
                        moveToAltDiscard(p.otherPlayerHand, p.otherPlayerDiscard, c);
                        c.triggerOnManualDiscard();
                        GameActionManager.incrementDiscard(endTurn);
                    }


                    float newDuration = (float)ReflectionHacks.getPrivate(__instance, AbstractGameAction.class, "duration") - Gdx.graphics.getDeltaTime();
                    ReflectionHacks.setPrivate(__instance, AbstractGameAction.class, "duration", newDuration);
                    if (newDuration < 0.0f)
                        __instance.isDone = true;
                }
                else
                {
                    AbstractDungeon.actionManager.addToTop(new ReceiveDiscardCardsAction());
                    AbstractDungeon.actionManager.addToTop(new WaitForSignalAction(uiStrings.TEXT[0] + partnerName + uiStrings.TEXT[1]));
                    __instance.isDone = true;
                }
                return SpireReturn.Return(null);
            }
        }
        return SpireReturn.Continue();
    }

    @SpireInsertPatch(
            locator = HandSelectOpenLocator.class
    )
    public static SpireReturn saveOnOpen(DiscardAction __instance)
    {
        if (enabled)
        {
            waiting = true;
            HandCardSelectReordering.saveHandPreOpenScreen();
        }
        return SpireReturn.Continue();
    }


    @SpireInsertPatch(
            locator = DiscardLocator.class,
            localvars = { "c" }
    )
    public static void reportDiscards(DiscardAction __instance, AbstractCard c)
    {
        if (enabled)
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
        if (enabled && __instance.isDone && waiting)
        {
            MultiplayerHelper.sendP2PString("signal");
        }
        if (waiting)
        {
            if (__instance.isDone)
                waiting = false;
        }
    }


    private static class DiscardStartLocator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "size");
            return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
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
}
