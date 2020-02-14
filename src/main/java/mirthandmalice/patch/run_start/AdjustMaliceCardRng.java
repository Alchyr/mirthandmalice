package mirthandmalice.patch.run_start;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;

import static mirthandmalice.MirthAndMaliceMod.logger;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "initializeCardPools"
)
public class AdjustMaliceCardRng {
    @SpirePostfixPatch
    public static void adjust(AbstractDungeon __instance)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice && !((MirthAndMalice) AbstractDungeon.player).isMirth)
        {
            AbstractDungeon.cardRng.setCounter(AbstractDungeon.cardRng.random(500));
            logger.info("Set Malice's card rng counter to " + AbstractDungeon.cardRng.counter);
        }
    }
}
