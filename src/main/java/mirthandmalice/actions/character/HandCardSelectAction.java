package mirthandmalice.actions.character;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;

import java.util.ArrayList;

//Action for reordering hand when other player opens a HandCardSelectScreen
public class HandCardSelectAction extends AbstractGameAction {
    private static ArrayList<Integer> finalPositions = new ArrayList<>();
    private static ArrayList<String[]> queuedPositions = new ArrayList<>();

    public static void setFinalPositions(String[] data)
    {
        if (!finalPositions.isEmpty())
        {
            queuedPositions.add(data);
        }
        else
        {
            for (String index : data)
            {
                finalPositions.add(Integer.parseInt(index));
            }
        }
    }

    public HandCardSelectAction()
    {
        this.duration = 1.0f;
        this.actionType = ActionType.WAIT;
    }

    @Override
    public void update() {
        if (!(AbstractDungeon.player instanceof MirthAndMalice)) {
            this.isDone = true;
        }
        else if (!finalPositions.isEmpty())
        {
            MirthAndMalice p = (MirthAndMalice) AbstractDungeon.player;
            ArrayList<AbstractCard> newLayout = new ArrayList<>();
            for (int i : finalPositions)
            {
                //i is current index of card in hand
                if (i < p.otherPlayerHand.group.size()) //i is a valid index within hand
                {
                    newLayout.add(p.otherPlayerHand.group.get(i)); //add to start of hand
                }
            }
            //newLayout should be all set up
            p.otherPlayerHand.group.clear();
            p.otherPlayerHand.group.addAll(newLayout);

            finalPositions.clear();
            if (!queuedPositions.isEmpty())
            {
                setFinalPositions(queuedPositions.remove(0));
            }
            this.isDone = true;
        }
        else
        {
            for (AbstractGameAction a : AbstractDungeon.actionManager.actions)
            {
                if (a instanceof WaitForSignalAction) //this should go after a signal action
                {
                    AbstractDungeon.actionManager.actions.add(AbstractDungeon.actionManager.actions.indexOf(a) + 1, new HandCardSelectAction());
                    this.isDone = true;
                    return;
                }
            }
        }
    }
}
