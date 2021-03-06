package mirthandmalice.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import mirthandmalice.abstracts.BasePower;
import mirthandmalice.actions.cards.SkipTurnAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.util.MultiplayerHelper;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class LullPower extends BasePower implements NonStackablePower {
    public static final String NAME = "Lull";
    public static final String POWER_ID = makeID(NAME);
    public static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.DEBUFF;
    public static final boolean TURN_BASED = true;

    public boolean forMirth;

    public LullPower(final AbstractCreature owner, boolean skipMirth)
    {
        super(NAME, TYPE, TURN_BASED, owner, null, 1);

        this.forMirth = skipMirth;
    }

    @Override
    public boolean isStackable(AbstractPower power) {
        if (power instanceof LullPower)
        {
            return ((LullPower) power).forMirth == this.forMirth;
        }
        return false;
    }

    @Override
    public void atStartOfTurn() {
        this.flashWithoutSound();
        AbstractDungeon.actionManager.addToBottom(new SkipTurnAction(this.forMirth));
        AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this.owner, this.owner, this, 1));
    }

    public void updateDescription() {
        if (AbstractDungeon.player instanceof MirthAndMalice) {
            if (MultiplayerHelper.getIsOtherFromMirth(forMirth)) {
                if (this.amount == 1) {
                    this.description = descriptions()[0] + ((MirthAndMalice) AbstractDungeon.player).getOtherPlayerName() + descriptions()[1];
                } else {
                    this.description = descriptions()[0] + ((MirthAndMalice) AbstractDungeon.player).getOtherPlayerName() + descriptions()[2] + this.amount + descriptions()[5];
                }
            } else {
                if (this.amount == 1) {
                    this.description = descriptions()[0] + AbstractDungeon.player.getTitle(AbstractDungeon.player.chosenClass) + descriptions()[1];
                } else {
                    this.description = descriptions()[0] + AbstractDungeon.player.getTitle(AbstractDungeon.player.chosenClass) + descriptions()[2] + this.amount + descriptions()[5];
                }
            }
        } else {
            if (this.amount == 1) {
                this.description = descriptions()[3];
            } else {
                this.description = descriptions()[4] + this.amount + descriptions()[5];
            }
        }
    }
}