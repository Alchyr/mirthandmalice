package mirthandmalice.patch.rewards;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.MultiplayerHelper;

import static mirthandmalice.util.MultiplayerHelper.partnerName;
import static mirthandmalice.MirthAndMaliceMod.makeID;

public class ObtainRewards {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("ClaimReward"));

    @SpirePatch(
            clz = RewardItem.class,
            method = "claimReward"
    )
    public static class OnClaimReward
    {
        @SpirePostfixPatch
        public static boolean reportClaim(boolean result, RewardItem __instance)
        {
            if (result && AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
            {
                char type = '_';
                switch (__instance.type)
                {
                    case GOLD:
                    case STOLEN_GOLD: //gold is individual
                    case POTION: //potions are handled separately and are also individual
                        break;
                    case RELIC:
                        type = 'r';
                    case SAPPHIRE_KEY:
                        if (type != 'r')
                            type = 's';
                        if (__instance.ignoreReward)
                            break;
                    case EMERALD_KEY:
                        if (type != 'r' && type != 's')
                            type = 'e';
                        MultiplayerHelper.sendP2PString("claim_reward" + type + (type == 'r' ? __instance.relic.relicId : ""));
                        break;
                }
            }
            return result;
        }
    }

    public static void claimReward(String args)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (AbstractDungeon.getCurrRoom() instanceof TreasureRoom && !((TreasureRoom) AbstractDungeon.getCurrRoom()).chest.isOpen) //The chest might not be open.
            {
                ((TreasureRoom) AbstractDungeon.getCurrRoom()).chest.open(false);
            }

            char type = args.charAt(0);
            switch (type)
            {
                case 'r':
                    String id = args.substring(1);
                    for (RewardItem i : AbstractDungeon.combatRewardScreen.rewards)
                    {
                        if (i.type == RewardItem.RewardType.RELIC && i.relic.relicId.equals(id))
                        {
                            i.isDone = true;
                            MultiplayerHelper.sendP2PMessage(partnerName + uiStrings.TEXT[0] + i.relic.name + uiStrings.TEXT[3]);
                        }
                    }
                    break;
                case 's':
                    for (RewardItem i : AbstractDungeon.combatRewardScreen.rewards)
                    {
                        if (i.type == RewardItem.RewardType.SAPPHIRE_KEY)
                        {
                            i.isDone = true;
                            MultiplayerHelper.sendP2PMessage(partnerName + uiStrings.TEXT[1]);
                        }
                    }
                case 'e':
                    for (RewardItem i : AbstractDungeon.combatRewardScreen.rewards)
                    {
                        if (i.type == RewardItem.RewardType.EMERALD_KEY)
                        {
                            i.isDone = true;
                            MultiplayerHelper.sendP2PMessage(((MirthAndMalice) AbstractDungeon.player).getOtherPlayerName() + uiStrings.TEXT[2]);
                        }
                    }
                    break;
            }
        }
    }
}
