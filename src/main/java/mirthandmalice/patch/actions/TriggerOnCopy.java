package mirthandmalice.patch.actions;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDrawPileEffect;

public class TriggerOnCopy {
    @SpirePatch(
            clz = ShowCardAndAddToDiscardEffect.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = { AbstractCard.class }
    )
    public static class TriggerCopyOnlyCard
    {
        @SpirePostfixPatch
        public static void thisCopiesBro(ShowCardAndAddToDiscardEffect __instance, AbstractCard c)
        {
            c.triggerWhenCopied();
        }
    }

    @SpirePatch(
            clz = ShowCardAndAddToDiscardEffect.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = { AbstractCard.class, float.class, float.class }
    )
    public static class TriggerCopyWithPosition
    {
        @SpirePostfixPatch
        public static void thisCopiesBro(ShowCardAndAddToDiscardEffect __instance, AbstractCard c, float x, float y)
        {
            c.triggerWhenCopied();
        }
    }

    @SpirePatch(
            clz = ShowCardAndAddToDrawPileEffect.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = { AbstractCard.class, boolean.class, boolean.class }
    )
    public static class TriggerCopyWithBooleans
    {
        @SpirePostfixPatch
        public static void thisCopiesBro(ShowCardAndAddToDrawPileEffect __instance, AbstractCard c, boolean whrrwh, boolean kukukukukuku)
        {
            c.triggerWhenCopied();
        }
    }

    @SpirePatch(
            clz = ShowCardAndAddToDrawPileEffect.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = { AbstractCard.class, float.class, float.class, boolean.class, boolean.class, boolean.class }
    )
    public static class TriggerCopyWithPosAndTripleBoolean
    {
        @SpirePostfixPatch
        public static void thisCopiesBro(ShowCardAndAddToDrawPileEffect __instance, AbstractCard c, float poopoo, float peepee, boolean whrrwh, boolean kukukukukuku, boolean waaaaaaaaaaaaaaaaaaaaaaaaa)
        {
            c.triggerWhenCopied();
        }
    }
}
