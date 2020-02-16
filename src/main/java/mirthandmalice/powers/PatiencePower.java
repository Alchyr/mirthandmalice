package mirthandmalice.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import mirthandmalice.abstracts.BasePower;


import static mirthandmalice.MirthAndMaliceMod.makeID;

//code also in patch.combat.PreventBlockLossPower
public class PatiencePower extends BasePower implements NonStackablePower {
    public static final String NAME = "Patience";
    public static final String POWER_ID = makeID(NAME);
    public static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    public static final boolean TURN_BASED = true;


    public PatiencePower(final AbstractCreature owner, int amount)
    {
        super(NAME, TYPE, TURN_BASED, owner, null, amount);
    }

    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            for (AbstractCard c : AbstractDungeon.player.hand.group)
                if (!c.isEthereal)
                    c.retain = true;
        }
    }

    public void atEndOfRound() {
        if (this.amount == 0) {
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        } else {
            this.addToBot(new ReducePowerAction(this.owner, this.owner, this, 1));
        }
    }

    public void updateDescription() {
        if (this.amount == 1) {
            this.description = descriptions()[0];
        } else {
            this.description = descriptions()[1] + this.amount + descriptions()[2];
        }
    }
}