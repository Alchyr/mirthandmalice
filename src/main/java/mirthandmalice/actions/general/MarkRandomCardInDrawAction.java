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
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.MathHelper;

public class MarkRandomCardInDrawAction extends AbstractGameAction
{
    private boolean fortune;
    private boolean other;
    private boolean fullRandom;

    public MarkRandomCardInDrawAction(boolean other, boolean fortune)
    {
        this(other, 1, fortune);
    }
    public MarkRandomCardInDrawAction(boolean other, int amount, boolean fortune)
    {
        this.amount = amount;
        this.fortune = fortune;
        this.other = other;
        fullRandom = false;
    }

    public MarkRandomCardInDrawAction(int amount, boolean fortune)
    {
        this.amount = amount;
        this.fortune = fortune;
        this.other = false;
        fullRandom = true;
    }

    @Override
    public void update() {
        if (fullRandom)
        {
            CardGroup cards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

            if (AbstractDungeon.player instanceof MirthAndMalice) {
                if (((MirthAndMalice) AbstractDungeon.player).isMirth)
                {
                    cards.group.addAll(AbstractDungeon.player.drawPile.group);
                    cards.group.addAll(((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.group);
                }
                else
                {
                    cards.group.addAll(((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.group);
                    cards.group.addAll(AbstractDungeon.player.drawPile.group);
                }
            }
            else
            {
                cards.group.addAll(AbstractDungeon.player.drawPile.group);
            }

            if (cards.size() >= amount)
            {
                Vector2[] points = MathHelper.getCirclePoints(Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f, (80.0f + amount * 5.0f) * Settings.scale, amount, MathUtils.random(360.0f));

                for (int i = 0; i < amount; ++i)
                {
                    AbstractCard c = cards.getRandomCard(AbstractDungeon.cardRandomRng);
                    cards.group.remove(c);

                    AbstractDungeon.effectList.add(new MarkEffect(this.fortune, points[i], getGroup(c)));
                    addToTop(new MarkCardAction(c, fortune));
                }
            }
            else if (!cards.isEmpty())
            {
                Vector2[] points = MathHelper.getCirclePoints(Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f, (80.0f + cards.size() * 5.0f) * Settings.scale, cards.size(), MathUtils.random(360.0f));

                for (int i = 0; i < amount; ++i)
                {
                    AbstractCard c = cards.group.remove(AbstractDungeon.cardRandomRng.random(cards.size() - 1));

                    AbstractDungeon.effectList.add(new MarkEffect(this.fortune, points[i++], getGroup(c)));
                    addToTop(new MarkCardAction(c, fortune));

                    if (cards.isEmpty())
                        break;
                }
            }
        }
        else if (other && AbstractDungeon.player instanceof MirthAndMalice)
        {
            CardGroup cards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            cards.group.addAll(((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.group);

            if (cards.size() >= amount)
            {
                Vector2[] points = MathHelper.getCirclePoints(Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f, (80.0f + amount * 5.0f) * Settings.scale, amount, MathUtils.random(360.0f));

                for (int i = 0; i < amount; ++i)
                {
                    AbstractCard c = cards.getRandomCard(AbstractDungeon.cardRandomRng);
                    cards.group.remove(c);

                    AbstractDungeon.effectList.add(new MarkEffect(this.fortune, points[i], ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw));
                    addToTop(new MarkCardAction(c, fortune));
                }
            }
            else if (!cards.isEmpty())
            {
                Vector2[] points = MathHelper.getCirclePoints(Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f, (80.0f + cards.size() * 5.0f) * Settings.scale, cards.size(), MathUtils.random(360.0f));

                for (int i = 0; i < amount; ++i)
                {
                    AbstractCard c = cards.group.remove(AbstractDungeon.cardRandomRng.random(cards.size() - 1));

                    AbstractDungeon.effectList.add(new MarkEffect(this.fortune, points[i++], ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw));
                    addToTop(new MarkCardAction(c, fortune));

                    if (cards.isEmpty())
                        break;
                }
            }
        }
        else
        {
            CardGroup cards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            cards.group.addAll(AbstractDungeon.player.drawPile.group);

            if (cards.size() >= amount)
            {
                Vector2[] points = MathHelper.getCirclePoints(Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f, (80.0f + amount * 5.0f) * Settings.scale, amount, MathUtils.random(360.0f));

                for (int i = 0; i < amount; ++i)
                {
                    AbstractCard c = cards.getRandomCard(AbstractDungeon.cardRandomRng);
                    cards.group.remove(c);

                    AbstractDungeon.effectList.add(new MarkEffect(this.fortune, points[i], AbstractDungeon.player.drawPile));
                    addToTop(new MarkCardAction(c, fortune));
                }
            }
            else if (!cards.isEmpty())
            {
                Vector2[] points = MathHelper.getCirclePoints(Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f, (80.0f + cards.size() * 5.0f) * Settings.scale, cards.size(), MathUtils.random(360.0f));

                for (int i = 0; i < amount; ++i)
                {
                    AbstractCard c = cards.group.remove(AbstractDungeon.cardRandomRng.random(cards.size() - 1));

                    AbstractDungeon.effectList.add(new MarkEffect(this.fortune, points[i++], AbstractDungeon.player.drawPile));
                    addToTop(new MarkCardAction(c, fortune));

                    if (cards.isEmpty())
                        break;
                }
            }
        }

        this.isDone = true;
    }

    private static CardGroup getGroup(AbstractCard c)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice && ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.contains(c))
                return ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw;

        return AbstractDungeon.player.drawPile;
    }
}