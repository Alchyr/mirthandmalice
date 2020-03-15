package mirthandmalice.actions.general;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.effects.MarkEffect;
import mirthandmalice.util.MathHelper;

import java.util.ArrayList;
import java.util.function.Predicate;

public class MarkCardsInHandAction extends AbstractGameAction {
    private boolean fortune;
    private boolean other;
    private Predicate<AbstractCard> condition;

    private ArrayList<AbstractGameEffect> markEffects;

    public MarkCardsInHandAction(boolean fortune, boolean other, int amount, Predicate<AbstractCard> condition)
    {
        this.fortune = fortune;
        this.amount = amount;
        this.other = other;
        this.condition = condition;

        markEffects = new ArrayList<>();
    }

    public MarkCardsInHandAction(boolean fortune, boolean other, Predicate<AbstractCard> condition)
    {
        this(fortune, other, -1, condition);
    }

    public MarkCardsInHandAction(boolean fortune, boolean other, int amount)
    {
        this(fortune, other, amount, (c)->true);
    }

    @Override
    public void update() {
        if (markEffects.isEmpty() && !this.isDone)
        {
            ArrayList<AbstractCard> validTargets = new ArrayList<>();
            if (other && AbstractDungeon.player instanceof MirthAndMalice)
            {
                for (AbstractCard c : ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.group)
                {
                    if (condition.test(c))
                    {
                        validTargets.add(c);
                    }
                }
            }
            else
            {
                for (AbstractCard c : AbstractDungeon.player.hand.group)
                {
                    if (condition.test(c))
                    {
                        validTargets.add(c);
                    }
                }
            }

            if (amount == -1)
            {
                Vector2[] points = MathHelper.getCirclePoints(Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f, (80.0f + validTargets.size() * 5.0f) * Settings.scale, validTargets.size(), MathUtils.random(360.0f));

                int i = 0;

                for (AbstractCard c : validTargets)
                {
                    AbstractDungeon.effectList.add(new MarkEffect(this.fortune, points[i++], c));
                    AbstractDungeon.actionManager.addToTop(new MarkCardAction(c, this.fortune));
                }
            }
            else
            {
                Vector2[] points = MathHelper.getCirclePoints(Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f, (80.0f + amount * 5.0f) * Settings.scale, amount, MathUtils.random(360.0f));

                for (int i = 0; i < amount; ++i)
                {
                    AbstractCard c = validTargets.remove(AbstractDungeon.cardRandomRng.random(validTargets.size() - 1));

                    AbstractDungeon.effectList.add(new MarkEffect(this.fortune, points[i], c));
                    AbstractDungeon.actionManager.addToTop(new MarkCardAction(c, this.fortune));

                    if (validTargets.isEmpty())
                        break;
                }
            }
        }

        this.isDone = true;
    }
}
