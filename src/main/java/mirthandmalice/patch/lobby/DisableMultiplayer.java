package mirthandmalice.patch.lobby;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import mirthandmalice.util.MultiplayerHelper;

@SpirePatch(
        clz = CardCrawlGame.class,
        method = "startOver"
)
@SpirePatch(
        clz = CardCrawlGame.class,
        method = "startOverButShowCredits"
)
public class DisableMultiplayer {
    @SpirePrefixPatch
    public static void reset()
    {
        MultiplayerHelper.reset();
    }
}
