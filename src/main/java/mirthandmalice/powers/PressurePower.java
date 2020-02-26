package mirthandmalice.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import mirthandmalice.abstracts.BasePower;
import mirthandmalice.actions.general.AllEnemyLoseHPAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.interfaces.OnManifestPower;
import mirthandmalice.util.MultiplayerHelper;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class PressurePower extends BasePower implements OnManifestPower, NonStackablePower, CloneablePowerInterface {
    public static final String NAME = "Pressure";
    public static final String POWER_ID = makeID(NAME);
    public static final PowerType TYPE = PowerType.BUFF;
    public static final boolean TURN_BASED = false;

    private boolean other;

    public PressurePower(final AbstractCreature owner, boolean other, int amount)
    {
        super(NAME, TYPE, TURN_BASED, owner, null, amount);
        this.other = other;
    }

    @Override
    public boolean isStackable(AbstractPower power) {
        return power instanceof PressurePower && ((PressurePower) power).other == this.other;
    }

    @Override
    public void onManifest(boolean other) {
        if (other == this.other)
        {
            addToTop(new AllEnemyLoseHPAction(AbstractDungeon.player, this.amount, AbstractGameAction.AttackEffect.NONE));
            addToTop(new VFXAction(AbstractDungeon.player, new ShockWaveEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, Color.BLACK, ShockWaveEffect.ShockWaveType.ADDITIVE), 0.2F));

            this.flash();
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new PressurePower(this.source, this.other, this.amount);
    }

    public void updateDescription() {
        if (AbstractDungeon.player instanceof MirthAndMalice) {
            if (other) {
                this.description = descriptions()[0] + ((MirthAndMalice) AbstractDungeon.player).getOtherPlayerName() + descriptions()[1] + this.amount + descriptions()[2];
            }
            else {
                this.description = descriptions()[0] + AbstractDungeon.player.getTitle(AbstractDungeon.player.chosenClass) + descriptions()[1] + this.amount + descriptions()[2];
            }
        }
        else {
            this.description = descriptions()[3] + this.amount + descriptions()[4];
        }
    }
}