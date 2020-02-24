package mirthandmalice.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import mirthandmalice.abstracts.BasePower;
import mirthandmalice.interfaces.OnEnemyGainBlockPower;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class CorrosionPower extends BasePower implements OnEnemyGainBlockPower, CloneablePowerInterface {
    public static final String NAME = "Corrosion";
    public static final String POWER_ID = makeID(NAME);
    public static final PowerType TYPE = PowerType.BUFF;
    public static final boolean TURN_BASED = false;

    public CorrosionPower(final AbstractCreature owner, int amount)
    {
        super(NAME, TYPE, TURN_BASED, owner, null, amount);
    }

    @Override
    public void onEnemyGainBlock(AbstractCreature m, float amt) {
        this.flashWithoutSound();
        AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(m, this.owner, new AtrophyPower(this.owner, m, this.amount), this.amount));
    }

    @Override
    public AbstractPower makeCopy() {
        return new CorrosionPower(this.owner, this.amount);
    }

    public void updateDescription() {
        this.description = descriptions()[0] + this.amount + descriptions()[1];
    }
}