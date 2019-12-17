package mirthandmalice.patch.card_use;

import basemod.ReflectionHacks;
import com.badlogic.gdx.utils.Queue;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.HandleMatchmaking;

import java.util.ArrayList;

import static mirthandmalice.util.MultiplayerHelper.sendP2PString;
import static mirthandmalice.util.MultiplayerHelper.tryOtherPlayCard;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "playCard"
)
public class PlayCardCheck {
    public static Queue<String> failedAttempts = new Queue<>();

    @SpireInsertPatch (
            locator = Locator.class
    )
    public static SpireReturn playAlt(AbstractPlayer __instance)
    {
        if (__instance.chosenClass == CharacterEnums.MIRTHMALICE)
        {
            if (HandleMatchmaking.isHost)
            {
                if (failedAttempts.size > 0)
                {
                    int amt = failedAttempts.size;
                    for (int i=0;i<amt;++i)
                    {
                        String args = failedAttempts.removeFirst();
                        if (tryOtherPlayCard(args))
                        {
                            sendP2PString("confirm_play_card" + args);
                        }
                    }
                }

                if (__instance.hoveredCard.target != AbstractCard.CardTarget.ENEMY && __instance.hoveredCard.target != AbstractCard.CardTarget.SELF_AND_ENEMY)
                {
                    sendP2PString("other_play_card" + AbstractDungeon.player.hand.group.indexOf(__instance.hoveredCard) + " " + __instance.hoveredCard.cardID + " -1 " + __instance.hoveredCard.current_x + " " + __instance.hoveredCard.current_y);
                }
                else
                {
                    AbstractMonster target = (AbstractMonster)ReflectionHacks.getPrivate(__instance, AbstractPlayer.class, "hoveredMonster");
                    sendP2PString("other_play_card" + AbstractDungeon.player.hand.group.indexOf(__instance.hoveredCard) + " " + __instance.hoveredCard.cardID + " " + (target != null ? AbstractDungeon.getMonsters().monsters.indexOf(target) : "-1") + " " + __instance.hoveredCard.current_x + " " + __instance.hoveredCard.current_y);
                }
                //send play card message to other player
            }
            else //this isn't host - has to get permission in order to ensure everything occurs in order.
            {
                if (failedAttempts.size > 0)
                {
                    int amt = failedAttempts.size;
                    for (int i=0;i<amt;++i)
                    {
                        String args = failedAttempts.removeFirst();
                        tryOtherPlayCard(args);
                    }
                }


                if (__instance.hoveredCard.target != AbstractCard.CardTarget.ENEMY && __instance.hoveredCard.target != AbstractCard.CardTarget.SELF_AND_ENEMY)
                {
                    sendP2PString("try_play_card" + AbstractDungeon.player.hand.group.indexOf(__instance.hoveredCard) + " " + __instance.hoveredCard.cardID + " -1 " + __instance.hoveredCard.current_x + " " + __instance.hoveredCard.current_y);
                }
                else
                {
                    AbstractMonster target = (AbstractMonster)ReflectionHacks.getPrivate(__instance, AbstractPlayer.class, "hoveredMonster");
                    sendP2PString("try_play_card" + AbstractDungeon.player.hand.group.indexOf(__instance.hoveredCard) + " " + __instance.hoveredCard.cardID + " " + (target != null ? AbstractDungeon.getMonsters().monsters.indexOf(target) : "-1") + " " + __instance.hoveredCard.current_x + " " + __instance.hoveredCard.current_y);
                }
                //send play card message to other player
                ReflectionHacks.setPrivate(__instance, AbstractPlayer.class, "isUsingClickDragControl", false); //handle rest of method
                __instance.hoveredCard = null;
                __instance.isDraggingCard = false;
                return SpireReturn.Return(null); //prevent playing of card
            }
        }
        return SpireReturn.Continue();
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");
            return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}