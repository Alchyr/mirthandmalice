package mirthandmalice.patch.gold;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import javassist.CtBehavior;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.MultiplayerHelper;

public class TrackGoldChanges {
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "gainGold"
    )
    public static class TrackGainGold {
        //Report gain
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void triggerRelicsOnGain(AbstractPlayer __instance, int gain)
        {
            if (__instance.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
            {
                MultiplayerHelper.sendP2PString("gain_gold");
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardCrawlGame.class, "goldGained");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        //Track gold values
        @SpirePostfixPatch
        public static void reportGain(AbstractPlayer __instance, int gain)
        {
            //report new value of __instance.gold
            if (__instance.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
            {
                MultiplayerHelper.sendP2PString("gold" + __instance.gold);
            }
        }
    }
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "loseGold"
    )
    public static class TrackLostGold {
        //Report loss
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void triggerRelicsOnLoss(AbstractPlayer __instance, int loss)
        {
            if (__instance.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
            {
                MultiplayerHelper.sendP2PString("lose_gold");
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "gold");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }


        @SpirePostfixPatch
        public static void reportLoss(AbstractPlayer __instance, int gain)
        {
            if (__instance.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
            {
                MultiplayerHelper.sendP2PString("gold" + __instance.gold);
            }
        }
    }
}
