package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.energy_division.TrackCardSource;

public class ProtectorAction extends AbstractGameAction {
    public ProtectorAction(AbstractCreature source, int amount)
    {
        this.source = source;
        this.amount = amount;
    }

    @Override
    public void update() {
        if (TrackCardSource.useMyEnergy)
        {
            if (AbstractDungeon.player instanceof MirthAndMalice)
            {
                for (AbstractCard c : ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.group)
                {
                    AbstractDungeon.actionManager.addToTop(new GainBlockAction(source, source, amount));
                }
            }
        }
        else
        {
            for (AbstractCard c : AbstractDungeon.player.hand.group)
            {
                AbstractDungeon.actionManager.addToTop(new GainBlockAction(source, source, amount));
            }
        }

        this.isDone = true;
    }
}
