package mirthandmalice.patch.hooks;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;
import mirthandmalice.interfaces.OnEnemyGainBlockPower;

@SpirePatch(
        clz = AbstractCreature.class,
        method = "addBlock"
)
public class OnEnemyGainBlock {
    @SpireInsertPatch (
            locator = Locator.class,
            localvars = { "tmp" }
    )
    public static void triggerOnEnemyGainBlockPowers(AbstractCreature __instance, int base, float tmp)
    {
        if (tmp >= 1 && !__instance.isPlayer)
        {
            for (AbstractPower p : AbstractDungeon.player.powers)
            {
                if (p instanceof OnEnemyGainBlockPower)
                {
                    ((OnEnemyGainBlockPower) p).onEnemyGainBlock(__instance, tmp);
                }
            }
        }
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
