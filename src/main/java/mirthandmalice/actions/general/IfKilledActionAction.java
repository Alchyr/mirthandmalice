package mirthandmalice.actions.general;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class IfKilledActionAction extends AbstractGameAction {
    private AbstractGameAction toAdd;
    public IfKilledActionAction(AbstractCreature toTest, AbstractGameAction action)
    {
        this.target = toTest;
        this.toAdd = action;
    }

    @Override
    public void update() {
        if ((this.target.isDying || this.target.currentHealth <= 0) && !this.target.halfDead) {
            AbstractDungeon.actionManager.addToTop(toAdd);
        }
        this.isDone = true;
    }
}
