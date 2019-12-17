package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class PurifyAction extends AbstractGameAction {
    public PurifyAction(int damage, AbstractPlayer source, AbstractMonster target)
    {
        setValues(target, source, damage);
    }

    @Override
    public void update() {
        if (target == null)
        {
            this.isDone = true;
            return;
        }

        for (AbstractPower p : target.powers)
        {
            if (p.type == AbstractPower.PowerType.DEBUFF)
            {
                AbstractDungeon.actionManager.addToTop(new DamageAction(target, new DamageInfo(source, this.amount, DamageInfo.DamageType.NORMAL), AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(target, source, p));
            }
        }

        this.isDone = true;
    }
}