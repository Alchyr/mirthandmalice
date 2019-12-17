package mirthandmalice.patch.deck_changes;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.MultiplayerHelper;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "bottledCardUpgradeCheck"
)
public class ReportUpgradeCard {
    @SpirePostfixPatch
    public static void EveryoneWhoUpgradesCardsInTheDeckBetterHeckingCallThis(AbstractPlayer __instance, AbstractCard upgraded)
    {
        if (__instance.chosenClass == CharacterEnums.MIRTHMALICE && __instance instanceof MirthAndMalice && MultiplayerHelper.active && __instance.masterDeck.contains(upgraded))
        {
            MultiplayerHelper.sendP2PString("other_upgrade_card" + __instance.masterDeck.group.indexOf(upgraded));
        }
    }

    public static void receiveOtherUpgradeCard(String arg)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            int index = Integer.valueOf(arg);
            AbstractCard toUpgrade = ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.group.get(index);
            toUpgrade.upgrade();
        }
    }
}
