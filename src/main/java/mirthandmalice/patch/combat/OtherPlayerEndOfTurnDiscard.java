package mirthandmalice.patch.combat;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.common.DiscardAtEndOfTurnAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.EquilibriumPower;
import com.megacrit.cardcrawl.relics.RunicPyramid;
import mirthandmalice.actions.character.OtherPlayerDiscardAction;
import mirthandmalice.actions.character.RestoreOtherRetainedCardsAction;
import mirthandmalice.character.MirthAndMalice;

import java.util.ArrayList;
import java.util.Collections;

@SpirePatch(
        clz = DiscardAtEndOfTurnAction.class,
        method = "update"
)
public class OtherPlayerEndOfTurnDiscard {
    @SpirePrefixPatch
    public static void onEndTurnDiscard(DiscardAtEndOfTurnAction __instance)
    {
        if (!__instance.isDone && AbstractDungeon.player instanceof MirthAndMalice)
        {
            MirthAndMalice player = (MirthAndMalice)AbstractDungeon.player;

            for (AbstractCard c : player.otherPlayerHand.group)
            {
                if (c.retain) {
                    player.fakeLimbo.addToTop(c);
                }
            }
            player.otherPlayerHand.group.removeIf((c)->c.retain);

            AbstractDungeon.actionManager.addToTop(new RestoreOtherRetainedCardsAction(player.fakeLimbo, player.otherPlayerHand));

            if (!AbstractDungeon.player.hasRelic(RunicPyramid.ID) && !AbstractDungeon.player.hasPower(EquilibriumPower.POWER_ID)) {
                int tempSize = player.otherPlayerHand.size();

                AbstractDungeon.actionManager.addToTop(new OtherPlayerDiscardAction(player, null, tempSize, true));
            }

            ArrayList<AbstractCard> cards = new ArrayList<>(player.otherPlayerHand.group);
            Collections.shuffle(cards);
            for (AbstractCard c : cards)
            {
                c.triggerOnEndOfPlayerTurn();
            }
        }
    }
}
