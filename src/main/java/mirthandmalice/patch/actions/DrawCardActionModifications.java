package mirthandmalice.patch.actions;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.SoulGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;

import static mirthandmalice.MirthAndMaliceMod.logger;

public class DrawCardActionModifications {
    @SpirePatch(
            clz = DrawCardAction.class,
            method = SpirePatch.CLASS
    )
    public static class DrawFields {
        public static SpireField<Integer> forceDraw = new SpireField<>(()->0); //force this to only draw for a specific player. 0 for disable, 1 for self, -1 for other.

    }

    @SpirePatch(
            clz = com.megacrit.cardcrawl.actions.common.DrawCardAction.class,
            method = "update"
    )
    public static class AlternateAction {
        private static com.megacrit.cardcrawl.actions.common.DrawCardAction lastAction = null;
        private static boolean failure = false;
        private static float duration;

        @SpirePrefixPatch
        public static SpireReturn ensureLimits(com.megacrit.cardcrawl.actions.common.DrawCardAction __instance)
        {
            int forceDraw = DrawFields.forceDraw.get(__instance);
            if (!__instance.equals(lastAction))
            {
                logger.info("New draw action. Amount: " + __instance.amount + " Draw target: " + (forceDraw == 0 ? "Neutral" : (forceDraw > 0 ? "Self" : "Other")));
                lastAction = __instance;
                failure = false;
                if (Settings.FAST_MODE) {
                    duration = Settings.ACTION_DUR_XFAST;
                } else {
                    duration = Settings.ACTION_DUR_FASTER;
                }
            }
            if (__instance.amount > 0 && AbstractDungeon.player instanceof MirthAndMalice)
            {
                MirthAndMalice player = (MirthAndMalice)AbstractDungeon.player;

                int deckSize = player.drawPile.size();
                int discardSize = player.discardPile.size();
                int otherDiscardSize = player.otherPlayerDiscard.size();
                int otherDeckSize = player.otherPlayerDraw.size();

                if (!SoulGroup.isActive()) {
                    if ((forceDraw >= 0 ? deckSize + discardSize : 0) +
                            (forceDraw <= 0  ? otherDeckSize + otherDiscardSize : 0) == 0) {
                        logger.info("No cards to draw.");
                        __instance.isDone = true;
                    } else if ((forceDraw < 0 || player.hand.size() == BaseMod.MAX_HAND_SIZE)
                            && (forceDraw > 0 || player.otherPlayerHand.size() == BaseMod.MAX_HAND_SIZE)) {
                        logger.info("Target hand is full.");
                        AbstractDungeon.player.createHandIsFullDialog();
                        __instance.isDone = true;
                    } else {
                        duration -= Gdx.graphics.getDeltaTime();
                        if (__instance.amount > 0 && duration < 0.0F) {
                            if (Settings.FAST_MODE) {
                                duration = Settings.ACTION_DUR_XFAST;
                            } else {
                                duration = Settings.ACTION_DUR_FASTER;
                            }


                            if (player.forceDrawPileValid(forceDraw) && !player.forceIsHandFull(forceDraw)) { //can draw from preferred draw pile?
                                if (player.forceTryDraw(forceDraw)) //try draw from preferred draw pile
                                {
                                    --__instance.amount;
                                    AbstractDungeon.player.hand.refreshHandLayout(); //is patched to refresh both hands.
                                    failure = false;

                                    if (__instance.amount == 0) {
                                        __instance.isDone = true;
                                    }
                                } //These failures should never occur, but just in case.
                                else if (failure) //Draw failed. Since drawPileValid passed, that means their hands are full. This shouldn't occur, since this is checked for earlier, but safety.
                                {
                                    __instance.isDone = true;
                                }
                                else //tried to draw, but failed. Will attempt draw from other player next, if not using force draw.
                                {
                                    failure = true;
                                }
                            }
                            else if (!player.forceDiscardPileEmpty(forceDraw)) { //There are still cards to draw, but this player's draw pile is empty. But, there are cards in preferred discard.
                                __instance.isDone = true;
                                DrawCardAction remainder = new DrawCardAction(__instance.source, __instance.amount);
                                DrawFields.forceDraw.set(remainder, forceDraw);
                                AbstractDungeon.actionManager.addToTop(remainder);
                                AbstractDungeon.actionManager.addToTop(player.forceGetShuffleAction(forceDraw));
                            }
                            else if (forceDraw == 0 && player.otherDrawPileValid() && !player.isOtherHandFull()) //The preferred player's hand is full, draw pile is empty and no cards in discard, or something similar.
                            {
                                //Try other player's draw pile.
                                if (player.tryOtherDraw()) //try draw from other draw pile
                                {
                                    --__instance.amount;
                                    AbstractDungeon.player.hand.refreshHandLayout();
                                    failure = false;

                                    if (__instance.amount == 0) {
                                        __instance.isDone = true;
                                    }
                                } //These failures should never occur, but just in case.
                                else if (failure) //neither player can draw. Since drawPileValid passed, that means their hands are full. This shouldn't occur, since this is checked for earlier, but safety.
                                {
                                    __instance.isDone = true;
                                }
                                else //tried to draw, but failed. Will attempt draw from other player next.
                                {
                                    failure = true;
                                }
                            }
                            else if (forceDraw == 0 && !player.otherDiscardPileEmpty())
                            {
                                __instance.isDone = true;
                                AbstractDungeon.actionManager.addToTop(new com.megacrit.cardcrawl.actions.common.DrawCardAction(__instance.source, __instance.amount));
                                AbstractDungeon.actionManager.addToTop(player.getOtherShuffleAction());
                            }
                            else //complete failure
                            {
                                __instance.isDone = true;
                            }
                        }
                    }
                }
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
