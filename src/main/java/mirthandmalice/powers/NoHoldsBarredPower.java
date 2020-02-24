package mirthandmalice.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;
import mirthandmalice.abstracts.BasePower;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class NoHoldsBarredPower extends BasePower implements NonStackablePower {
    public static final String NAME = "NoHoldsBarred";
    public static final String POWER_ID = makeID(NAME);
    public static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    public static final boolean TURN_BASED = false;


    public NoHoldsBarredPower(final AbstractCreature owner, int amount)
    {
        super(NAME, TYPE, TURN_BASED, owner, null, amount);
    }

    @Override
    public void atStartOfTurn() {
        this.flash();
        addToBot(new ApplyPowerAction(this.owner, this.owner, new VigorPower(this.owner, this.amount), this.amount));
    }

    public void updateDescription() {
        this.description = descriptions()[0] + this.amount + descriptions()[1];
    }
}