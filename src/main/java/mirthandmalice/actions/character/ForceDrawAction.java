package mirthandmalice.actions.character;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import mirthandmalice.patch.actions.DrawCardActionModifications;

public class ForceDrawAction extends AbstractGameAction {
    private boolean other;

    public ForceDrawAction(boolean other, int amount)
    {
        this(null, other, amount);
    }
    public ForceDrawAction(AbstractCreature source, boolean other, int amount)
    {
        this.source = source;
        this.amount = amount;
        this.other = other;
    }

    @Override
    public void update() {
        DrawCardAction forceDraw = new DrawCardAction(source, amount);

        DrawCardActionModifications.DrawFields.forceDraw.set(forceDraw, other ? -1 : 1);

        addToTop(forceDraw);

        this.isDone = true;
    }
}
