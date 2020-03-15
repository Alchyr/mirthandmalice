package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class ReciprocateAction extends AbstractGameAction {
    public ReciprocateAction(AbstractCreature source, AbstractCreature target)
    {
        this.setValues(target, source);
    }

    @Override
    public void update() {
        addToTop(new GainBlockAction(source, source, target.currentBlock));
        this.isDone = true;
    }
}
