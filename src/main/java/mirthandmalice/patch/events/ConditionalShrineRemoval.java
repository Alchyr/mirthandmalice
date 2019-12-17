package mirthandmalice.patch.events;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.TheJoust;
import com.megacrit.cardcrawl.events.shrines.*;
import com.megacrit.cardcrawl.random.Random;
import javassist.CtBehavior;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.MultiplayerHelper;

import java.util.ArrayList;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "getShrine"
)
public class ConditionalShrineRemoval {
    @SpireInsertPatch(
            locator = Locator.class,
            localvars = { "tmp" } //tmp is ArrayList of String for shrines
    )
    public static void checkShrineList(Random rng, ArrayList<String> tmp)
    {
        if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
        {
            ArrayList<String> toRemove = new ArrayList<>();
            for (String e : tmp)
            {
                switch (e)
                {
                    case Designer.ID: //player has >=75 gold; everything other than full service always is <=75
                        int fullService = AbstractDungeon.ascensionLevel >= 15 ? 110 : 90;
                        if (MultiplayerHelper.otherPlayerGold < 75 ||
                                (AbstractDungeon.player.gold >= fullService && MultiplayerHelper.otherPlayerGold < fullService) ||
                                (AbstractDungeon.player.gold < fullService && MultiplayerHelper.otherPlayerGold >= fullService))
                        {
                            toRemove.add(e);
                        }
                        break;
                    case TheJoust.ID:
                    case WomanInBlue.ID:
                        //Both these events require player to have 50 gold and do not have an option that costs more than 50.
                        if (MultiplayerHelper.otherPlayerGold < 50)
                            toRemove.add(e);
                        break;
                }
            }

            tmp.removeAll(toRemove);



            if (tmp.isEmpty()) //this *should* be almost impossible, but just in case. The game has no backup for tmp being empty, which would cause a crash.
            {
                tmp.add(GoldShrine.ID);
                tmp.add(Duplicator.ID);
                tmp.add(UpgradeShrine.ID); //rng is seeded, so this will be consistent. Better than just adding one, I think.
            }
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "get");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
