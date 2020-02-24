package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.powers.ItPower;

import java.util.ArrayList;

public class TransferItAction extends AbstractGameAction {
    public TransferItAction(AbstractCreature source, AbstractCreature target)
    {
        this.source = source;
        this.target = target;
    }

    @Override
    public void update() {
        ArrayList<AbstractCreature> targets = new ArrayList<>();

        //iterator is more efficient but they look gross :)
        for (AbstractGameAction a : AbstractDungeon.actionManager.actions)
        {
            if (a instanceof TransferItAction)
            {
                targets.add(a.target);
            }
        }

        AbstractDungeon.actionManager.actions.removeIf((a)->a instanceof TransferItAction);

        for (AbstractCreature c : targets)
            if (c.currentHealth > target.currentHealth)
                target = c;

        if (!target.isDeadOrEscaped()) //can't tag someone who's dead.
        {
            addToTop(new ApplyPowerAction(target, source, new ItPower(target)));
            addToTop(new RemoveAllItAction(target));
        }

        this.isDone = true;
    }
}
