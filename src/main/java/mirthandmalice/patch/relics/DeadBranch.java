package mirthandmalice.patch.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.energy_division.TrackCardSource;

@SpirePatch(
        clz = com.megacrit.cardcrawl.relics.DeadBranch.class,
        method = "onExhaust"
)
public class DeadBranch {
    @SpirePrefixPatch
    public static SpireReturn toOtherPlayer(com.megacrit.cardcrawl.relics.DeadBranch __instance, AbstractCard exhausted)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice && TrackCardSource.useOtherEnergy)
        {
            if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
                __instance.flash();
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, __instance));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(AbstractDungeon.returnTrulyRandomCardInCombat().makeCopy(), false));
            }
        }
        return SpireReturn.Continue();
    }
}
