package mirthandmalice.patch.energy_division;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;
import mirthandmalice.actions.character.DontUseSpecificEnergyAction;
import mirthandmalice.actions.character.ManifestAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.card_use.DiscardToCorrectPile;
import mirthandmalice.patch.enums.CharacterEnums;

import static mirthandmalice.MirthAndMaliceMod.logger;

public class TrackCardSource {
    public static boolean useOtherEnergy = false;
    public static boolean useMyEnergy = false;
    //Reset at start and end of battle in main mod class

    //use the onCardUse stuff to track the last player that used a card this turn
    //Add an action before a card adds any actions
    //Add an action after a card adds its actions
    //Anything that triggers onUse will thus go before the first action, and anything that adds to bottom will go after the last action
    //When energy modification occurs: If last card played this turn is from unspecified player, or source is potion, affect both players.
    //If possible, also relics affect both players? Try to only card stuff be affected by cards. Main concern is X-costs, which use energy differently than other cards.


    public static boolean isPlayedByMirth()
    {
        //Use this during a card's method to check if this card was played by Mirth or Malice
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (useOtherEnergy)
            {
                return !((MirthAndMalice) AbstractDungeon.player).isMirth;
            }
            else //I played it
            {
                return ((MirthAndMalice) AbstractDungeon.player).isMirth;
            }
        }
        return false;
    }


    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "useCard"
    )
    public static class addTrackingActions
    {
        @SpirePrefixPatch
        public static void setEnergyUser(AbstractPlayer __instance, AbstractCard c, AbstractMonster m, int energyOnUse)
        {
            if (__instance instanceof MirthAndMalice) {
                if (DiscardToCorrectPile.useOtherDiscard) //Takes advantage of same code that decides where cards go when played
                {
                    if (c.type == AbstractCard.CardType.ATTACK)
                    {
                        AbstractDungeon.actionManager.addToBottom(new ManifestAction(true));
                    }
                    useOtherEnergy = true;
                }
                else //use own discard
                {
                    if (c.type == AbstractCard.CardType.ATTACK)
                    {
                        AbstractDungeon.actionManager.addToBottom(new ManifestAction(false));
                    }
                    useMyEnergy = true;
                }
            }
        }

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void resetEnergyUser(AbstractPlayer __instance, AbstractCard c, AbstractMonster m, int energyOnUse)
        {
            if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE) //can be a faster check here, since there's not much of a problem if this triggers incorrectly
                AbstractDungeon.actionManager.addToBottom(new DontUseSpecificEnergyAction());
        }


        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "dontTriggerOnUseCard");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
