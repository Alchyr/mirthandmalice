package mirthandmalice.patch.combat;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CtBehavior;
import mirthandmalice.powers.PatiencePower;

public class PreventBlockLossPower {
    @SpirePatch(
            clz = GameActionManager.class,
            method = "getNextAction"
    )
    public static class ActionManagerNoRemoveBlockPatch
    {
        @SpireInsertPatch(
                locator = BeforeLoseBlockLocator.class
        )
        public static void PreventLoseBlock(GameActionManager __instance)
        {
            for (AbstractPower p : AbstractDungeon.player.powers)
            {
                if (p instanceof PatiencePower)
                {
                    NoLoseBlockSpireField.loseBlock.set(AbstractDungeon.player, false);
                }
            }
        }

        @SpireInsertPatch(
                locator = AfterLoseBlockLocator.class
        )
        public static void EnableLoseBlock(GameActionManager __instance)
        {
            NoLoseBlockSpireField.loseBlock.set(AbstractDungeon.player, true);
        }


        private static class BeforeLoseBlockLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasPower");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
        private static class AfterLoseBlockLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractRoom.class, "isBattleOver");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractCreature.class,
            method = SpirePatch.CLASS
    )
    public static class NoLoseBlockSpireField
    {
        public static SpireField<Boolean> loseBlock = new SpireField<>(()->true);
    }

    @SpirePatch(
            clz = AbstractCreature.class,
            method = "loseBlock",
            paramtypez = { int.class }
    )
    public static class CheckWhenLoseBlock
    {
        @SpirePrefixPatch
        public static SpireReturn Prefix(AbstractCreature __instance)
        {
            if (!NoLoseBlockSpireField.loseBlock.get(__instance))
            {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
