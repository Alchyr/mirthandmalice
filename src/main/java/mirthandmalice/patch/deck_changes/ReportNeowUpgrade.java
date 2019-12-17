package mirthandmalice.patch.deck_changes;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import javassist.CtBehavior;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.MultiplayerHelper;

@SpirePatch(
        clz = NeowReward.class,
        method = "update"
)
public class ReportNeowUpgrade {
    @SpireInsertPatch(
            locator = Locator.class,
            localvars = { "c" }
    )
    public static void NeowICanUnderstandWhyYouDontCheckIfCardsAreBottledButWhyMustYouMakeItSoHard(NeowReward __instance, AbstractCard c)
    {
        if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && AbstractDungeon.player instanceof MirthAndMalice && MultiplayerHelper.active && AbstractDungeon.player.masterDeck.contains(c))
        {
            MultiplayerHelper.sendP2PString("other_upgrade_card" + AbstractDungeon.player.masterDeck.group.indexOf(c));
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.NewExprMatcher(ShowCardBrieflyEffect.class);
            return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
