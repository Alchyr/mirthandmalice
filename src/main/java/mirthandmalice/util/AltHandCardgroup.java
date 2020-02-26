package mirthandmalice.util;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.SurroundedPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.MirthAndMaliceMod;
import mirthandmalice.patch.combat.ShowHover;

public class AltHandCardgroup extends CardGroup {
    private boolean onLeft;
    private float posMultiplier;

    public AltHandCardgroup(boolean left) {
        super(CardGroupType.HAND);
        onLeft = left;

        posMultiplier = onLeft ? 0.25f : 0.75f;
    }

    @Override
    public void refreshHandLayout() {
        if (AbstractDungeon.getCurrRoom().monsters == null || !AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
            if (AbstractDungeon.player.hasPower(SurroundedPower.POWER_ID)) {
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (AbstractDungeon.player.flipHorizontal) {
                        if (AbstractDungeon.player.drawX < m.drawX) {
                            m.applyPowers();
                        } else {
                            m.applyPowers();
                            m.removeSurroundedPower();
                        }
                    } else {
                        if (AbstractDungeon.player.drawX > m.drawX) {
                            m.applyPowers();
                        } else {
                            m.applyPowers();
                            m.removeSurroundedPower();
                        }
                    }
                }
            }
            for (AbstractOrb o : AbstractDungeon.player.orbs) {
                o.hideEvokeValues();
            }
            if (AbstractDungeon.player.hand.size() + AbstractDungeon.player.drawPile.size() + AbstractDungeon.player.discardPile.size() <= 3 && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && AbstractDungeon.getCurrRoom().monsters != null && !AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead() && AbstractDungeon.floorNum > 3) {
                UnlockTracker.unlockAchievement("PURITY");
            }

            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onRefreshHand();
            }

            float angleRange = 50.0F - (float) (8 - this.group.size()) * 6.0F;
            float incrementAngle = angleRange / (float) this.group.size();
            float sinkStart = 80.0F * Settings.scale;
            float sinkRange = 300.0F * Settings.scale;
            float incrementSink = sinkRange / (float) this.group.size() / 2.0F;
            int middle = this.group.size() / 2;

            for (int i = 0; i < this.group.size(); ++i) {
                this.group.get(i).setAngle(angleRange / 2.0F - incrementAngle * (float) i - incrementAngle / 2.0F);
                int t = i - middle;
                if (t >= 0) {
                    if (this.group.size() % 2 == 0) {
                        ++t;
                        t = -t;
                    } else {
                        t = -t;
                    }
                }

                if (this.group.size() % 2 == 0) {
                    ++t;
                }

                t = (int) ((float) t * 1.7F);
                this.group.get(i).target_y = sinkStart + incrementSink * (float) t;
            }

            for (AbstractCard c : this.group) {
                c.targetDrawScale = 0.66F;
            }

            AbstractCard c;
            switch (this.group.size()) {
                case 0:
                    if (this.equals(AbstractDungeon.player.hand) && AbstractDungeon.player instanceof MirthAndMalice)
                    {
                        ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.refreshHandLayout();
                    }
                    return;
                case 1:
                    this.group.get(0).target_x = (float) Settings.WIDTH * posMultiplier;
                    break;
                case 2:
                    this.group.get(0).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 0.35F;
                    this.group.get(1).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 0.35F;
                    break;
                case 3:
                    this.group.get(0).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 0.7F;
                    this.group.get(1).target_x = (float) Settings.WIDTH * posMultiplier;
                    this.group.get(2).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 0.7F;
                    this.group.get(0).target_y += 25.0F * Settings.scale;
                    this.group.get(2).target_y += 25.0F * Settings.scale;
                    break;
                case 4:
                    this.group.get(0).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 1.0F;
                    this.group.get(1).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 0.35F;
                    this.group.get(2).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 0.35F;
                    this.group.get(3).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 1.0F;
                    this.group.get(1).target_y -= 20.0F * Settings.scale;
                    this.group.get(2).target_y -= 20.0F * Settings.scale;

                    for (AbstractCard card : this.group)
                    {
                        card.targetDrawScale = 0.6F;
                    }
                    break;
                case 5:
                    this.group.get(0).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 1.2F;
                    this.group.get(1).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 0.6F;
                    this.group.get(2).target_x = (float) Settings.WIDTH * posMultiplier;
                    this.group.get(3).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 0.6F;
                    this.group.get(4).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 1.2F;
                    c = this.group.get(0);
                    c.target_y += 30.0F * Settings.scale;
                    c = this.group.get(2);
                    c.target_y -= 20.0F * Settings.scale;
                    c = this.group.get(4);
                    c.target_y += 30.0F * Settings.scale;

                    for (AbstractCard card : this.group)
                    {
                        card.targetDrawScale = 0.55F;
                    }
                    break;
                case 6:
                    this.group.get(0).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 1.5F;
                    this.group.get(1).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 0.9F;
                    this.group.get(2).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 0.3F;
                    this.group.get(3).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 0.3F;
                    this.group.get(4).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 0.9F;
                    this.group.get(5).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 1.5F;
                    c = this.group.get(0);
                    c.target_y += 25.0F * Settings.scale;
                    c = this.group.get(1);
                    c.target_y += 15.0F * Settings.scale;
                    c = this.group.get(4);
                    c.target_y += 15.0F * Settings.scale;
                    c = this.group.get(5);
                    c.target_y += 25.0F * Settings.scale;

                    for (AbstractCard card : this.group)
                    {
                        card.targetDrawScale = 0.5F;
                    }
                    break;
                case 7:
                    this.group.get(0).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 2.4F;
                    this.group.get(1).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 1.7F;
                    this.group.get(2).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 0.9F;
                    this.group.get(3).target_x = (float) Settings.WIDTH * posMultiplier;
                    this.group.get(4).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 0.9F;
                    this.group.get(5).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 1.7F;
                    this.group.get(6).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 2.4F;
                    c = this.group.get(0);
                    c.target_y += 25.0F * Settings.scale;
                    c = this.group.get(1);
                    c.target_y += 18.0F * Settings.scale;
                    c = this.group.get(3);
                    c.target_y -= 6.0F * Settings.scale;
                    c = this.group.get(5);
                    c.target_y += 18.0F * Settings.scale;
                    c = this.group.get(6);
                    c.target_y += 25.0F * Settings.scale;

                    for (AbstractCard card : this.group)
                    {
                        card.targetDrawScale = 0.45F;
                    }
                    break;
                case 8:
                    this.group.get(0).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 2.5F;
                    this.group.get(1).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 1.82F;
                    this.group.get(2).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 1.1F;
                    this.group.get(3).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 0.38F;
                    this.group.get(4).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 0.38F;
                    this.group.get(5).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 1.1F;
                    this.group.get(6).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 1.77F;
                    this.group.get(7).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 2.5F;
                    c = this.group.get(1);
                    c.target_y += 10.0F * Settings.scale;
                    c = this.group.get(6);
                    c.target_y += 10.0F * Settings.scale;

                    for (AbstractCard card : this.group)
                    {
                        card.targetDrawScale = 0.4F;
                    }
                case 9:
                    this.group.get(0).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 2.8F;
                    this.group.get(1).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 2.2F;
                    this.group.get(2).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 1.53F;
                    this.group.get(3).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 0.8F;
                    this.group.get(4).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 0.0F;
                    this.group.get(5).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 0.8F;
                    this.group.get(6).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 1.53F;
                    this.group.get(7).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 2.2F;
                    this.group.get(8).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 2.8F;
                    c = this.group.get(1);
                    c.target_y += 22.0F * Settings.scale;
                    c = this.group.get(2);
                    c.target_y += 18.0F * Settings.scale;
                    c = this.group.get(3);
                    c.target_y += 12.0F * Settings.scale;
                    c = this.group.get(5);
                    c.target_y += 12.0F * Settings.scale;
                    c = this.group.get(6);
                    c.target_y += 18.0F * Settings.scale;
                    c = this.group.get(7);
                    c.target_y += 22.0F * Settings.scale;

                    for (AbstractCard card : this.group)
                    {
                        card.targetDrawScale = 0.55F;
                    }
                    break;
                case 10:
                    this.group.get(0).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 2.9F;
                    this.group.get(1).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 2.4F;
                    this.group.get(2).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 1.8F;
                    this.group.get(3).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 1.1F;
                    this.group.get(4).target_x = (float) Settings.WIDTH * posMultiplier - AbstractCard.IMG_WIDTH_S * 0.4F;
                    this.group.get(5).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 0.4F;
                    this.group.get(6).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 1.1F;
                    this.group.get(7).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 1.8F;
                    this.group.get(8).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 2.4F;
                    this.group.get(9).target_x = (float) Settings.WIDTH * posMultiplier + AbstractCard.IMG_WIDTH_S * 2.9F;
                    c = this.group.get(1);
                    c.target_y += 20.0F * Settings.scale;
                    c = this.group.get(2);
                    c.target_y += 17.0F * Settings.scale;
                    c = this.group.get(3);
                    c.target_y += 12.0F * Settings.scale;
                    c = this.group.get(4);
                    c.target_y += 5.0F * Settings.scale;
                    c = this.group.get(5);
                    c.target_y += 5.0F * Settings.scale;
                    c = this.group.get(6);
                    c.target_y += 12.0F * Settings.scale;
                    c = this.group.get(7);
                    c.target_y += 17.0F * Settings.scale;
                    c = this.group.get(8);
                    c.target_y += 20.0F * Settings.scale;

                    for (AbstractCard card : this.group)
                    {
                        card.targetDrawScale = 0.5F;
                    }
                    break;
                default:
                    MirthAndMaliceMod.logger.info("WTF MATE, why so many cards... I don't have code to handle this uwu");
            }
        }

        if (this.equals(AbstractDungeon.player.hand) && AbstractDungeon.player instanceof MirthAndMalice)
        {
            AbstractCard card = AbstractDungeon.player.hoveredCard;
            if (card != null) {
                card.setAngle(0.0F);
                card.target_x = (card.current_x + card.target_x) / 2.0F;
                card.target_y = card.current_y;
            }

            for (CardQueueItem q : AbstractDungeon.actionManager.cardQueue) {
                if (q.card != null) {
                    q.card.setAngle(0.0F);
                    q.card.target_x = q.card.current_x;
                    q.card.target_y = q.card.current_y;
                }
            }

            ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.refreshHandLayout();
        }
        else
        {
            if (ShowHover.otherHoveredCard != null)
            {
                ShowHover.otherHoveredCard.setAngle(0.0F);
                ShowHover.otherHoveredCard.target_x = (ShowHover.otherHoveredCard.current_x + ShowHover.otherHoveredCard.target_x) / 2.0F;
                ShowHover.otherHoveredCard.target_y = ShowHover.otherHoveredCard.current_y;
            }
        }
    }

    @Override
    public void applyPowers() {
        super.applyPowers();

        if (this.equals(AbstractDungeon.player.hand) && AbstractDungeon.player instanceof MirthAndMalice)
        {
            ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.applyPowers();
        }
    }
}
