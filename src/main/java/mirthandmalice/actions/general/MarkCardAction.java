package mirthandmalice.actions.general;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import mirthandmalice.patch.fortune_misfortune.FortuneMisfortune;

public class MarkCardAction extends AbstractGameAction {
    private AbstractCard c;
    private boolean fortune;

    private static Color MISFORTUNE_COLOR = new Color(0.1f, 0.1f, 0.2f, 1.0f);
    private static Color FORTUNE_COLOR = new Color(0.9f, 0.77f, 0.23f, 1.0f);

    public MarkCardAction(AbstractCard c, boolean fortune) //true fortune, false misfortune
    {
        this.c = c;
        this.fortune = fortune;

        this.duration = this.startDuration = 0.1f;
    }

    @Override
    public void update() {
        if (this.duration == this.startDuration)
        {
            if (fortune)
            {
                c.superFlash(FORTUNE_COLOR.cpy());
                FortuneMisfortune.Fields.fortune.set(c, FortuneMisfortune.Fields.fortune.get(c) + 1);
            }
            else
            {
                c.superFlash(MISFORTUNE_COLOR.cpy());
                FortuneMisfortune.Fields.misfortune.set(c, FortuneMisfortune.Fields.misfortune.get(c) + 1);
            }
        }

        tickDuration();
    }
}
