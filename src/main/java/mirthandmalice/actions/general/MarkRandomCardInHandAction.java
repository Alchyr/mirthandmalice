package mirthandmalice.actions.general;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.energy_division.TrackCardSource;

public class MarkRandomCardInHandAction extends AbstractGameAction {
    private boolean fortune;

    public MarkRandomCardInHandAction(boolean fortune)
    {
        this(1, fortune);
    }
    public MarkRandomCardInHandAction(int amount, boolean fortune)
    {
        this.amount = amount;
        this.fortune = fortune;
    }

    @Override
    public void update() {
        if (TrackCardSource.useOtherEnergy && AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (!((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.isEmpty())
            {
                for (int i = 0; i < amount; ++i)
                {
                    AbstractCard c = ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.getRandomCard(AbstractDungeon.cardRandomRng);
                    addToTop(new MarkCardAction(c, fortune));
                }
            }
        }
        else
        {
            if (!AbstractDungeon.player.hand.isEmpty())
            {
                for (int i = 0; i < amount; ++i)
                {
                    AbstractCard c = AbstractDungeon.player.hand.getRandomCard(AbstractDungeon.cardRandomRng);
                    addToTop(new MarkCardAction(c, fortune));
                }
            }
        }

        this.isDone = true;
    }
}
