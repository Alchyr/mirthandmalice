package mirthandmalice.actions.character;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import mirthandmalice.patch.energy_division.SetEnergyGain;

public class ResetEnergyGainAction extends AbstractGameAction {
    @Override
    public void update() {
        SetEnergyGain.myGain = false;
        SetEnergyGain.otherPlayerGain = false;
        this.isDone = true;
    }
}
