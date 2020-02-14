package mirthandmalice.actions.general;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.effects.ImageAboveCreatureEffect;

public class ImageAboveCreatureAction extends AbstractGameAction {
    private Texture texture;

    public ImageAboveCreatureAction(AbstractCreature target, Texture t) {
        this.source = target;

        this.texture = t;
        this.actionType = ActionType.TEXT;
        this.duration = Settings.ACTION_DUR_XFAST;
    }

    public void update() {
        AbstractDungeon.effectList.add(new ImageAboveCreatureEffect(this.source.hb.cX - this.source.animX, this.source.hb.cY + this.source.hb.height / 2.0F - this.source.animY, this.texture));

        this.isDone = true;
    }
}