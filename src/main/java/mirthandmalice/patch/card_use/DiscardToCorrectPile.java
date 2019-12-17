package mirthandmalice.patch.card_use;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.combat.SoulAltDiscard;
import mirthandmalice.patch.combat.SoulAltOnToDeck;
import mirthandmalice.util.OtherPlayerCardQueueItem;

public class DiscardToCorrectPile {
    public static boolean useOtherDiscard = false;

    @SpirePatch(
            clz = GameActionManager.class,
            method = "getNextAction"
    )
    public static class onPlayerUseCard
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void setDiscardPile(GameActionManager __instance)
        {
            if (__instance.cardQueue.get(0) instanceof OtherPlayerCardQueueItem)
            {
                useOtherDiscard = true;
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "useCard");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = UseCardAction.class,
            method = "update"
    )
    public static class useCorrectPiles
    {
        @SpireInsertPatch(
                locator = DiscardLocator.class
        )
        public static void moveAltDiscard(UseCardAction __instance)
        {
            if (UseCardActionDestination.useAlternatePile.get(__instance) && AbstractDungeon.player instanceof MirthAndMalice)
            {
                SoulAltDiscard.altGroup = ((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard;
                //SoulAltDiscard modifies Soul.discard, which is used by moveToDiscardPile.
            }
        }

        @SpireInsertPatch(
                locator = DrawLocator.class
        )
        public static void moveAltDraw(UseCardAction __instance)
        {
            if (UseCardActionDestination.useAlternatePile.get(__instance) && AbstractDungeon.player instanceof MirthAndMalice)
            {
                SoulAltOnToDeck.altGroup = ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw;
                //SoulAltDiscard modifies Soul.discard, which is used by moveToDiscardPile.
            }
        }

        @SpireInsertPatch(
                locator = ReturnLocator.class
        )
        public static SpireReturn returnToHand(UseCardAction __instance)
        {
            if (UseCardActionDestination.returnHand.get(__instance))
            {
                AbstractCard c = (AbstractCard) ReflectionHacks.getPrivate(__instance, UseCardAction.class, "targetCard");
                ReflectionHacks.setPrivate(__instance, AbstractGameAction.class, "duration", 0.1f);
                if (UseCardActionDestination.useAlternatePile.get(__instance) && AbstractDungeon.player instanceof MirthAndMalice)
                {
                    if (!((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.group.contains(c))
                        ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.group.add(c);
                }
                else
                {
                    if (!AbstractDungeon.player.hand.group.contains(c))
                        AbstractDungeon.player.hand.group.add(c);
                }
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }


        //For future - simply set to true if useAlternatePile is true at start of update, set to false at end
        @SpirePostfixPatch
        public static void resetVars(UseCardAction __instance)
        {
            //ensuring that if anything else prevents moveToDiscard or moveToDraw or whatever else from being called, the modified groups aren't used on next calls.
            SoulAltDiscard.altGroup = null;
            SoulAltOnToDeck.altGroup = null;
        }


        private static class DiscardLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "moveToDiscardPile");
                return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            }
        }
        private static class DrawLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "moveToDeck");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
        private static class ReturnLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "type");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    public static void reset()
    {
        useOtherDiscard = false;
    }
}
