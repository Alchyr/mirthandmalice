package mirthandmalice.patch.potions;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.potions.FairyPotion;
import com.megacrit.cardcrawl.relics.MarkOfTheBloom;
import javassist.CtBehavior;
import mirthandmalice.patch.combat.PotionUse;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.HandleMatchmaking;
import mirthandmalice.util.MultiplayerHelper;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "damage"
)
public class Fairy {
    @SpireInsertPatch(
            locator = HostLocator.class
    )
    public static SpireReturn HostPotionsFirst(AbstractPlayer __instance, DamageInfo info)
    {
        if (MultiplayerHelper.active && !HandleMatchmaking.isHost && __instance.chosenClass == CharacterEnums.MIRTHMALICE) //host potions are used first
        {
            if (MultiplayerHelper.otherPlayerPotions.contains(FairyPotion.POTION_ID)) //host's potion will be used automatically
            {
                __instance.currentHealth = 0;
                PotionUse.usePotion(FairyPotion.POTION_ID + " -2");
                return SpireReturn.Return(null);
            }
        }
        return SpireReturn.Continue();
    }

    @SpireInsertPatch(
            locator = OtherLocatorA.class
    )
    public static SpireReturn TryOtherBeforeLizardTail(AbstractPlayer __instance, DamageInfo info)
    {
        if (MultiplayerHelper.active && HandleMatchmaking.isHost && __instance.chosenClass == CharacterEnums.MIRTHMALICE) //I am the host and I don't have fairy potions.
        {
            if (MultiplayerHelper.otherPlayerPotions.contains(FairyPotion.POTION_ID)) //how bout you, pardner
            {
                PotionUse.otherUsePotion(FairyPotion.POTION_ID + "-1 -2");
                return SpireReturn.Return(null);
            }
        }
        return SpireReturn.Continue();
    }
    @SpireInsertPatch(
            locator = OtherLocatorB.class
    )
    public static SpireReturn TheVeryLastChance(AbstractPlayer __instance, DamageInfo info)
    {
        if (MultiplayerHelper.active && HandleMatchmaking.isHost && !__instance.hasRelic(MarkOfTheBloom.ID) && __instance.chosenClass == CharacterEnums.MIRTHMALICE) //I am the host and I don't have fairy potions.
        {
            if (MultiplayerHelper.otherPlayerPotions.contains(FairyPotion.POTION_ID)) //how bout you, pardner
            {
                __instance.currentHealth = 0;
                PotionUse.otherUsePotion(FairyPotion.POTION_ID + "-1 -2");
                return SpireReturn.Return(null);
            }
        }
        return SpireReturn.Continue();
    }


    private static class HostLocator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasPotion");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }


    private static class OtherLocatorA extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "getRelic");
            return new int[] { LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[1] };
        }
    }
    private static class OtherLocatorB extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "isDead");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
