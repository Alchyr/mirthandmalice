package mirthandmalice.actions.general;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.energy_division.TrackCardSource;

public class MarkRandomCardInDrawAction extends AbstractGameAction
{
    private boolean fortune;
    private boolean other;

    public MarkRandomCardInDrawAction(boolean other, boolean fortune)
    {
        this(other, 1, fortune);
    }
    public MarkRandomCardInDrawAction(boolean other, int amount, boolean fortune)
    {
        this.amount = amount;
        this.fortune = fortune;
        this.other = other;
    }

    @Override
    public void update() {
        if (other && AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (!((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.isEmpty())
            {
                for (int i = 0; i < amount; ++i)
                {
                    AbstractCard c = ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.getRandomCard(AbstractDungeon.cardRandomRng);
                    addToTop(new MarkCardAction(c, fortune));
                }
            }
        }
        else
        {
            if (!AbstractDungeon.player.drawPile.isEmpty())
            {
                for (int i = 0; i < amount; ++i)
                {
                    AbstractCard c = AbstractDungeon.player.drawPile.getRandomCard(AbstractDungeon.cardRandomRng);
                    addToTop(new MarkCardAction(c, fortune));
                }
            }
        }

        this.isDone = true;
    }
}