package mirthandmalice.patch.manifestation;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;

@SpirePatch(
        clz = CardCrawlGame.class,
        method = "createCharacter"
)
public class InitializeManifestation {
    @SpirePostfixPatch
    public static AbstractPlayer onCreate(AbstractPlayer __result, AbstractPlayer.PlayerClass selection)
    {
        ManifestField.mirthManifested.set(__result, true);
        return __result;
    }
}
