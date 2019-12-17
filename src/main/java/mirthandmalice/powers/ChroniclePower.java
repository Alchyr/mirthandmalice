package mirthandmalice.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import mirthandmalice.abstracts.BasePower;
import mirthandmalice.util.CombatDeckHelper;

public class ChroniclePower extends BasePower {
    public static final String NAME = "Chronicle";
    public static final AbstractPower.PowerType TYPE = AbstractPower.PowerType.BUFF;
    public static final boolean TURN_BASED = false;

    public ChroniclePower(final AbstractCreature owner, int amount)
    {
        super(NAME, TYPE, TURN_BASED, owner, null, amount);
    }

    @Override
    public void onCardDraw(AbstractCard c) {
        if (!CombatDeckHelper.inInitialDeck(c) && !CombatDeckHelper.inOtherInitialDeck(c))
        {
            this.flash();
            AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, this.amount), this.amount));
        }
    }

    /*@Override
    public boolean betterOnApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (!target.isPlayer && owner.equals(source) && power.type == PowerType.DEBUFF)
        {
            AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, this.amount), this.amount));
        }
        return true;
    }*/

    public void updateDescription() {
        this.description = descriptions()[0] + this.amount + descriptions()[1];
    }
}