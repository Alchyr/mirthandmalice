package mirthandmalice.powers;

import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import mirthandmalice.abstracts.BasePower;

public class ExcessPower extends BasePower {
    public static final String NAME = "Excess";
    public static final PowerType TYPE = PowerType.DEBUFF;
    public static final boolean TURN_BASED = true;

    public ExcessPower(final AbstractCreature source, final AbstractCreature owner, int amount)
    {
        super(NAME, TYPE, TURN_BASED, owner, source, amount);
    }

    @Override
    public void atStartOfTurn() {
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            this.flashWithoutSound();
            AbstractDungeon.actionManager.addToBottom(new LoseHPAction(this.owner, this.source, this.amount));

            //add hooks to modify from removal to reduction?
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }

    public void updateDescription() {
        if (this.owner != null && !this.owner.isPlayer) {
            this.description = DESCRIPTIONS[2] + this.amount + DESCRIPTIONS[1];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        }
    }
}