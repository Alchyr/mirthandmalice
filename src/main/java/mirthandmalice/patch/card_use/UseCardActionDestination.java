package mirthandmalice.patch.card_use;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

@SpirePatch(
        clz = UseCardAction.class,
        method = SpirePatch.CLASS
)
public class UseCardActionDestination {
    public static SpireField<Boolean> returnHand = new SpireField<>(()->false);
    public static SpireField<Boolean> useAlternatePile = new SpireField<>(()->false);

    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CLASS
    )
    public static class CardFields {
        public static SpireField<Boolean> swapPiles = new SpireField<>(()->false);
        public static SpireField<Boolean> returnDraw = new SpireField<>(()->false);
    }
}