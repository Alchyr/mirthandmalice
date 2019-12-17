package mirthandmalice.actions.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BlockPerAttackPlayedAction extends AbstractGameAction {
    private int block;

    public BlockPerAttackPlayedAction(AbstractCreature target, int block) {
        this.target = target;
        this.block = block;
        this.actionType = ActionType.BLOCK;
    }

    public void update() {
        if (this.target != null) {
            int count = 0;

            for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisTurn)
            {
                if (c.type == AbstractCard.CardType.ATTACK) {
                    ++count;
                }
            }

            for(int i = 0; i < count; ++i) {
                AbstractDungeon.actionManager.addToTop(new GainBlockAction(this.target, this.target, this.block));
            }
        }
        this.isDone = true;
    }
}