package mirthandmalice.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import mirthandmalice.abstracts.BasePower;
import mirthandmalice.actions.general.MarkRandomCardInDrawAction;
import mirthandmalice.character.MirthAndMalice;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class SerendipityPower extends BasePower implements NonStackablePower {
    public static final String NAME = "Serendipity";
    public static final String POWER_ID = makeID(NAME);
    public static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    public static final boolean TURN_BASED = false;

    public boolean other;

    public SerendipityPower(final AbstractCreature owner, boolean other, int amount)
    {
        super(NAME, TYPE, TURN_BASED, owner, null, amount);
        this.other = other;
    }

    @Override
    public boolean isStackable(AbstractPower power) {
        if (power instanceof SerendipityPower)
        {
            return ((SerendipityPower) power).other == this.other;
        }
        return false;
    }

    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            addToBot(new MarkRandomCardInDrawAction(this.other, this.amount, true));
        }
    }

    public void updateDescription() {
        if (AbstractDungeon.player instanceof MirthAndMalice) {
            if (other) {
                if (this.amount == 1) {
                    this.description = descriptions()[0] + ((MirthAndMalice) AbstractDungeon.player).getOtherPlayerName() + descriptions()[3];
                } else {
                    this.description = descriptions()[1] + this.amount + descriptions()[2] + ((MirthAndMalice) AbstractDungeon.player).getOtherPlayerName() + descriptions()[3];
                }
            }
            else {
                if (this.amount == 1) {
                    this.description = descriptions()[0] + AbstractDungeon.player.getLocalizedCharacterName() + descriptions()[3];
                } else {
                    this.description = descriptions()[1] + this.amount + descriptions()[2] + AbstractDungeon.player.getLocalizedCharacterName() + descriptions()[3];
                }
            }
        }
        else {
            if (this.amount == 1) {
                this.description = descriptions()[4];
            } else {
                this.description = descriptions()[5] + this.amount + descriptions()[6];
            }
        }
    }
}