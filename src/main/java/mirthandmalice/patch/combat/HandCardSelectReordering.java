package mirthandmalice.patch.combat;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import javassist.CtBehavior;
import mirthandmalice.util.MultiplayerHelper;

import java.util.ArrayList;

//This class is to ensure when the order of cards in hand is shuffled by a HandCardSelectScreen, it (hopefully) stays consistent.
public class HandCardSelectReordering {
    private static ArrayList<AbstractCard> originalHand = new ArrayList<>();

    public static void reset()
    {
        originalHand.clear();
    }
    public static void saveHandPreOpenScreen() //This should be called before cards are removed from hand and before hand select screen is opened.
    {
        originalHand.clear();
        originalHand.addAll(AbstractDungeon.player.hand.group);
    }

    @SpirePatch(
            clz = HandCardSelectScreen.class,
            method = "open",
            paramtypez = { String.class, int.class, boolean.class, boolean.class, boolean.class, boolean.class, boolean.class }
    )
    @SpirePatch(
            clz = HandCardSelectScreen.class,
            method = "open",
            paramtypez = { String.class, int.class, boolean.class, boolean.class }
    )
    public static class ReportOnOpen
    {
        @SpirePrefixPatch
        public static void reportOpening(HandCardSelectScreen __instance)
        {
            if (MultiplayerHelper.active && !originalHand.isEmpty())
            {
                MultiplayerHelper.sendP2PString("hand_select");
            }
        }
    }

    @SpirePatch(
            clz = HandCardSelectScreen.class,
            method = "update"
    )
    public static class UpdateOnClose
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void onClose(HandCardSelectScreen __instance)
        {
            if (MultiplayerHelper.active && !originalHand.isEmpty())
            {
                //Compare current hand to "originalHand" which hopefully has been reported
                StringBuilder indexes = new StringBuilder();
                for (AbstractCard c : AbstractDungeon.player.hand.group)
                {
                    if (originalHand.contains(c))
                    {
                        indexes.append(originalHand.indexOf(c)).append(" ");
                    }
                }
                for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group)
                {
                    if (originalHand.contains(c))
                    {
                        indexes.append(originalHand.indexOf(c)).append(" ");
                    }
                }
                for (AbstractCard c : originalHand)
                {
                    if (!AbstractDungeon.player.hand.group.contains(c) && !AbstractDungeon.handCardSelectScreen.selectedCards.group.contains(c)) //cards were in original hand but have vanished - they were removed and will be re-added afterwards. so, on to end they go.
                    {
                        indexes.append(originalHand.indexOf(c)).append(" ");
                    }
                }

                //send index array, space separated numbers
                MultiplayerHelper.sendP2PString("hand_select_indexes" + indexes.toString().trim());

                reset();
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "closeCurrentScreen");
                return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
