package mirthandmalice.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import mirthandmalice.abstracts.BasePower;

public class AtrophyPower extends BasePower implements OnReceivePowerPower {
    public static final String NAME = "Atrophy";
    public static final PowerType TYPE = PowerType.DEBUFF;
    public static final boolean TURN_BASED = false;

    public AtrophyPower(final AbstractCreature source, final AbstractCreature owner, int amount)
    {
        super(NAME, TYPE, TURN_BASED, owner, source, amount);
    }

    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (owner.equals(target) && power.type == PowerType.DEBUFF && !owner.equals(source))
        {
            this.flashWithoutSound();
            AbstractDungeon.actionManager.addToTop(new LoseHPAction(this.owner, this.source, this.amount));
        }
        return true;
    }

    public void updateDescription() {
        this.description = descriptions()[0] + this.amount + descriptions()[1];
    }
}