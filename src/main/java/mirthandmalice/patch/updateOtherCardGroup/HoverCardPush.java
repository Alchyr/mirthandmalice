package mirthandmalice.patch.updateOtherCardGroup;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.enums.CharacterEnums;

@SpirePatch(
        clz = CardGroup.class,
        method = "hoverCardPush"
)
public class HoverCardPush {
    @SpirePrefixPatch
    public static SpireReturn pushAlt(CardGroup __instance, AbstractCard c)
    {
        if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (!__instance.contains(c) && __instance.equals(AbstractDungeon.player.hand))
            {
                ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.hoverCardPush(c);
                return SpireReturn.Return(null);
            }
        }
        return SpireReturn.Continue();
    }
}
