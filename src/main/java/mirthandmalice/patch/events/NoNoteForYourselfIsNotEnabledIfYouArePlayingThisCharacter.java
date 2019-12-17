package mirthandmalice.patch.events;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.patch.enums.CharacterEnums;

import static mirthandmalice.MirthAndMaliceMod.logger;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "isNoteForYourselfAvailable"
)
public class NoNoteForYourselfIsNotEnabledIfYouArePlayingThisCharacter {
    @SpirePrefixPatch
    public static SpireReturn<Boolean> nope(AbstractDungeon __instance)
    {
        if (CardCrawlGame.chosenCharacter == CharacterEnums.MIRTHMALICE)
        {
            logger.info("Note For Yourself is disabled due to Multiplayer");
            return SpireReturn.Return(false);
        }
        return SpireReturn.Continue();
    }
}
