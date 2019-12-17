package mirthandmalice.abstracts;

import com.badlogic.gdx.utils.Queue;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;

import static mirthandmalice.MirthAndMaliceMod.logger;

//import もこけね.cards.colorless.FantasyCard;

public abstract class ReceiveSignalCardsAction extends AbstractGameAction {
    public static Queue<String> signals = new Queue<>();
    public static Queue<AbstractCard> signaledCards = new Queue<>();
    public static Queue<CardGroup> signaledGroups = new Queue<>();

/*
    public static String signalFantasyCardString(FantasyCard c)
    {
        String sourceType = "";

        return "signalcard" + ("f") + c.effectString();
    }*/
    public static String signalCardString(AbstractCard c, CardGroup source, boolean otherGroups)
    {
        String sourceType = "";
        switch (source.type)
        {
            case HAND:
                sourceType = "h";
                break;
            case DRAW_PILE:
                sourceType = "d";
                break;
            case DISCARD_PILE:
                sourceType = "c";
                break;
            case EXHAUST_PILE:
                sourceType = "e";
                otherGroups = false;
                break;
        }
        if (sourceType.isEmpty() || !source.contains(c))
            return "";
        return "signalcard" + (otherGroups ? "o" : "m") + sourceType + source.group.indexOf(c);
    }
    public static String signalCardString(int index, CardGroup source, boolean otherGroups)
    {
        String sourceType = "";
        switch (source.type)
        {
            case HAND:
                sourceType = "h";
                break;
            case DRAW_PILE:
                sourceType = "d";
                break;
            case DISCARD_PILE:
                sourceType = "c";
                break;
            case EXHAUST_PILE:
                sourceType = "e";
                otherGroups = false;
                break;
        }
        if (sourceType.isEmpty())
            return "";
        return "signalcard" + (otherGroups ? "o" : "m") + sourceType + index;
    }

    public static void receiveCardString(String data)
    {
        signals.addLast(data);
    }
    public static void processCardStrings()
    {
        while (signals.size > 0)
        {
            String data = signals.removeFirst();

            if (AbstractDungeon.player instanceof MirthAndMalice)
            {
                CardGroup source = null;

                switch (data.charAt(0))
                {
                    case 'o':
                        switch (data.charAt(1))
                        {
                            case 'h':
                                source = ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand;
                                break;
                            case 'd':
                                source = ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw;
                                break;
                            case 'c':
                                source = ((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard;
                                break;
                        }
                        break;
                    case 'm':
                        switch (data.charAt(1))
                        {
                            case 'h':
                                source = AbstractDungeon.player.hand;
                                break;
                            case 'd':
                                source = AbstractDungeon.player.drawPile;
                                break;
                            case 'c':
                                source = AbstractDungeon.player.discardPile;
                                break;
                            case 'e':
                                source = AbstractDungeon.player.exhaustPile;
                                break;
                        }
                        break;
                /*case 'f':
                    AbstractCard c = FantasyCard.fromEffectString(data.substring(1));
                    signaledCards.addLast(c);
                    signaledGroups.addLast(AbstractDungeon.player.limbo);
                    return;*/
                    default:
                        logger.error("Invalid signaled card string.");
                        return;
                }

                int index = Integer.parseInt(data.substring(2));

                if (source != null && index >= 0 && index < source.group.size())
                {
                    AbstractCard c = source.group.get(index);
                    signaledCards.addLast(c);
                    signaledGroups.addLast(source);
                }
            }
        }
    }
}
