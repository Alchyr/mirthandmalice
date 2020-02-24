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

import static mirthandmalice.MirthAndMaliceMod.FULL_DEBUG;
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
        private static float duration;

        private enum DRAWTARGET {
            NEUTRAL,
            SELF,
            OTHER
        }

        @SpirePrefixPatch
        public static SpireReturn ensureLimits(com.megacrit.cardcrawl.actions.common.DrawCardAction __instance)
        {
            int forceDraw = DrawFields.forceDraw.get(__instance);
            DRAWTARGET drawTarget = forceDraw == 0 ? DRAWTARGET.NEUTRAL : (forceDraw > 0 ? DRAWTARGET.SELF : DRAWTARGET.OTHER);

            if (!__instance.equals(lastAction))
            {
                logger.info("Starting draw action. Amount: " + __instance.amount + " Draw target: " + drawTarget.name());
                lastAction = __instance;
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
                    if ((drawTarget != DRAWTARGET.OTHER ? deckSize + discardSize : 0) +
                            (drawTarget != DRAWTARGET.SELF ? otherDeckSize + otherDiscardSize : 0) == 0) {
                        logger.info("No cards to draw.");

                        __instance.isDone = true;
                    } else if ((drawTarget == DRAWTARGET.OTHER || player.hand.size() == BaseMod.MAX_HAND_SIZE)
                            && (drawTarget == DRAWTARGET.SELF || player.otherPlayerHand.size() == BaseMod.MAX_HAND_SIZE)) {
                        //Either both hands are full, or draw target's hand is full
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

                                    if (FULL_DEBUG)
                                        logger.info("Drew 1 card.");

                                    if (__instance.amount == 0) {
                                        __instance.isDone = true;
                                    }
                                } //These failures should never occur, but just in case.
                                else //tried to draw, but failed.
                                {
                                    if (FULL_DEBUG)
                                        logger.info("Failed to draw for intended player.");

                                    __instance.isDone = true;
                                }
                            }
                            else if (!player.forceDiscardPileEmpty(forceDraw) && !player.forceIsHandFull(forceDraw)) { //There are still cards to draw and hand is not full, but this player's draw pile is empty. But, there are cards in preferred discard.
                                __instance.isDone = true;
                                DrawCardAction remainder = new DrawCardAction(__instance.source, __instance.amount);
                                DrawFields.forceDraw.set(remainder, forceDraw);

                                if (FULL_DEBUG)
                                    logger.info("Reshuffling draw pile. " + __instance.amount + " cards remain to draw.");

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

                                    if (FULL_DEBUG)
                                        logger.info("Drew 1 card from alternate pile due to draw failure.");

                                    if (__instance.amount == 0) {
                                        __instance.isDone = true;
                                    }
                                } //These failures should never occur, but just in case.
                                else //tried to draw, but failed. Will attempt draw from other player next.
                                {
                                    if (FULL_DEBUG)
                                        logger.info("Failed to draw from alternate pile.");

                                    __instance.isDone = true;
                                }
                            }
                            else if (forceDraw == 0 && !player.otherDiscardPileEmpty() && !player.isOtherHandFull())
                            {
                                if (FULL_DEBUG)
                                    logger.info("Reshuffling alternate pile.");

                                __instance.isDone = true;
                                AbstractDungeon.actionManager.addToTop(new DrawCardAction(__instance.source, __instance.amount));
                                AbstractDungeon.actionManager.addToTop(player.getOtherShuffleAction());
                            }
                            else //complete failure
                            {
                                if (FULL_DEBUG)
                                    logger.info("COMPLETE DRAW FAILURE.");
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
