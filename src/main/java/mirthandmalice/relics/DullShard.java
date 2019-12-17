package mirthandmalice.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import mirthandmalice.abstracts.BaseRelic;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class DullShard extends BaseRelic {
    private static final String ID = makeID("DullShard");
    private static final String IMG = "prism";

    public DullShard()
    {
        super(ID, IMG, AbstractRelic.RelicTier.SPECIAL, AbstractRelic.LandingSound.CLINK);
    }
}
