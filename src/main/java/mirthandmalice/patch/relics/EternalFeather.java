package mirthandmalice.patch.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CtBehavior;
import mirthandmalice.character.MirthAndMalice;

@SpirePatch(
        clz = com.megacrit.cardcrawl.relics.EternalFeather.class,
        method = "onEnterRoom"
)
public class EternalFeather {
    @SpireInsertPatch(
            locator = Locator.class,
            localvars = { "amountToGain" }
    )
    public static void countAlt(com.megacrit.cardcrawl.relics.EternalFeather __instance, AbstractRoom r, @ByRef int[] amountToGain)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            amountToGain[0] += ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.size() / 5 * 3;
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "heal");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
