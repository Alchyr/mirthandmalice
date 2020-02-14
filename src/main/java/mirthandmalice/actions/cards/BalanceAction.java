package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;

public class BalanceAction extends AbstractGameAction {
    private boolean isMirth;
    private boolean upgraded;

    public BalanceAction(boolean isMirth, boolean upgraded)
    {
        this.isMirth = isMirth;
        this.upgraded = upgraded;
    }

    @Override
    public void update() {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            int draw = 0;
            if (isMirth == ((MirthAndMalice) AbstractDungeon.player).isMirth) //self
            {
                draw = (AbstractDungeon.player.hand.size() - ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.size()) + (upgraded ? 1 : 0);
            }
            else
            {
                draw = (((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.size() - AbstractDungeon.player.hand.size()) + (upgraded ? 1 : 0);
            }

            if (draw > 0)
                addToTop(new DrawCardAction(draw));
        }

        this.isDone = true;
    }
}
