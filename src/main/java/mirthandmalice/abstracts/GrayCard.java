package mirthandmalice.abstracts;

import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.CardInfo;

public abstract class GrayCard extends BaseCard {
    public GrayCard(CardInfo info, boolean upgradesDescription)
    {
        super(CharacterEnums.MIRTHMALICE_NEUTRAL, info, upgradesDescription);
    }
}