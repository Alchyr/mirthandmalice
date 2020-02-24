package mirthandmalice.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import mirthandmalice.abstracts.BasePower;
import mirthandmalice.actions.cards.TransferItAction;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class ItPower extends BasePower {
    public static final String NAME = "It";
    public static final String ID = makeID(NAME);
    public static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    public static final boolean TURN_BASED = false;

    public ItPower(final AbstractCreature owner)
    {
        super(NAME, TYPE, TURN_BASED, owner, null, -1);
    }
    //Implemented in FortuneAction

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (damageAmount > 0 && target != this.owner && info.type == DamageInfo.DamageType.NORMAL) {
            addToTop(new TransferItAction(this.owner, target));
        }
    }

    public void updateDescription() {
        if (this.owner.isPlayer)
        {
            this.description = descriptions()[0];
        }
        else
        {
            this.description = descriptions()[1];
        }
    }
}