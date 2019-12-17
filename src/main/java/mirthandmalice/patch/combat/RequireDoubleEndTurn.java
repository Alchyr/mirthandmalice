package mirthandmalice.patch.combat;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.ui.buttons.EndTurnButton;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.util.HandleMatchmaking;
import mirthandmalice.util.MultiplayerHelper;
import mirthandmalice.util.OtherPlayerCardQueueItem;

import static mirthandmalice.util.HandleMatchmaking.isHost;
import static mirthandmalice.MirthAndMaliceMod.logger;
import static mirthandmalice.MirthAndMaliceMod.makeID;

public class RequireDoubleEndTurn {
    public static final UIStrings endTurnStrings = CardCrawlGame.languagePack.getUIString(makeID("EndTurn"));

    public static boolean ended = false;
    public static boolean otherPlayerEnded = false;

    private static boolean allowDisable = false;

    public static void reset()
    {
        ended = false;
        otherPlayerEnded = false;
        allowDisable = false;
    }

    @SpirePatch(
            clz = EndTurnButton.class,
            method = "disable",
            paramtypez = { boolean.class }
    )
    public static class onDisable
    {
        @SpirePrefixPatch
        public static SpireReturn preventInstantEnd(EndTurnButton __instance, boolean isEnemyTurn)
        {
            if (allowDisable)
            {
                reset();
                return SpireReturn.Continue();
            }

            if (AbstractDungeon.player instanceof MirthAndMalice && MultiplayerHelper.active)
            {
                if (!ended)
                {
                    endTurn();
                }
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    private static void endTurn()
    {
        AbstractDungeon.player.releaseCard();
        ended = true;
        AbstractDungeon.overlayMenu.endTurnButton.disable();
        MultiplayerHelper.sendP2PMessage(CardCrawlGame.playerName + endTurnStrings.TEXT[1]);
        logger.info("Ended turn.");

        AbstractDungeon.player.hand.glowCheck();

        if (isHost && otherPlayerEnded)
        {
            allowDisable = true;
            MultiplayerHelper.sendP2PString("full_end_turn");
            AbstractDungeon.overlayMenu.endTurnButton.disable(true);
        }
        else
        {
            MultiplayerHelper.sendP2PString("end_turn");
        }
    }

    public static void otherPlayerEndTurn()
    {
        otherPlayerEnded = true;
        if (ended && isHost)
        {
            allowDisable = true;
            MultiplayerHelper.sendP2PString("full_end_turn");
            AbstractDungeon.overlayMenu.endTurnButton.disable(true);
        }
    }

    public static void fullEndTurn()
    {
        allowDisable = true;
        AbstractDungeon.overlayMenu.endTurnButton.disable(true);
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "canUse"
    )
    public static class NoPlay
    {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> noPlayAfterEnd(AbstractCard __instance, AbstractPlayer p, AbstractMonster m)
        {
            if (!AbstractDungeon.actionManager.cardQueue.isEmpty() && __instance.equals(AbstractDungeon.actionManager.cardQueue.get(0).card) && AbstractDungeon.actionManager.cardQueue.get(0) instanceof OtherPlayerCardQueueItem)
            {
                return SpireReturn.Return(true);
            }
            if (ended && HandleMatchmaking.activeMultiplayer && p instanceof MirthAndMalice)
            {
                __instance.cantUseMessage = endTurnStrings.TEXT[0];
                return SpireReturn.Return(false);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = AbstractPotion.class,
            method = "canUse"
    )
    public static class NoPotion
    {
        @SpirePostfixPatch
        public static boolean NoPotionAfterEnd(boolean result, AbstractPotion __instance)
        {
            if (ended)
            {
                return false;
            }
            return result;
        }
    }
}
