package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.abstracts.AbstractXAction;
import mirthandmalice.actions.character.ForceDrawAction;
import mirthandmalice.actions.character.ResetEnergyGainAction;
import mirthandmalice.actions.character.SetEnergyGainAction;

public class TrustAction extends AbstractXAction {
    private int bonusAmt;
    private boolean other;

    public TrustAction(boolean other, int bonusAmt)
    {
        this.duration = Settings.ACTION_DUR_FAST;
        this.actionType = AbstractGameAction.ActionType.SPECIAL;

        this.other = other;

        this.bonusAmt = bonusAmt;
    }

    @Override
    public void initialize(int totalAmount) {
        super.initialize(totalAmount);
        this.amount += bonusAmt;
    }

    public void update() {
        if (amount > 0) {
            addToTop(new ForceDrawAction(other, amount));

            if (other) {
                addToTop(new ResetEnergyGainAction());
                addToTop(new GainEnergyAction(this.amount));
                addToTop(new SetEnergyGainAction(true));
            }
            else {
                addToTop(new GainEnergyAction(this.amount));
            }
        }

        this.isDone = true;
    }
}