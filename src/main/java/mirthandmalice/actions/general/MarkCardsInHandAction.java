package mirthandmalice.actions.general;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;

import java.util.ArrayList;
import java.util.function.Predicate;

public class MarkCardsInHandAction extends AbstractGameAction {
    private boolean fortune;
    private boolean other;
    private Predicate<AbstractCard> condition;

    public MarkCardsInHandAction(boolean fortune, boolean other, int amount, Predicate<AbstractCard> condition)
    {
        this.fortune = fortune;
        this.amount = amount;
        this.other = other;
        this.condition = condition;
    }

    public MarkCardsInHandAction(boolean fortune, boolean other, Predicate<AbstractCard> condition)
    {
        this(fortune, other, -1, condition);
    }

    public MarkCardsInHandAction(boolean fortune, boolean other, int amount)
    {
        this(fortune, other, amount, (c)->true);
    }

    @Override
    public void update() {
        ArrayList<AbstractCard> validTargets = new ArrayList<>();
        if (other && AbstractDungeon.player instanceof MirthAndMalice)
        {
            for (AbstractCard c : ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.group)
            {
                if (condition.test(c))
                {
                    validTargets.add(c);
                }
            }
        }
        else
        {
            for (AbstractCard c : AbstractDungeon.player.hand.group)
            {
                if (condition.test(c))
                {
                    validTargets.add(c);
                }
            }
        }

        if (amount == -1)
        {
            for (AbstractCard c : validTargets)
            {
                AbstractDungeon.actionManager.addToTop(new MarkCardAction(c, this.fortune));
            }
        }
        else
        {
            for (int i = 0; i < amount; ++i)
            {
                AbstractDungeon.actionManager.addToTop(new MarkCardAction(validTargets.remove(AbstractDungeon.cardRandomRng.random(validTargets.size() - 1)), this.fortune));
            }
        }

        this.isDone = true;
    }
}
