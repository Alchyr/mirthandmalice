package mirthandmalice.patch.updateOtherCardGroup;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;

@SpirePatch(
        clz = CardGroup.class,
        method = "triggerOnOtherCardPlayed"
)
public class TriggerOnCardPlayed {
    @SpirePrefixPatch
    public static void triggerOtherHandAlso(CardGroup __instance, AbstractCard c)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            for (AbstractCard card : ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.group)
            {
                if (card != c)
                {
                    card.triggerOnOtherCardPlayed(c);
                }
            }
        }
    }
}
