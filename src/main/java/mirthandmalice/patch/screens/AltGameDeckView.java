package mirthandmalice.patch.screens;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.screens.DrawPileViewScreen;
import javassist.CtBehavior;


@SpirePatch(
        clz = DrawPileViewScreen.class,
        method = "open"
)
public class AltGameDeckView {
    public static CardGroup altGroup = null;

    @SpireInsertPatch(
            locator = Locator.class,
            localvars = { "drawPileCopy" }
    )
    public static void changeGroup(DrawPileViewScreen __instance, @ByRef(type="com.megacrit.cardcrawl.cards.CardGroup") Object[] drawPileCopy)
    {
        if (altGroup != null)
        {
            CardGroup replacement = (CardGroup)drawPileCopy[0];
            replacement.clear();

            for (AbstractCard c : altGroup.group)
            {
                c.setAngle(0.0F, true);
                c.targetDrawScale = 0.75F;
                c.drawScale = 0.75F;
                c.lighten(true);
                replacement.addToBottom(c);
            }

            drawPileCopy[0] = replacement;
            altGroup = null;
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
