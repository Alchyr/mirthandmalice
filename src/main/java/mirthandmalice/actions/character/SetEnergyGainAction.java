package mirthandmalice.actions.character;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import mirthandmalice.patch.energy_division.SetEnergyGain;
import mirthandmalice.patch.energy_division.TrackCardSource;

public class SetEnergyGainAction extends AbstractGameAction {
    private boolean other;

    public SetEnergyGainAction(boolean otherPlayer)
    {
        other = otherPlayer;
    }

    @Override
    public void update() {
        if ((TrackCardSource.useOtherEnergy && other) || (TrackCardSource.useMyEnergy && !other))
        {
            SetEnergyGain.myGain = true;
            SetEnergyGain.otherPlayerGain = false;
        }
        else
        {
            SetEnergyGain.otherPlayerGain = true;
            SetEnergyGain.myGain = false;
        }

        this.isDone = true;
    }
}
