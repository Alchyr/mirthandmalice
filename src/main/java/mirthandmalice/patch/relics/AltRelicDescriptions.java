package mirthandmalice.patch.relics;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;
import mirthandmalice.patch.enums.CharacterEnums;

import static mirthandmalice.MirthAndMaliceMod.makeID;

@SpirePatch(
        clz = AbstractRelic.class,
        method = SpirePatch.CONSTRUCTOR
)
public class AltRelicDescriptions {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void checkForAltDescriptions(AbstractRelic __instance, String id, String b, AbstractRelic.RelicTier t, AbstractRelic.LandingSound bloop)
    {
        if (AbstractDungeon.player != null && AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE)
        {
            RelicStrings alt = CardCrawlGame.languagePack.getRelicStrings(makeID(__instance.relicId));
            if (alt != null)
            {
                ReflectionHacks.setPrivate(__instance, AbstractRelic.class, "relicStrings", alt);
            }
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(RelicStrings.class, "DESCRIPTIONS");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
