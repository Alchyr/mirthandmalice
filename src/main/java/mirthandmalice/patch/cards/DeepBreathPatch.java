package mirthandmalice.patch.cards;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.cards.colorless.DeepBreath;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.actions.character.OtherPlayerDeckShuffleAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.energy_division.TrackCardSource;

@SpirePatch(
        clz = DeepBreath.class,
        method = "use"
)
public class DeepBreathPatch {
    @SpirePrefixPatch
    public static SpireReturn<?> altShuffle(DeepBreath __instance, AbstractPlayer p, AbstractMonster m)
    {
        if (TrackCardSource.useOtherEnergy && AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard.size() > 0) {
                AbstractDungeon.actionManager.addToBottom(new OtherPlayerDeckShuffleAction());
                AbstractDungeon.actionManager.addToBottom(new ShuffleAction(((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw, false));
            }

            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, __instance.magicNumber));

            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}
