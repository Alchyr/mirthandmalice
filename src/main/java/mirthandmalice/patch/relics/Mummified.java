package mirthandmalice.patch.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.MummifiedHand;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.energy_division.TrackCardSource;

import java.util.ArrayList;

import static mirthandmalice.MirthAndMaliceMod.logger;

@SpirePatch(
        clz = MummifiedHand.class,
        method = "onUseCard"
)
public class Mummified {
    @SpirePrefixPatch
    public static SpireReturn otherPlayerPowerThenOtherPlayerHand(MummifiedHand __instance, AbstractCard c, UseCardAction a)
    {
        if (TrackCardSource.useOtherEnergy && c.type == AbstractCard.CardType.POWER && AbstractDungeon.player instanceof MirthAndMalice)
        {
            __instance.flash();
            AbstractDungeon.actionManager.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, __instance));
            ArrayList<AbstractCard> groupCopy = new ArrayList<>();

            for (AbstractCard card : ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.group)
            {
                if (card.cost > 0 && card.costForTurn > 0 && !card.freeToPlayOnce) {
                    groupCopy.add(card);
                } else {
                    logger.info("COST IS 0: " + card.name);
                }
            }
            for (CardQueueItem i : AbstractDungeon.actionManager.cardQueue)
            {
                if (i.card != null) {
                    logger.info("INVALID: " + i.card.name);
                    groupCopy.remove(i.card);
                }
            }

            AbstractCard card = null;
            if (groupCopy.isEmpty()) {
                logger.info("NO VALID CARDS");
            } else {
                card = groupCopy.get(AbstractDungeon.cardRandomRng.random(0, groupCopy.size() - 1));
            }

            if (card != null) {
                logger.info("Mummified hand: " + card.name);
                card.setCostForTurn(0);
            } else {
                logger.info("ERROR: MUMMIFIED HAND PATCH NOT WORKING");
            }
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}
