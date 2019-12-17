package mirthandmalice.patch.actions;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.unique.DiscoveryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import javassist.CtBehavior;
import mirthandmalice.actions.character.WaitForSignalAction;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.MessageHelper;
import mirthandmalice.util.MultiplayerHelper;

import static mirthandmalice.util.MultiplayerHelper.partnerName;
import static mirthandmalice.MirthAndMaliceMod.makeID;

@SpirePatch(
        clz = DiscoveryAction.class,
        method = "update"
)
public class Discovery {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("Discovery"));

    @SpirePrefixPatch
    public static SpireReturn waitForPlayer(DiscoveryAction __instance)
    {
        if (MultiplayerHelper.active && AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE)
        {
            if (TrackCardSource.useOtherEnergy) //played by other player.
            {
                AbstractDungeon.actionManager.addToTop(new WaitForSignalAction(uiStrings.TEXT[0] + partnerName + uiStrings.TEXT[1]));
                __instance.isDone = true;
                return SpireReturn.Return(null);
            }
            else if (!TrackCardSource.useMyEnergy) //This is neutral source. Each player will have to choose, and then wait for other player to choose.
            {
                AbstractDungeon.actionManager.addToTop(new WaitForSignalAction(uiStrings.TEXT[0] + partnerName + uiStrings.TEXT[1]));
            }
        }
        return SpireReturn.Continue();
    }

    @SpireInsertPatch(
            locator = Locator.class,
            localvars = {
                    "disCard"
            }
    )
    public static void reportChosenCard(DiscoveryAction __instance, AbstractCard disCard)
    {
        if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
        {
            MultiplayerHelper.sendP2PString("discover_card" + MessageHelper.cardInfoString(disCard));
        }
    }

    @SpirePostfixPatch
    public static void endAction(DiscoveryAction __instance)
    {
        if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active && __instance.isDone)
        {
            int rngStatus = AbstractDungeon.cardRandomRng.counter;
            MultiplayerHelper.sendP2PString("signalcrrng" + rngStatus);
        }
    }


    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardRewardScreen.class, "discoveryCard");
            return new int[] { LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[2] };
        }
    }
}
