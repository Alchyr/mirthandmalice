package mirthandmalice.patch.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.relics.DullShard;

public class NoShard {
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "initializeRelicList"
    )
    public static class Delet
    {
        @SpirePrefixPatch
        public static void NoShard(AbstractDungeon __instance)
        {
            if (CardCrawlGame.chosenCharacter == CharacterEnums.MIRTHMALICE)
                AbstractDungeon.relicsToRemoveOnStart.add(PrismaticShard.ID);
        }
    }

    @SpirePatch(
            clz = PrismaticShard.class,
            method = "makeCopy"
    )
    public static class YouReallyCantGetItOk
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractRelic> OtherShard(PrismaticShard __instance)
        {
            if (CardCrawlGame.chosenCharacter == CharacterEnums.MIRTHMALICE)
                return SpireReturn.Return(new DullShard());
            return SpireReturn.Continue();
        }
    }
}
