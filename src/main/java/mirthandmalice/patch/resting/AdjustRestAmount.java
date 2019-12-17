package mirthandmalice.patch.resting;

import basemod.ReflectionHacks;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.NightTerrors;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.RegalPillow;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.ui.campfire.RestOption;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSleepEffect;
import javassist.CtBehavior;
import mirthandmalice.patch.enums.CharacterEnums;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class AdjustRestAmount {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("Resting"));
    public static final String[] TEXT = uiStrings.TEXT;

    @SpirePatch(
            clz = RestOption.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class AdjustDisplay
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = { "healAmt" }
        )
        public static void adjustAmount(RestOption __instance, boolean active, @ByRef int[] healAmt)
        {
            if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE);
            {
                healAmt[0] = MathUtils.round(healAmt[0] * 0.5f); //30% -> 15% or 100% -> 50%
            }
        }

        @SpireInsertPatch(
                locator = UpdateDescriptionLocator.class,
                localvars = { "healAmt" }
        )
        public static void adjustDescription(RestOption __instance, boolean active, int healAmt)
        {
            if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE);
            {
                String text;
                if (ModHelper.isModEnabled(NightTerrors.ID)) {
                    text = TEXT[0] + healAmt + ").";
                } else {
                    text = TEXT[2] + healAmt + ").";
                }
                if (AbstractDungeon.player.hasRelic(RegalPillow.ID)) {
                    text += "\n+15" + TEXT[1] + AbstractDungeon.player.getRelic(RegalPillow.ID).name + LocalizedStrings.PERIOD;
                }
                ReflectionHacks.setPrivate(__instance, AbstractCampfireOption.class, "description", text);
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(Settings.class, "isEndless");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
        private static class UpdateDescriptionLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(RestOption.class, "updateUsability");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = CampfireSleepEffect.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class AdjustHeal
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = { "healAmount" }
        )
        public static void adjustAmount(CampfireSleepEffect __instance, @ByRef int[] healAmount)
        {
            if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE);
            {
                healAmount[0] = MathUtils.round(healAmount[0] * 0.5f); //30% -> 15% or 100% -> 50%
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
