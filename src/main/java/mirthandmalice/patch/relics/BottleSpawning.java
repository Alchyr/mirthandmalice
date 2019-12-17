package mirthandmalice.patch.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.BottledFlame;
import com.megacrit.cardcrawl.relics.BottledLightning;
import com.megacrit.cardcrawl.relics.BottledTornado;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.util.MultiplayerHelper;

public class BottleSpawning {
    @SpirePatch(
            clz = BottledTornado.class,
            method = "canSpawn"
    )
    public static class OnlyIfBothHaveAPower
    {
        @SpirePostfixPatch
        public static boolean testOther(boolean returnVal, AbstractRelic __instance)
        {
            if (returnVal && AbstractDungeon.player instanceof MirthAndMalice && MultiplayerHelper.active)
            {
                for (AbstractCard c : ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.group)
                {
                    if (c.type == AbstractCard.CardType.POWER)
                        return true;
                }
                return false;
            }
            return returnVal;
        }
    }


    @SpirePatch(
            clz = BottledFlame.class,
            method = "canSpawn"
    )
    public static class OnlyIfBothHaveAnAttack
    {
        @SpirePostfixPatch
        public static boolean testOther(boolean returnVal, AbstractRelic __instance)
        {
            if (returnVal && AbstractDungeon.player instanceof MirthAndMalice && MultiplayerHelper.active)
            {
                for (AbstractCard c : ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.group)
                {
                    if (c.type == AbstractCard.CardType.ATTACK && c.rarity != AbstractCard.CardRarity.BASIC)
                        return true;
                }
                return false;
            }
            return returnVal;
        }
    }


    @SpirePatch(
            clz = BottledLightning.class,
            method = "canSpawn"
    )
    public static class OnlyIfBothHaveASkill
    {
        @SpirePostfixPatch
        public static boolean testOther(boolean returnVal, AbstractRelic __instance)
        {
            if (returnVal && AbstractDungeon.player instanceof MirthAndMalice && MultiplayerHelper.active)
            {
                for (AbstractCard c : ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.group)
                {
                    if (c.type == AbstractCard.CardType.SKILL && c.rarity != AbstractCard.CardRarity.BASIC)
                        return true;
                }
                return false;
            }
            return returnVal;
        }
    }
}
