package mirthandmalice.patch.ui;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.enums.CharacterEnums;

@SpirePatch(
        clz = AbstractCard.class,
        method = "canUse"
)
public class OtherHandNoGlow {
    @SpirePrefixPatch
    public static SpireReturn<Boolean> noActuallyYouCantUseThese(AbstractCard __instance, AbstractPlayer p, AbstractMonster m)
    {
        if (p.chosenClass == CharacterEnums.MIRTHMALICE && p instanceof MirthAndMalice)
        {
            if (((MirthAndMalice) p).otherPlayerHand.contains(__instance))
            {
                return SpireReturn.Return(false);
            }
        }
        return SpireReturn.Continue();
    }
}
