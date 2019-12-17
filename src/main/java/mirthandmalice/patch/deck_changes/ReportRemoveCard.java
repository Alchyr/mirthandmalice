package mirthandmalice.patch.deck_changes;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.util.MultiplayerHelper;

import java.util.ArrayList;

public class ReportRemoveCard {
    @SpirePatch(
            clz = CardGroup.class,
            method = "removeCard",
            paramtypez = { AbstractCard.class }
    )
    public static class OnRemove
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void onRemoveFromMasterDeck(CardGroup __instance, AbstractCard removed)
        {
            if (AbstractDungeon.player instanceof MirthAndMalice && MultiplayerHelper.active && __instance.equals(AbstractDungeon.player.masterDeck))
            {
                MultiplayerHelper.sendP2PString("other_remove_card" + __instance.group.indexOf(removed));
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "remove");
                return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    public static void receiveOtherRemoveCard(String arg)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            int index = Integer.valueOf(arg);
            AbstractCard toRemove = ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.group.get(index);
            ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.removeCard(toRemove);
        }
    }
}
