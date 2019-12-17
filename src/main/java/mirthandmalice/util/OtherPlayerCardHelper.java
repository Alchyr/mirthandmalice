package mirthandmalice.util;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.util.ArrayList;

public class OtherPlayerCardHelper {
    //expand these methods later
    public static AbstractCard returnSomewhatRandomCardInCombat()
    {
        ArrayList<AbstractCard> cardList = new ArrayList<>();
        //can't use card pools since each player has a different card pool

        for (AbstractCard c : CardLibrary.getAllCards())
        {

        }
        return null; //TODO: actually do this
    }
}
