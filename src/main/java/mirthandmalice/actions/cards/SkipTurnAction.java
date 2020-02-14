package mirthandmalice.actions.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import mirthandmalice.character.MirthAndMalice;

public class SkipTurnAction extends AbstractGameAction {
    private boolean forMirth;

    public SkipTurnAction(boolean forMirth)
    {
        this.forMirth = forMirth;
    }

    @Override
    public void update() {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (((MirthAndMalice) AbstractDungeon.player).isMirth == forMirth)
            {
                AbstractDungeon.actionManager.callEndTurnEarlySequence();
                AbstractDungeon.effectList.add(new BorderFlashEffect(forMirth ? Color.WHITE : Color.DARK_GRAY, true));
            }
        }
        this.isDone = true;
    }
}
