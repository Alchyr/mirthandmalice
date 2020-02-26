/*package mirthandmalice.patch.updateOtherCardGroup;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import javassist.CtBehavior;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.combat.ShowHover;
import mirthandmalice.patch.enums.CharacterEnums;

import java.util.ArrayList;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "updateInput"
)
public class HoverOtherHand {
    @SpireInsertPatch(
            locator = FirstLocator.class
    )
    public static void GetHoverOtherHand(AbstractPlayer __instance)
    {
        if (__instance.chosenClass == CharacterEnums.MIRTHMALICE && __instance instanceof MirthAndMalice)
        {
            if (__instance.hoveredCard == null)
            {
                __instance.hoveredCard = ((MirthAndMalice) __instance).otherPlayerHand.getHoveredCard();
            }
        }
    }

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void TestHoverOtherHand(AbstractPlayer __instance)
    {
        if (__instance.chosenClass == CharacterEnums.MIRTHMALICE && __instance instanceof MirthAndMalice)
        {
            if (__instance.toHover == null)
            {
                for(int i = 0; i < ((MirthAndMalice) __instance).otherPlayerHand.group.size(); ++i) {// 995
                    if (((MirthAndMalice) __instance).otherPlayerHand.group.get(i) == __instance.hoveredCard && i != 0 && ((MirthAndMalice) __instance).otherPlayerHand.group.get(i - 1).isHoveredInHand(1.0F)) {// 996 997
                        __instance.toHover = ((MirthAndMalice) __instance).otherPlayerHand.group.get(i - 1);// 998
                        break;// 999
                    }
                }
            }
        }
    }

    private static class FirstLocator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            ArrayList<Matcher> requiredMatches = new ArrayList<>();
            requiredMatches.add(new Matcher.MethodCallMatcher(CardGroup.class, "getHoveredCard"));
            requiredMatches.add(new Matcher.FieldAccessMatcher(AbstractPlayer.class, "hoveredCard"));

            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "hoveredCard");

            return LineFinder.findInOrder(ctMethodToPatch, requiredMatches, finalMatcher);
        }
    }
    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "releaseCard");

            return new int[] { LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[2] };
        }
    }
}
*/