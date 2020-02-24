package mirthandmalice.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.WaterDropEffect;

public class BleedEffect extends AbstractGameEffect {
    private int count = 0;
    private float timer = 0.0F;
    
    private AbstractCreature target;

    public BleedEffect(AbstractCreature target) {
        this.target = target;
    }

    public void update() {
        this.timer -= Gdx.graphics.getDeltaTime();
        if (this.timer < 0.0F) {
            this.timer += 0.3F;
            switch(this.count) {
                case 0:
                    CardCrawlGame.sound.playA("BLOOD_SPLAT", -0.75F);
                    AbstractDungeon.effectsQueue.add(new BorderLongFlashEffect(new Color(1.0F, 0.1F, 0.1F, 1.0F)));
                    AbstractDungeon.effectsQueue.add(new WaterDropEffect(target.hb.cX, target.hb.cY + 250.0F * Settings.scale));
                    break;
                case 1:
                    AbstractDungeon.effectsQueue.add(new WaterDropEffect(target.hb.cX + 150.0F * Settings.scale, target.hb.cY - 80.0F * Settings.scale));
                    break;
                case 2:
                    AbstractDungeon.effectsQueue.add(new WaterDropEffect(target.hb.cX - 200.0F * Settings.scale, target.hb.cY + 50.0F * Settings.scale));
                    break;
                case 3:
                    AbstractDungeon.effectsQueue.add(new WaterDropEffect(target.hb.cX + 200.0F * Settings.scale, target.hb.cY + 50.0F * Settings.scale));
                    break;
                case 4:
                    AbstractDungeon.effectsQueue.add(new WaterDropEffect(target.hb.cX - 150.0F * Settings.scale, target.hb.cY - 80.0F * Settings.scale));
            }

            ++this.count;
            if (this.count == 6) {
                this.isDone = true;
            }
        }

    }

    public void render(SpriteBatch sb) {
    }

    public void dispose() {
    }
}