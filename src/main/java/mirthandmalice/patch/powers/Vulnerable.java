package mirthandmalice.patch.powers;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import mirthandmalice.powers.TraumaPower;

public class Vulnerable {
    @SpirePatch(
            clz = VulnerablePower.class,
            method = "atDamageReceive"
    )
    public static class StackBonus
    {
        @SpirePostfixPatch
        public static float weightyStacking(float out, VulnerablePower __instance, float base, DamageInfo.DamageType t)
        {
            if (out != base && __instance.owner.hasPower(TraumaPower.POWER_ID))
            {
                float mult = out / base; //ex, 1.5, 1.75, idk
                mult -= 1; //now it's .5, .75, something like that
                mult *= __instance.amount; //now it's a much bigger number. 3 stacks at .5 per: this is 1.5.
                mult += 1;
                return base * mult;
            }
            return out;
        }
    }

    @SpirePatch(
            clz = VulnerablePower.class,
            method = "updateDescription"
    )
    public static class MatchingDescription
    {
        @SpirePrefixPatch
        public static SpireReturn altDescription(VulnerablePower __instance)
        {
            if (__instance.owner.hasPower(TraumaPower.POWER_ID))
            {
                float mult = __instance.atDamageReceive(1.0f, DamageInfo.DamageType.NORMAL); //will use altered calculation
                mult -= 1;
                mult *= 100;
                int out = MathUtils.floor(mult);

                if (__instance.amount == 1)
                    __instance.description = VulnerablePower.DESCRIPTIONS[0] + out + VulnerablePower.DESCRIPTIONS[1] + __instance.amount + VulnerablePower.DESCRIPTIONS[2];
                else
                    __instance.description = VulnerablePower.DESCRIPTIONS[0] + out + VulnerablePower.DESCRIPTIONS[1] + __instance.amount + VulnerablePower.DESCRIPTIONS[3];
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
