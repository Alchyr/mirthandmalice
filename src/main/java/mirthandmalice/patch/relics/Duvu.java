package mirthandmalice.patch.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.DuVuDoll;
import javassist.CtBehavior;
import mirthandmalice.character.MirthAndMalice;

@SpirePatch(
        clz = DuVuDoll.class,
        method = "onMasterDeckChange"
)
@SpirePatch(
        clz = DuVuDoll.class,
        method = "onEquip"
)
public class Duvu {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void CountBothDecks(DuVuDoll __instance)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            for (AbstractCard c : ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.group)
            {
                if (c.type == AbstractCard.CardType.CURSE)
                    __instance.counter++;
            }
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "masterDeck");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
