package mirthandmalice.patch.manifestation;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = SpirePatch.CLASS
)
public class ManifestField {
    //Code to change who is manifested upon playing an attack can be found in energy_division.TrackCardSource
    public static SpireField<Boolean> mirthManifested = new SpireField<>(()->true);

    public static boolean isManifested()
    {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            return mirthManifested.get(AbstractDungeon.player) == ((MirthAndMalice) AbstractDungeon.player).isMirth;
        }
        return true;
    }
    public static boolean otherManifested()
    {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            return mirthManifested.get(AbstractDungeon.player) ^ ((MirthAndMalice) AbstractDungeon.player).isMirth;
        }
        return false;
    }
    public static boolean inHandManifested(AbstractCard c)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (AbstractDungeon.player.hand.contains(c))
                return isManifested();
            else if (((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.contains(c))
                return otherManifested();
        }
        return true;
    }
}
