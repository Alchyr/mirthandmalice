package mirthandmalice.patch.card_use;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;
import mirthandmalice.character.MirthAndMalice;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "useCard"
)
public class OnUseCard {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void removeFromOtherHand(AbstractPlayer __instance, AbstractCard c, AbstractMonster t, int energyOnUse)
    {
        if (__instance instanceof MirthAndMalice)
        {
            if (DiscardToCorrectPile.removeFromOtherHand) //This card was queued by other player, from other player hand.
            {
                DiscardToCorrectPile.removeFromOtherHand = false;

                if (!UseCardActionDestination.CardFields.swapPiles.get(c))
                {
                    int index = AbstractDungeon.actionManager.actions.size();
                    UseCardAction action = null;
                    while (action == null && index > 0)
                    {
                        --index;
                        if (AbstractDungeon.actionManager.actions.get(index) instanceof UseCardAction)
                        {
                            action = (UseCardAction)AbstractDungeon.actionManager.actions.get(index);
                            UseCardActionDestination.useAlternatePile.set(action, true);
                        }
                    }
                }
            }
            else if (UseCardActionDestination.CardFields.swapPiles.get(c))
            {
                int index = AbstractDungeon.actionManager.actions.size();
                UseCardAction action = null;
                while (action == null && index > 0)
                {
                    --index;
                    if (AbstractDungeon.actionManager.actions.get(index) instanceof UseCardAction)
                    {
                        action = (UseCardAction)AbstractDungeon.actionManager.actions.get(index);
                        UseCardActionDestination.useAlternatePile.set(action, true);
                    }
                }
            }
            ((MirthAndMalice) __instance).otherPlayerHand.removeCard(c);
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "removeCard");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
