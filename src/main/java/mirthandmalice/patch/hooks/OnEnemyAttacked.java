package mirthandmalice.patch.hooks;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.patch.combat.BurstActive;

@SpirePatch(
        clz = AbstractMonster.class,
        method = "damage"
)
public class OnEnemyAttacked { //This is more like "onDamaged"
    @SpirePrefixPatch
    public static void triggerWhenAttacked(AbstractMonster __instance, DamageInfo info)
    {
        if (info.output > 0) // && info.type == DamageInfo.DamageType.NORMAL)
        {
            BurstActive.active.set(__instance, true);
        }
    }
}
