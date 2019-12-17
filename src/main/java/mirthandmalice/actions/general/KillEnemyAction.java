package mirthandmalice.actions.general;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.DeckPoofEffect;

public class KillEnemyAction extends AbstractGameAction {
    private AbstractMonster m;

    public KillEnemyAction(AbstractMonster m)
    {
        this.actionType = ActionType.SPECIAL;
        this.m = m;
    }

    @Override
    public void update() {
        m.currentHealth = 0;

        loseHP(999999);

        m.die(); //(hopefully) ensure death

        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            AbstractDungeon.actionManager.cleanCardQueue();
            AbstractDungeon.effectList.add(new DeckPoofEffect(64.0F * Settings.scale, 64.0F * Settings.scale, true));
            AbstractDungeon.effectList.add(new DeckPoofEffect((float)Settings.WIDTH - 64.0F * Settings.scale, 64.0F * Settings.scale, false));
            AbstractDungeon.overlayMenu.hideCombatPanels();
        }

        if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
            AbstractDungeon.actionManager.clearPostCombatActions();
        }
        this.isDone = true;
    }


    private void loseHP(int damageAmount)
    {
        m.loseBlock();

        m.currentHealth -= damageAmount;
        if (m.currentHealth < 0) {
            m.currentHealth = 0;
        }

        m.healthBarUpdatedEvent();
    }
}