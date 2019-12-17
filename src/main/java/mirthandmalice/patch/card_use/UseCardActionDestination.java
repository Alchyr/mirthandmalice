package mirthandmalice.patch.card_use;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;

@SpirePatch(
        clz = UseCardAction.class,
        method = SpirePatch.CLASS
)
public class UseCardActionDestination {
    public static SpireField<Boolean> returnHand = new SpireField<>(()->false);
    public static SpireField<Boolean> useAlternatePile = new SpireField<>(()->false);
}