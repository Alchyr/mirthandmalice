package mirthandmalice.patch.combat;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.util.CombatDeckHelper;

@SpirePatch(
        clz = CardGroup.class,
        method = "initializeDeck"
)
public class InitializeCombatDeck {
    @SpirePrefixPatch
    public static SpireReturn preOtherPlayer(CardGroup __instance, CardGroup masterDeck) //Keine's deck is always shuffled first, to ensure consistency in rng.
    {
        if (AbstractDungeon.player instanceof MirthAndMalice && ((MirthAndMalice) AbstractDungeon.player).isMirth && !__instance.equals(((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw))
        {
            ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.initializeDeck(((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck);
        }
        return SpireReturn.Continue();
    }

    @SpirePostfixPatch
    public static SpireReturn postOtherPlayer(CardGroup __instance, CardGroup masterDeck)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice && !((MirthAndMalice) AbstractDungeon.player).isMirth && !__instance.equals(((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw))
        {
            ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.initializeDeck(((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck);
        }

        if (AbstractDungeon.player instanceof MirthAndMalice && __instance.equals(((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw))
        {
            CombatDeckHelper.setOtherInitialCards(__instance);
        }
        else
        {
            CombatDeckHelper.setInitialCards(__instance);
        }

        return SpireReturn.Continue();
    }
}
