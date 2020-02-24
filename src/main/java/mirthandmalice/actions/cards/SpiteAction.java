package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class SpiteAction extends AbstractGameAction {
    public SpiteAction(AbstractCreature target, AbstractCreature source, int amount)
    {
        this.source = source;
        this.target = target;
        this.amount = amount;
    }

    @Override
    public void update() {
        if (this.target.currentBlock > 0)
        {
            addToTop(new LoseHPAction(target, source, amount));
        }
        this.isDone = true;
    }
}
