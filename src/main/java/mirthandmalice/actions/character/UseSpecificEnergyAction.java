package mirthandmalice.actions.character;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import mirthandmalice.patch.energy_division.TrackCardSource;

public class UseSpecificEnergyAction extends AbstractGameAction {
    private boolean other;
    public UseSpecificEnergyAction(boolean other)
    {
        this.other = other;
    }

    @Override
    public void update() {
        TrackCardSource.useOtherEnergy = other;
        TrackCardSource.useMyEnergy = !other;
        this.isDone = true;
    }
}