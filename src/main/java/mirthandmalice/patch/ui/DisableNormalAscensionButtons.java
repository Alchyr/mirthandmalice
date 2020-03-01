package mirthandmalice.patch.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import mirthandmalice.patch.enums.CharacterEnums;

public class DisableNormalAscensionButtons {
    @SpirePatch(
            clz = CharacterSelectScreen.class,
            method = "updateAscensionToggle"
    )
    public static class NoUpdate {
        @SpirePrefixPatch
        public static SpireReturn<?> no(CharacterSelectScreen __instance)
        {
            if (CardCrawlGame.chosenCharacter != null && CardCrawlGame.chosenCharacter == CharacterEnums.MIRTHMALICE)
            {

                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
    @SpirePatch(
            clz = CharacterSelectScreen.class,
            method = "renderAscensionMode"
    )
    public static class NoRender {
        @SpirePrefixPatch
        public static SpireReturn<?> no(CharacterSelectScreen __instance, SpriteBatch sb)
        {
            if (CardCrawlGame.chosenCharacter != null && CardCrawlGame.chosenCharacter == CharacterEnums.MIRTHMALICE)
            {

                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
