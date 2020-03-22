package mirthandmalice.patch.cards;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Metamorphosis;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.actions.character.MakeTempCardInOtherDrawAction;
import mirthandmalice.patch.energy_division.TrackCardSource;

@SpirePatch(
        clz = Metamorphosis.class,
        method = "use"
)
public class MetamorphosisPatch {
    @SpirePrefixPatch
    public static SpireReturn<?> altDraw(Metamorphosis __instance, AbstractPlayer p, AbstractMonster m)
    {
        if (TrackCardSource.useOtherEnergy)
        {
            for(int i = 0; i < __instance.magicNumber; ++i) {
                AbstractCard card = AbstractDungeon.returnTrulyRandomCardInCombat(AbstractCard.CardType.ATTACK).makeCopy();
                if (card.cost > 0) {
                    card.cost = 0;
                    card.costForTurn = 0;
                    card.isCostModified = true;
                }

                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInOtherDrawAction(card, 1, true, true));
            }
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}
