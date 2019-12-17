package mirthandmalice.util;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;

import java.util.ArrayList;

public class CombatDeckHelper {
    public static ArrayList<AbstractCard> combatDeckCards = new ArrayList<>();
    public static ArrayList<AbstractCard> otherPlayerCombatDeckCards = new ArrayList<>();

    public static void clear()
    {
        combatDeckCards.clear();
        otherPlayerCombatDeckCards.clear();
    }
    public static void setInitialCards(CardGroup deck)
    {
        combatDeckCards.clear();
        combatDeckCards.addAll(deck.group);
    }
    public static void setOtherInitialCards(CardGroup deck)
    {
        otherPlayerCombatDeckCards.clear();
        otherPlayerCombatDeckCards.addAll(deck.group);
    }


    public static boolean inInitialDeck(AbstractCard c)
    {
        return combatDeckCards.contains(c);
    }
    public static boolean inOtherInitialDeck(AbstractCard c)
    {
        return otherPlayerCombatDeckCards.contains(c);
    }
}
