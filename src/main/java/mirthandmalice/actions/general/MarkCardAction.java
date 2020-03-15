package mirthandmalice.actions.general;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.effects.ShowSmallCardBrieflyEffect;
import mirthandmalice.interfaces.OnMarkPower;
import mirthandmalice.patch.fortune_misfortune.FortuneMisfortune;

//ALL Marking should end up using this action.


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
            AbstractCard displayCard = c;

            if (!AbstractDungeon.player.hand.contains(c) && (!(AbstractDungeon.player instanceof MirthAndMalice) || !((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.contains(c)))
            {
                //card is not in a hand.
                displayCard = c.makeStatEquivalentCopy();
                displayCard.current_x = c.current_x;
                displayCard.current_y = c.current_y;

                AbstractDungeon.effectList.add(new ShowSmallCardBrieflyEffect(displayCard));
            }


            if (fortune)
            {
                displayCard.superFlash(FORTUNE_COLOR.cpy());
                FortuneMisfortune.Fields.fortune.set(c, FortuneMisfortune.Fields.fortune.get(c) + 1);
                if (displayCard != c)
                    FortuneMisfortune.Fields.fortune.set(displayCard, FortuneMisfortune.Fields.fortune.get(displayCard) + 1);
            }
            else
            {
                displayCard.superFlash(MISFORTUNE_COLOR.cpy());
                FortuneMisfortune.Fields.misfortune.set(c, FortuneMisfortune.Fields.misfortune.get(c) + 1);
                if (displayCard != c)
                    FortuneMisfortune.Fields.misfortune.set(displayCard, FortuneMisfortune.Fields.misfortune.get(displayCard) + 1);
            }

            for (AbstractPower p : AbstractDungeon.player.powers)
            {
                if (p instanceof OnMarkPower)
                    ((OnMarkPower) p).onMark(c, fortune);
            }
        }

        tickDuration();
    }
}
