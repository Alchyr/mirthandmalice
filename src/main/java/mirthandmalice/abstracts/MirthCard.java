package mirthandmalice.abstracts;

import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.CardInfo;

public abstract class MirthCard extends BaseCard {
    public MirthCard(CardInfo info, boolean upgradesDescription)
    {
        super(CharacterEnums.MIRTHMALICE_MIRTH, info, upgradesDescription);
    }
}
