package mirthandmalice.actions.character;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.enemies.Spark;
import mirthandmalice.powers.VolatilePower;

public class SummonSparkAction extends AbstractGameAction {
    private static final float MAX_Y = 250.0F;
    private static final float MIN_Y = 150.0F;
    private static final float MIN_X = -350.0F;
    private static final float MAX_X = 150.0F;
    //TODO: Add special case for shield/spear

    public SummonSparkAction()
    {
        this.actionType = ActionType.SPECIAL;
    }

    @Override
    public void update() {
        //first, find a good position

        float x = MathUtils.random(MIN_X, MAX_X);
        float y = MathUtils.random(MIN_Y, MAX_Y);

        Spark m = new Spark(x, y);

        x = m.hb_x; //multiplied by scale
        y = m.hb_y;

        float actualX = m.hb.x;
        float actualY = m.hb.y;
        float adjustDistance = 0;
        float adjustAngle = 0;
        float xOffset = 0;
        float yOffset = 0;
        boolean success = false;

        //check if this is a fine position.
        while (!success)
        {
            success = true;
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters)
            {
                if (!(monster.isDeadOrEscaped() && monster.id.equals(m.id))) //we don't care about sparks that died, but other enemies could be issues (like repto daggers which have same pos)
                {
                    if (overlap(monster.hb, m.hb))
                    {
                        success = false;

                        adjustAngle = (adjustAngle + 0.1f) % (MathUtils.PI2);
                        adjustDistance += 10.0f;

                        xOffset = MathUtils.cos(adjustAngle) * adjustDistance;
                        yOffset = MathUtils.sin(adjustAngle) * adjustDistance;

                        m.hb.x = actualX + xOffset;
                        m.hb.y = actualY + yOffset;

                        break;
                    }
                }
            }
        }

        m.hb.move(m.hb.x + m.hb.width / 2.0f, m.hb.y + m.hb.height / 2.0f);
        m.hb_x = m.hb.cX - (m.drawX + m.animX);
        m.hb_y = m.hb.cY - (m.drawY + m.animY);
        m.healthHb.move(m.hb.cX, m.hb.cY - m.hb_h / 2.0F - m.healthHb.height / 2.0F);

        AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(m, m, new VolatilePower(m)));
        AbstractDungeon.actionManager.addToTop(new SpawnMonsterAction(m, true));

        this.isDone = true;
    }

    private static final float BORDER = 20.0F * Settings.scale;

    private static boolean overlap(Hitbox a, Hitbox b)
    {
        if (a.x > b.x + (b.width + BORDER) || b.x > a.x + (a.width + BORDER))
            return false;

        return !(a.y > b.y + (b.height + BORDER) || b.y > a.y + (a.height + BORDER));
    }
}
