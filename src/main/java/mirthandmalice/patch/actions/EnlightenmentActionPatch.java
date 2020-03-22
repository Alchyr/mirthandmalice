package mirthandmalice.patch.actions;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.EnlightenmentAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.energy_division.TrackCardSource;

@SpirePatch(
        clz = EnlightenmentAction.class,
        method = "update"
)
public class EnlightenmentActionPatch {
    @SpirePrefixPatch
    public static SpireReturn<?> altShuffle(EnlightenmentAction __instance, float ___duration, boolean ___forCombat)
    {
        if (TrackCardSource.useOtherEnergy && AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (___duration == Settings.ACTION_DUR_FAST)
            {
                for (AbstractCard c : ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.group)
                {
                    if (c.costForTurn > 1) {
                        c.costForTurn = 1;
                        c.isCostModifiedForTurn = true;
                    }

                    if (___forCombat && c.cost > 1) {
                        c.cost = 1;
                        c.isCostModified = true;
                    }
                }

                ReflectionHacks.setPrivate(__instance, AbstractGameAction.class, "duration", ___duration - Gdx.graphics.getDeltaTime());
                return SpireReturn.Return(null);
            }
        }
        return SpireReturn.Continue();
    }
}
