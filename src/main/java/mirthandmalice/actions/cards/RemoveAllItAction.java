package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.powers.ItPower;

public class RemoveAllItAction extends AbstractGameAction {
    public RemoveAllItAction()
    {

    }

    public RemoveAllItAction(AbstractCreature except)
    {
        this.source = except;
    }

    @Override
    public void update() {
        if (AbstractDungeon.player.hasPower(ItPower.ID) && !AbstractDungeon.player.equals(this.source))
            AbstractDungeon.player.powers.remove(AbstractDungeon.player.getPower(ItPower.ID));

        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
        {
            if (m.hasPower(ItPower.ID) && !m.equals(this.source))
            {
                m.powers.remove(m.getPower(ItPower.ID));
            }
        }

        this.isDone = true;
    }
}
