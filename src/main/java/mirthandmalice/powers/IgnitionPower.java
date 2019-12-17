package mirthandmalice.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import mirthandmalice.abstracts.BasePower;
import mirthandmalice.actions.character.SummonSparkAction;

public class IgnitionPower extends BasePower implements OnReceivePowerPower {
    public static final String NAME = "Ignition";
    public static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    public static final boolean TURN_BASED = false;

    public IgnitionPower(final AbstractCreature owner, int amount)
    {
        super(NAME, TYPE, TURN_BASED, owner, null, amount);
    }

    @Override
    public boolean onReceivePower(AbstractPower p, AbstractCreature a, AbstractCreature b) {
        if (p.ID.equals(StrengthPower.POWER_ID) && p.amount > 0)
        {
            for (int i = 0; i < this.amount; ++i)
                AbstractDungeon.actionManager.addToTop(new SummonSparkAction());
            return false;
        }
        return true;
    }

    public void updateDescription() {
        if (this.amount == 1)
        {
            this.description = descriptions()[0];
        }
        else
        {
            this.description = descriptions()[1] + this.amount + descriptions()[2];
        }
    }
}