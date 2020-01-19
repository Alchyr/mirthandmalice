package mirthandmalice.patch.manifestation;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.stances.AbstractStance;
import javassist.CtBehavior;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.energy_division.TrackCardSource;

import java.util.ArrayList;

public class ModifyCalculations {
    private static final float NOT_MANIFESTED_DEBUFF = 0.5f;

    private static float applyManifestation(AbstractCard card, float value)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (AbstractDungeon.actionManager.usingCard && !AbstractDungeon.actionManager.cardsPlayedThisCombat.isEmpty() && AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1).equals(card))
            {
                if (TrackCardSource.useMyEnergy && ManifestField.otherManifested())
                {
                    value *= NOT_MANIFESTED_DEBUFF;
                }
                else if (TrackCardSource.useOtherEnergy && ManifestField.isManifested())
                {
                    value *= NOT_MANIFESTED_DEBUFF;
                }
            }
            else //no card in use
            {
                if (AbstractDungeon.player.hand.contains(card))
                {
                    if (ManifestField.otherManifested())
                    {
                        value *= NOT_MANIFESTED_DEBUFF;
                    }
                }
                else if (((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.contains(card))
                {
                    if (ManifestField.isManifested())
                    {
                        value *= NOT_MANIFESTED_DEBUFF;
                    }
                }
                else if (AbstractDungeon.player.drawPile.contains(card))
                {
                    if (ManifestField.otherManifested())
                    {
                        value *= NOT_MANIFESTED_DEBUFF;
                    }
                }
                else if (((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.contains(card))
                {
                    if (ManifestField.isManifested())
                    {
                        value *= NOT_MANIFESTED_DEBUFF;
                    }
                }
                else if (AbstractDungeon.player.discardPile.contains(card))
                {
                    if (ManifestField.otherManifested())
                    {
                        value *= NOT_MANIFESTED_DEBUFF;
                    }
                }
                else if (((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard.contains(card))
                {
                    if (ManifestField.isManifested())
                    {
                        value *= NOT_MANIFESTED_DEBUFF;
                    }
                }
            }
        }
        return value;
    }

    private static float applyManifestation(DamageInfo info, AbstractCreature owner, float value)
    {
        if (owner instanceof MirthAndMalice)
        {
            if (TrackCardSource.useMyEnergy && ManifestField.otherManifested())
            {
                value *= NOT_MANIFESTED_DEBUFF;
            }
            else if (TrackCardSource.useOtherEnergy && ManifestField.isManifested())
            {
                value *= NOT_MANIFESTED_DEBUFF;
            }

            if (value != info.base)
                info.isModified = true;
        }

        return value;
    }

    @SpirePatch(
            clz=AbstractCard.class,
            method="applyPowersToBlock"
    )
    public static class ApplyPowersBlock
    {
        @SpireInsertPatch(
                locator= Locator.class,
                localvars={"tmp"}
        )
        public static void Insert(AbstractCard __instance, @ByRef float[] tmp)
        {
            tmp[0] = applyManifestation(__instance, tmp[0]);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(MathUtils.class, "floor");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz=AbstractCard.class,
            method="applyPowers"
    )
    public static class ApplyPowersSingle
    {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"tmp"}
        )
        public static void Insert(AbstractCard __instance, @ByRef float[] tmp)
        {
            tmp[0] = applyManifestation(__instance, tmp[0]);
            if (__instance.baseDamage != tmp[0])
                __instance.isDamageModified = true;
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(MathUtils.class, "floor");
                ArrayList<Matcher> matchers = new ArrayList<>();
                matchers.add(finalMatcher);
                return LineFinder.findInOrder(ctMethodToPatch, matchers, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz=AbstractCard.class,
            method="applyPowers"
    )
    public static class ApplyPowersMulti
    {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"tmp", "i"}
        )
        public static void Insert(AbstractCard __instance, float[] tmp, int i)
        {
            tmp[i] = applyManifestation(__instance, tmp[i]);
            if (__instance.baseDamage != tmp[i])
                __instance.isDamageModified = true;
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(MathUtils.class, "floor");
                ArrayList<Matcher> matchers = new ArrayList<>();
                matchers.add(finalMatcher);
                matchers.add(finalMatcher);
                return LineFinder.findInOrder(ctMethodToPatch, matchers, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz=AbstractCard.class,
            method="calculateCardDamage"
    )
    public static class CalculateCardSingle
    {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"tmp"}
        )
        public static void Insert(AbstractCard __instance, AbstractMonster mo, @ByRef float[] tmp)
        {
            tmp[0] = applyManifestation(__instance, tmp[0]);
            if (__instance.baseDamage != tmp[0])
                __instance.isDamageModified = true;
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractStance.class, "atDamageGive");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz=AbstractCard.class,
            method="calculateCardDamage"
    )
    public static class CalculateCardMulti
    {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"tmp", "i"}
        )
        public static void Insert(AbstractCard __instance, AbstractMonster mo, float[] tmp, int i)
        {
            tmp[i] = applyManifestation(__instance, tmp[i]);
            if (__instance.baseDamage != tmp[i])
                __instance.isDamageModified = true;
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractStance.class, "atDamageGive");
                ArrayList<Matcher> matchers = new ArrayList<>();
                matchers.add(finalMatcher);
                return LineFinder.findInOrder(ctMethodToPatch, matchers, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz=DamageInfo.class,
            method="applyPowers"
    )
    public static class DamageInfoApplyPowers
    {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"tmp"}
        )
        public static void Insert(DamageInfo __instance, AbstractCreature owner, AbstractCreature target, @ByRef float[] tmp)
        {
            tmp[0] = applyManifestation(__instance, owner, tmp[0]);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(MathUtils.class, "floor");
                return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
