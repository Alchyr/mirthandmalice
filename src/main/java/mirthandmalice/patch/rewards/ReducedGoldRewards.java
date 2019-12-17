package mirthandmalice.patch.rewards;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import mirthandmalice.patch.enums.CharacterEnums;

@SpirePatch(
        clz = RewardItem.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = { int.class }
)
public class ReducedGoldRewards {
    @SpirePrefixPatch
    public static void reduceNormalGoldRewards(RewardItem __instance, @ByRef int[] goldAmt)
    {
        if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE)
        {
            goldAmt[0] = MathUtils.ceil(goldAmt[0] * 0.7f); //Not fully halved; too hard to get anything at all if it is.
        }
    }
    //Theft reward is fine, since both players lose stolen gold, and get back stolen amount.
}
