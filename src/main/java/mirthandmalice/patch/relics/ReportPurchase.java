package mirthandmalice.patch.relics;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StoreRelic;
import javassist.CtBehavior;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.HandleMatchmaking;
import mirthandmalice.util.MultiplayerHelper;

import java.util.ArrayList;

public class ReportPurchase {
    private static StoreRelic forcePurchase = null;

    @SpirePatch(
            clz = StoreRelic.class,
            method = "purchaseRelic"
    )
    public static class ReportOnPurchase
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn onPurchase(StoreRelic __instance)
        {
            if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
            {
                if (__instance != forcePurchase)
                {
                    if (HandleMatchmaking.isHost) //host can purchase immediately
                    {
                        MultiplayerHelper.sendP2PMessage(AbstractDungeon.player.name + " bought " + __instance.relic.name + ".");
                        MultiplayerHelper.sendP2PString("purchase_relic" + __instance.relic.relicId);
                    }
                    else //non-host must receive confirmation message to puchase
                    {
                        //MultiplayerHelper.sendP2PMessage(AbstractDungeon.player.name + " bought " + __instance.relic.name + ".");
                        MultiplayerHelper.sendP2PString("try_purchase_relic" + __instance.relic.relicId);
                        return SpireReturn.Return(null);
                    }
                }
                else //if (__instance == forcePurchase)
                {
                    forcePurchase = null;
                }
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "loseGold");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static AbstractRelic forcePurchase(String id)
    {
        if (AbstractDungeon.shopScreen != null)
        {
            ArrayList<StoreRelic> shopRelics = (ArrayList<StoreRelic>) ReflectionHacks.getPrivate(AbstractDungeon.shopScreen, ShopScreen.class, "relics");
            for (StoreRelic r : shopRelics)
            {
                if (r.relic != null && r.relic.relicId.equals(id))
                {
                    r.price = 0;
                    forcePurchase = r; //prevent purchaseRelic from reporting purchase to other player
                    r.purchaseRelic();
                    return r.relic;
                }
            }
        }
        return null;
    }
    @SuppressWarnings("unchecked")
    public static void normalPurchase(String id)
    {
        if (AbstractDungeon.shopScreen != null)
        {
            ArrayList<StoreRelic> shopRelics = (ArrayList<StoreRelic>) ReflectionHacks.getPrivate(AbstractDungeon.shopScreen, ShopScreen.class, "relics");
            for (StoreRelic r : shopRelics)
            {
                if (r.relic != null && r.relic.relicId.equals(id))
                {
                    //r.price = 0;
                    forcePurchase = r; //prevent purchaseRelic from reporting purchase to other player
                    r.purchaseRelic();
                    //return r.relic;
                }
            }
        }
        //return null;
    }
}
