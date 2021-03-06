package mirthandmalice.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import mirthandmalice.abstracts.BasePower;
import mirthandmalice.interfaces.OnEnemyDeathPower;

public class RuthlessPower extends BasePower implements OnEnemyDeathPower {
    public static final String NAME = "Ruthless";
    public static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    public static final boolean TURN_BASED = false;

    public RuthlessPower(final AbstractCreature owner, int amount)
    {
        super(NAME, TYPE, TURN_BASED, owner, null, amount);
    }

    @Override
    public void onEnemyDeath(AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, this.amount), this.amount));
    }

    public void updateDescription() {
        this.description = descriptions()[0] + this.amount + descriptions()[1];
    }
}