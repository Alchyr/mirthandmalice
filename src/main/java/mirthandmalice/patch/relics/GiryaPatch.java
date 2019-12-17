package mirthandmalice.patch.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.campfire.LiftOption;
import com.megacrit.cardcrawl.vfx.campfire.CampfireLiftEffect;
import javassist.CtBehavior;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.MultiplayerHelper;

import java.util.ArrayList;

@SpirePatch(
        clz = LiftOption.class,
        method = "useOption"
)
public class GiryaPatch {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void reportLifting(LiftOption __instance)
    {
        if (AbstractDungeon.player != null && AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
        {
            MultiplayerHelper.sendP2PString("LIFT");
        }
    }

    public static void doLift()
    {
        AbstractDungeon.effectList.add(new CampfireLiftEffect());
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
