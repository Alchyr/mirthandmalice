package mirthandmalice.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import mirthandmalice.abstracts.BasePower;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.interfaces.OnManifestPower;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class JoyPower extends BasePower implements OnManifestPower, NonStackablePower, CloneablePowerInterface {
    public static final String NAME = "Joy";
    public static final String POWER_ID = makeID(NAME);
    public static final PowerType TYPE = PowerType.BUFF;
    public static final boolean TURN_BASED = false;

    private boolean other;

    public JoyPower(final AbstractCreature owner, boolean other, int amount)
    {
        super(NAME, TYPE, TURN_BASED, owner, null, amount);
        this.other = other;
    }

    @Override
    public boolean isStackable(AbstractPower power) {
        return power instanceof JoyPower && ((JoyPower) power).other == this.other;
    }

    @Override
    public void onManifest(boolean other) {
        if (other == this.other)
        {
            this.flash();
            for (AbstractMonster m : AbstractDungeon.getMonsters().monsters)
            {
                if (!m.isDeadOrEscaped())
                {
                    AbstractDungeon.actionManager.addToTop(new GainBlockAction(m, AbstractDungeon.player, this.amount, true));
                }
            }
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new JoyPower(this.source, this.other, this.amount);
    }

    public void updateDescription() {
        if (AbstractDungeon.player instanceof MirthAndMalice) {
            if (other) {
                this.description = descriptions()[0] + ((MirthAndMalice) AbstractDungeon.player).getOtherPlayerName() + descriptions()[1] + this.amount + descriptions()[2];
            }
            else {
                this.description = descriptions()[0] + AbstractDungeon.player.getLocalizedCharacterName() + descriptions()[1] + this.amount + descriptions()[2];
            }
        }
        else {
            this.description = descriptions()[3] + this.amount + descriptions()[4];
        }
    }
}