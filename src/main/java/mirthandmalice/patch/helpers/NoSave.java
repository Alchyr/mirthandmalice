package mirthandmalice.patch.helpers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.SaveHelper;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import mirthandmalice.patch.enums.CharacterEnums;

public class NoSave {
    @SpirePatch(
            clz = SaveHelper.class,
            method = "shouldSave"
    )
    public static class DoNot
    {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> noYouShouldNotSave()
        {
            if (CardCrawlGame.chosenCharacter == CharacterEnums.MIRTHMALICE)
            {
                return SpireReturn.Return(false);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = SaveAndContinue.class,
            method = "save"
    )
    public static class ISaidDoNotDidYouNotHearMeStupidGameReeeeeeeeeeeeeeeeeeeeeeeeee
    {
        @SpirePrefixPatch
        public static SpireReturn ree(SaveFile ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff)
        {
            if (CardCrawlGame.chosenCharacter == CharacterEnums.MIRTHMALICE)
            {
                CardCrawlGame.loadingSave = false;
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
