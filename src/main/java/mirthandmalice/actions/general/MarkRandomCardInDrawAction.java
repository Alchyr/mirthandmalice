package mirthandmalice.actions.general;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.energy_division.TrackCardSource;

public class MarkRandomCardInDrawAction extends AbstractGameAction
{
    private boolean fortune;
    private boolean other;
    private boolean fullRandom;

    public MarkRandomCardInDrawAction(boolean other, boolean fortune)
    {
        this(other, 1, fortune);
    }
    public MarkRandomCardInDrawAction(boolean other, int amount, boolean fortune)
    {
        this.amount = amount;
        this.fortune = fortune;
        this.other = other;
        fullRandom = false;
    }

    public MarkRandomCardInDrawAction(int amount, boolean fortune)
    {
        this.amount = amount;
        this.fortune = fortune;
        this.other = false;
        fullRandom = true;
    }

    @Override
    public void update() {
        if (fullRandom)
        {
            CardGroup cards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

            if (AbstractDungeon.player instanceof MirthAndMalice) {
                if (((MirthAndMalice) AbstractDungeon.player).isMirth)
                {
                    cards.group.addAll(AbstractDungeon.player.drawPile.group);
                    cards.group.addAll(((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.group);
                }
                else
                {
                    cards.group.addAll(((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.group);
                    cards.group.addAll(AbstractDungeon.player.drawPile.group);
                }
            }
            else
            {
                cards.group.addAll(AbstractDungeon.player.drawPile.group);
            }

            if (cards.size() >= amount)
            {
                for (int i = 0; i < amount; ++i)
                {
                    AbstractCard c = cards.getRandomCard(AbstractDungeon.cardRandomRng);
                    cards.group.remove(c);
                    addToTop(new MarkCardAction(c, fortune));
                }
            }
            else if (!cards.isEmpty())
            {
                for (int i = 0; i < amount; ++i)
                {
                    AbstractCard c = ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.getRandomCard(AbstractDungeon.cardRandomRng);
                    addToTop(new MarkCardAction(c, fortune));
                }
            }
        }
        else if (other && AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.size() >= amount)
            {
                CardGroup cards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                cards.group.addAll(((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.group);

                for (int i = 0; i < amount; ++i)
                {
                    AbstractCard c = cards.getRandomCard(AbstractDungeon.cardRandomRng);
                    cards.group.remove(c);
                    addToTop(new MarkCardAction(c, fortune));
                }
            }
            else if (!((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.isEmpty())
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
            if (AbstractDungeon.player.drawPile.size() >= amount)
            {
                CardGroup cards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                cards.group.addAll(AbstractDungeon.player.drawPile.group);

                for (int i = 0; i < amount; ++i)
                {
                    AbstractCard c = cards.getRandomCard(AbstractDungeon.cardRandomRng);
                    cards.group.remove(c);
                    addToTop(new MarkCardAction(c, fortune));
                }
            }
            else if (!AbstractDungeon.player.drawPile.isEmpty())
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