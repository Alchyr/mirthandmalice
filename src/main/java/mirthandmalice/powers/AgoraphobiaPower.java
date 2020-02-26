package mirthandmalice.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import mirthandmalice.abstracts.BasePower;
import mirthandmalice.actions.character.ManifestAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.interfaces.OnManifestPower;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class AgoraphobiaPower extends BasePower implements OnManifestPower, NonStackablePower, CloneablePowerInterface {
    public static final String NAME = "Agoraphobia";
    public static final String POWER_ID = makeID(NAME);
    public static final PowerType TYPE = PowerType.BUFF;
    public static final boolean TURN_BASED = false;

    private boolean other;
    private boolean triggered;

    public AgoraphobiaPower(final AbstractCreature owner, boolean other, int amount)
    {
        super(NAME, TYPE, TURN_BASED, owner, null, amount);
        this.other = other;
        this.triggered = false;
    }

    @Override
    public boolean isStackable(AbstractPower power) {
        return power instanceof AgoraphobiaPower && ((AgoraphobiaPower) power).other == this.other && !this.triggered;
    }

    @Override
    public void onManifest(boolean other) {
        if (other == this.other)
        {
            this.flash();
            this.triggered = true;

            addToTop(new GainBlockAction(this.owner, this.owner, this.amount));
            addToTop(new ManifestAction(!other));
            addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new AgoraphobiaPower(this.source, this.other, this.amount);
    }

    public void updateDescription() {
        if (AbstractDungeon.player instanceof MirthAndMalice) {
            if (other) {
                this.description = descriptions()[0] + ((MirthAndMalice) AbstractDungeon.player).getOtherPlayerName() + descriptions()[1] + this.amount + descriptions()[2] + ((MirthAndMalice) AbstractDungeon.player).getOtherPlayerName() + descriptions()[3];
            }
            else {
                this.description = descriptions()[0] + AbstractDungeon.player.getTitle(AbstractDungeon.player.chosenClass) + descriptions()[1] + this.amount + descriptions()[2] + AbstractDungeon.player.getTitle(AbstractDungeon.player.chosenClass) + descriptions()[3];
            }
        }
        else {
            this.description = descriptions()[4] + this.amount + descriptions()[5];
        }
    }
}