package mirthandmalice.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import mirthandmalice.abstracts.BasePower;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class TraumaPower extends BasePower implements NonStackablePower {
    public static final String NAME = "Trauma";
    public static final String POWER_ID = makeID(NAME);
    public static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.DEBUFF;
    public static final boolean TURN_BASED = true;

    public TraumaPower(final AbstractCreature owner)
    {
        super(NAME, TYPE, TURN_BASED, owner, null, 1);
    }

    //Patched into vulnerable.

    @Override
    public void onInitialApplication() {
        if (owner.hasPower(VulnerablePower.POWER_ID))
        {
            owner.getPower(VulnerablePower.POWER_ID).updateDescription();
        }
    }

    @Override
    public void onRemove() {
        if (owner.hasPower(VulnerablePower.POWER_ID))
        {
            owner.getPower(VulnerablePower.POWER_ID).updateDescription();
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this.owner, this.owner, this, 1));
    }

    public void updateDescription() {
        if (this.amount == 1)
        {
            this.description = descriptions()[0];
        }
        else
        {
            this.description = descriptions()[1] + amount + descriptions()[2];
        }
    }
}