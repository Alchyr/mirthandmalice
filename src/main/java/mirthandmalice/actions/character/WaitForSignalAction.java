package mirthandmalice.actions.character;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.effects.ScreenFadeEffect;

public class WaitForSignalAction extends AbstractGameAction {
    public static int signal = 0;

    private String msg;
    private boolean sent;
    private ScreenFadeEffect waitEffect;

    public WaitForSignalAction(String waitMessage)
    {
        this.actionType = ActionType.WAIT;
        this.msg = waitMessage;
        this.sent = false;
    }

    @Override
    public void update() {
        if (!sent) {
            sent = true;
            if (!msg.isEmpty()) {
                waitEffect = new ScreenFadeEffect(msg);
                AbstractDungeon.effectList.add(waitEffect);
            }
        }
        if (signal > 0) {
            --signal;
            if (waitEffect != null)
                waitEffect.finishing = true;
            this.isDone = true;
        }
    }
}
