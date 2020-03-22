package mirthandmalice.actions.general;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.effects.MarkEffect;
import mirthandmalice.util.MathHelper;

public class MarkTopCardInDrawAction extends AbstractGameAction
{
    private boolean fortune;
    private boolean other;

    public MarkTopCardInDrawAction(boolean other, boolean fortune)
    {
        this.other = other;
        this.fortune = fortune;
    }

    @Override
    public void update() {
        CardGroup cards = AbstractDungeon.player.drawPile;
        if (other && AbstractDungeon.player instanceof MirthAndMalice)
        {
            cards = ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw;
        }

        if (cards.isEmpty())
        {
            this.isDone = true;
            return;
        }

        AbstractCard c = cards.getTopCard();

        AbstractDungeon.effectList.add(new MarkEffect(this.fortune, Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f, AbstractDungeon.player.drawPile));
        addToTop(new MarkCardAction(c, fortune));

        this.isDone = true;
    }
}