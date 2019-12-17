package mirthandmalice.actions.character;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import mirthandmalice.patch.energy_division.SetEnergyGain;
import mirthandmalice.patch.energy_division.TrackCardSource;

public class DontUseSpecificEnergyAction extends AbstractGameAction {
    public DontUseSpecificEnergyAction()
    {
        this.actionType = ActionType.DAMAGE; //damage actions aren't cleared post-combat.
    }

    @Override
    public void update() {
        TrackCardSource.useOtherEnergy = false;
        TrackCardSource.useMyEnergy = false;
        SetEnergyGain.otherPlayerGain = false;
        SetEnergyGain.myGain = false;
        this.isDone = true;
    }
}