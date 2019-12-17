package mirthandmalice.patch.events;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.WeMeetAgain;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.util.MultiplayerHelper;

import static com.megacrit.cardcrawl.events.shrines.WeMeetAgain.OPTIONS;
import static mirthandmalice.MirthAndMaliceMod.makeID;

@SpirePatch(
        clz = WeMeetAgain.class,
        method = SpirePatch.CONSTRUCTOR
)
public class Ranwid {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("Ranwid"));

    @SpirePostfixPatch
    public static void checkAltButtonText(WeMeetAgain __instance) //no need to patch potion removal, since it will be removed from other player, which will be reported (and nothing will be removed from player, because it uses object comparison)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice && MultiplayerHelper.active)
        {
            if (!__instance.imageEventText.optionList.get(0).isDisabled) //a potion exists
            {
                if (((MirthAndMalice) AbstractDungeon.player).randomOtherPlayerPotion) //the potion was the other player's
                {
                    AbstractPotion potionOption = (AbstractPotion)ReflectionHacks.getPrivate(__instance, WeMeetAgain.class, "potionOption");
                    __instance.imageEventText.optionList.get(0).msg = OPTIONS[0] + FontHelper.colorString(MultiplayerHelper.partnerName + uiStrings.TEXT[0] + potionOption.name, "r") + OPTIONS[6];
                }
            }

            if (!__instance.imageEventText.optionList.get(1).isDisabled) //can afford gold option
            {
                if (MultiplayerHelper.otherPlayerGold < 50) //other player cannot afford
                {
                    __instance.imageEventText.optionList.get(0).msg = uiStrings.TEXT[1] + MultiplayerHelper.partnerName + uiStrings.TEXT[2];
                    __instance.imageEventText.optionList.get(0).isDisabled = true;
                }
            }
        }
    }
}