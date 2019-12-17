package mirthandmalice.util;

import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ConservePower;
import com.megacrit.cardcrawl.relics.IceCream;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.ui.OtherEnergyPanel;

public class AltEnergyManager extends EnergyManager {
    public AltEnergyManager(int e)
    {
        super(e);
    }

    public void prep() {
        super.prep();
        OtherEnergyPanel.totalCount = 0;
    }

    @Override
    public void recharge() {
        int oldEnergy = EnergyPanel.totalCount;
        super.recharge();

        if (AbstractDungeon.player.hasRelic(IceCream.ID) || AbstractDungeon.player.hasPower(ConservePower.POWER_ID)) //had some form of energy conservation
        {
            OtherEnergyPanel.addEnergy(this.energy);
        }
        else if (EnergyPanel.totalCount == energy)
        {
            OtherEnergyPanel.setEnergy(this.energy);
        }
        else if (EnergyPanel.totalCount == oldEnergy + this.energy) //it's not the same, must have been conserved in some way.
        {
            OtherEnergyPanel.addEnergy(this.energy);
        }
        else
        {
            int bonus = EnergyPanel.totalCount - energy;
            if (oldEnergy >= bonus) //bonus is probably based on last turn energy, though possibly limited
            {
                bonus = Math.min(OtherEnergyPanel.totalCount, bonus);
            }
            OtherEnergyPanel.setEnergy(this.energy + bonus);
        }
    }

    @Override
    public void use(int e) {
        if (TrackCardSource.useMyEnergy == TrackCardSource.useOtherEnergy)
        {
            super.use(e);
            OtherEnergyPanel.useEnergy(e);
        }
        else if (TrackCardSource.useMyEnergy)
        {
            super.use(e);
        }
        else
        {
            OtherEnergyPanel.useEnergy(e);
        }
    }
}
