package mirthandmalice.patch.events;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.Beggar;
import com.megacrit.cardcrawl.events.exordium.Cleric;
import com.megacrit.cardcrawl.random.Random;
import javassist.CtBehavior;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.MultiplayerHelper;

import java.util.ArrayList;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "getEvent"
)
public class ConditionalEventRemoval {
    @SpireInsertPatch(
            locator = Locator.class,
            localvars = { "tmp" } //tmp is ArrayList of String for events
    )
    public static void checkEventList(Random rng, ArrayList<String> tmp)
    {
        if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
        {
            ArrayList<String> toRemove = new ArrayList<>();
            for (String e : tmp)
            {
                switch (e)
                {
                    case Cleric.ID: //player has >=35 gold
                        int purifyCost = AbstractDungeon.ascensionLevel >= 15 ? 75 : 50;
                        if (MultiplayerHelper.otherPlayerGold < 35 ||
                                (AbstractDungeon.player.gold >= purifyCost && MultiplayerHelper.otherPlayerGold < purifyCost) ||
                                (AbstractDungeon.player.gold < purifyCost && MultiplayerHelper.otherPlayerGold >= purifyCost))
                            toRemove.add(e); //can't run into cleric if one player can choose an option that the other player cannot
                        break;
                    case Beggar.ID: //player has >=75 gold
                        if (MultiplayerHelper.otherPlayerGold < 75) //other player doesn't.
                            toRemove.add(e);
                        break;
                }
            }

            tmp.removeAll(toRemove);
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "isEmpty");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
