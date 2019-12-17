package mirthandmalice.patch.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.HexPower;
import mirthandmalice.actions.character.MakeTempCardInOtherDrawAction;
import mirthandmalice.patch.energy_division.TrackCardSource;

@SpirePatch(
        clz = HexPower.class,
        method = "onUseCard"
)
public class Hex {
    @SpirePrefixPatch
    public static SpireReturn IfOtherPlayerThenOtherPile(HexPower __instance, AbstractCard c, UseCardAction a)
    {
        if (TrackCardSource.useOtherEnergy && c.type != AbstractCard.CardType.ATTACK)
        {
            __instance.flash();
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInOtherDrawAction(new Dazed(), __instance.amount, true, true));

            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}
