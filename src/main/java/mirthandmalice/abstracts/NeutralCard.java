package mirthandmalice.abstracts;

import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.CardInfo;

public abstract class NeutralCard extends BaseCard {
    public NeutralCard(CardInfo info, boolean upgradesDescription)
    {
        super(CharacterEnums.MIRTHMALICE_NEUTRAL, info, upgradesDescription);
    }
}