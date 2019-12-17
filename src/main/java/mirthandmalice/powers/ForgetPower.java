package mirthandmalice.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import mirthandmalice.abstracts.BasePower;

public class ForgetPower extends BasePower {
    public static final String NAME = "Forget";
    public static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.DEBUFF;
    public static final boolean TURN_BASED = true;

    public ForgetPower(final AbstractCreature owner, final AbstractCreature source, int amt)
    {
        super(NAME, TYPE, TURN_BASED, owner, source, amt);
        this.priority = 999999;
    }

    @Override
    public void updateDescription() {
        this.description = descriptions()[0] + this.amount + descriptions()[1];


    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL) {
            AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(this.owner, this.source, new WeakPower(this.owner, this.amount, false), this.amount, false));
            this.flash();
        }
        return damageAmount;
    }

    @Override
    public void atStartOfTurn() {
        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
    }
}
