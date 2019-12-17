package mirthandmalice.actions.general;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.vfx.combat.BlockedNumberEffect;
import com.megacrit.cardcrawl.vfx.combat.HbBlockBrokenEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;

public class YeetPlayerAction extends AbstractGameAction {
    private AbstractPlayer p;

    public YeetPlayerAction()
    {
        this.p = AbstractDungeon.player;
    }

    @Override
    public void update() {
        String damage = "9";

        p.currentHealth = 0;

        while (damage.length() < 10) {
            for (int i = 0; i < 10; i += MathUtils.random(1, 3))
                loseHP(Integer.parseInt(damage));
            damage += "9";
        }

        p.isDead = true;
        AbstractDungeon.deathScreen = new DeathScreen(AbstractDungeon.getMonsters());

        AbstractDungeon.actionManager.clearPostCombatActions();

        this.isDone = true;
    }


    private void loseHP(int damageAmount)
    {
        decrementBlock();

        if (damageAmount > 0) {
            p.useStaggerAnimation();

            p.currentHealth -= damageAmount;
            GameActionManager.hpLossThisCombat += damageAmount;
            AbstractDungeon.effectList.add(new StrikeEffect(p, MathUtils.random(0, Settings.WIDTH), MathUtils.random(0, Settings.HEIGHT), damageAmount));
            if (p.currentHealth < 0) {
                p.currentHealth = 0;
            }

            p.healthBarUpdatedEvent();
        }
    }

    private void loseHP(String damageAmount)
    {
        AbstractDungeon.effectList.add(new StrikeEffect(p, MathUtils.random(0, Settings.WIDTH), MathUtils.random(0, Settings.HEIGHT), MathUtils.randomBoolean() ? damageAmount + damageAmount + damageAmount : damageAmount + damageAmount));
    }


    private void decrementBlock()
    {
        if (p.currentBlock > 0) {
            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);
            if (Settings.SHOW_DMG_BLOCK) {
                AbstractDungeon.effectList.add(new BlockedNumberEffect(p.hb.cX, p.hb.cY + p.hb.height / 2.0F, Integer.toString(p.currentBlock)));
            }
            p.loseBlock();
            p.currentBlock = 0;

            AbstractDungeon.effectList.add(new HbBlockBrokenEffect(p.hb.cX - p.hb.width / 2.0F + BLOCK_ICON_X, p.hb.cY - p.hb.height / 2.0F + BLOCK_ICON_Y));
            CardCrawlGame.sound.play("BLOCK_BREAK");
        }
    }


    private static final float BLOCK_ICON_X = -14.0F * Settings.scale;
    private static final float BLOCK_ICON_Y = -14.0F * Settings.scale;
}
