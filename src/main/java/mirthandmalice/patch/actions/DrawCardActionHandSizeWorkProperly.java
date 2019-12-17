package mirthandmalice.patch.actions;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.SoulGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;

@SpirePatch(
        clz = DrawCardAction.class,
        method = "update"
)
public class DrawCardActionHandSizeWorkProperly {
    private static DrawCardAction lastAction = null;
    private static boolean failure = false;
    private static float duration;

    @SpirePrefixPatch
    public static SpireReturn ensureLimits(DrawCardAction __instance)
    {
        if (lastAction == null || !lastAction.equals(__instance))
        {
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
                if (deckSize + otherDeckSize + discardSize + otherDiscardSize == 0) {
                    __instance.isDone = true;
                } else if (player.hand.size() == BaseMod.MAX_HAND_SIZE && player.otherPlayerHand.size() == BaseMod.MAX_HAND_SIZE) {
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

                        if (player.drawPileValid() && !player.isHandFull()) { //can draw from preferred draw pile?
                            if (player.tryDraw()) //try draw from preferred draw pile
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
                        else if (!player.discardPileEmpty()) { //There are still cards to draw, but this player's draw pile is empty. But, there are cards in preferred discard.
                            __instance.isDone = true;
                            AbstractDungeon.actionManager.addToTop(new DrawCardAction(__instance.source, __instance.amount));
                            AbstractDungeon.actionManager.addToTop(player.getShuffleAction());
                        }
                        else if (player.otherDrawPileValid() && !player.isOtherHandFull()) //The preferred player's hand is full, draw pile is empty and no cards in discard, or something similar.
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
                        else if (!player.otherDiscardPileEmpty())
                        {
                            __instance.isDone = true;
                            AbstractDungeon.actionManager.addToTop(new DrawCardAction(__instance.source, __instance.amount));
                            AbstractDungeon.actionManager.addToTop(player.getOtherShuffleAction());
                        }
                    }
                }
            }
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}
