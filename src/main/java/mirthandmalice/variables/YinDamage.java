package mirthandmalice.variables;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class YinDamage extends DynamicVariable {
    @Override
    public String key()
    {
        return "YINDAMAGE";
        // What you put in your localization file between ! to show your value. Eg, !myKey!.
    }

    @Override
    public boolean isModified(AbstractCard c) {
        return false;
    }

    @Override
    public int value(AbstractCard c) {
        return 0;
    }

    @Override
    public int baseValue(AbstractCard c) {
        return 0;
    }

    @Override
    public boolean upgraded(AbstractCard c) {
        return false;
    }

}
