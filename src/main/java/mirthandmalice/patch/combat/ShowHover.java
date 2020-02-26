package mirthandmalice.patch.combat;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.util.MultiplayerHelper;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "update"
)
public class ShowHover {
    private static boolean wasHoveringCard = false;
    private static boolean wasDraggingCard = false;
    private static AbstractCard lastHoveredCard = null;


    public static AbstractCard otherHoveredCard = null;
    public static boolean isDragging = false;

    @SpirePostfixPatch
    public static void reportHover(AbstractPlayer __instance, boolean ___isHoveringCard)
    {
        if (MultiplayerHelper.active) //this is occurring every frame so I'd like to keep the check simple.
        {
            if (__instance.hoveredCard != null)
            {
                if (__instance.hoveredCard != lastHoveredCard)
                {
                    if (__instance.hand.contains(__instance.hoveredCard))
                    {
                        if (__instance.isDraggingCard)
                        {
                            MultiplayerHelper.sendP2PString("drag" + AbstractDungeon.player.hand.group.indexOf(__instance.hoveredCard));
                        }
                        else
                        {
                            MultiplayerHelper.sendP2PString("hover" + AbstractDungeon.player.hand.group.indexOf(__instance.hoveredCard));
                        }
                    }
                    else
                    {
                        MultiplayerHelper.sendP2PString("stophover");
                    }
                    lastHoveredCard = __instance.hoveredCard;
                }
                else
                {
                    if (__instance.isDraggingCard && !wasDraggingCard)
                    {
                        MultiplayerHelper.sendP2PString("drag");
                    }
                }
            }
            else
            {
                if (wasHoveringCard)
                {
                    MultiplayerHelper.sendP2PString("stophover");
                }
            }

            wasHoveringCard = ___isHoveringCard;
            wasDraggingCard = __instance.isDraggingCard;


            if (otherHoveredCard != null)
            {
                if (isDragging)
                {

                }
                else
                {
                    ((MirthAndMalice)AbstractDungeon.player).otherPlayerHand.hoverCardPush(otherHoveredCard);
                }
            }
        }
    }
}
