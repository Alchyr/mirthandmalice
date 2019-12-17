package mirthandmalice.abstracts;

import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.CardInfo;

public abstract class MaliceCard extends BaseCard {
    public MaliceCard(CardInfo info, boolean upgradesDescription)
    {
        super(CharacterEnums.MIRTHMALICE_MALICE, info, upgradesDescription);
    }
}