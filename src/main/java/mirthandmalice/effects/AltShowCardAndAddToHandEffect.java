package mirthandmalice.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.CorruptionPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.CardPoofEffect;

import java.util.Iterator;

public class AltShowCardAndAddToHandEffect extends AbstractGameEffect {
    private static final float EFFECT_DUR = 0.8F;
    private AbstractCard card;
    private static final float PADDING;


    public AltShowCardAndAddToHandEffect(AbstractCard card, boolean isCopy) {
        this.card = card;
        this.identifySpawnLocation();
        this.duration = EFFECT_DUR;
        card.drawScale = 0.75F;
        card.targetDrawScale = 0.75F;
        card.transparency = 0.01F;
        card.targetTransparency = 1.0F;
        card.fadingOut = false;
        AbstractDungeon.player.hand.addToHand(card);
        if (isCopy)
            card.triggerWhenCopied();
        AbstractDungeon.player.hand.refreshHandLayout();
        AbstractDungeon.player.hand.applyPowers();
        AbstractDungeon.player.onCardDrawOrDiscard();
        if (AbstractDungeon.player.hasPower(CorruptionPower.POWER_ID) && card.type == AbstractCard.CardType.SKILL) {
            card.setCostForTurn(0);
        }

    }

    private void identifySpawnLocation() {
        int effectCount = 0;
        Iterator var4 = AbstractDungeon.effectList.iterator();

        while(var4.hasNext()) {
            AbstractGameEffect e = (AbstractGameEffect)var4.next();
            if (e instanceof AltShowCardAndAddToHandEffect) {
                ++effectCount;// 84
            }
        }

        this.card.target_y = (float)Settings.HEIGHT * 0.5F;
        switch(effectCount) {
            case 0:
                this.card.target_x = (float)Settings.WIDTH * 0.5F;
                break;
            case 1:
                this.card.target_x = (float)Settings.WIDTH * 0.5F - PADDING - AbstractCard.IMG_WIDTH;
                break;
            case 2:
                this.card.target_x = (float)Settings.WIDTH * 0.5F + PADDING + AbstractCard.IMG_WIDTH;
                break;
            case 3:
                this.card.target_x = (float)Settings.WIDTH * 0.5F - (PADDING + AbstractCard.IMG_WIDTH) * 2.0F;
                break;
            case 4:
                this.card.target_x = (float)Settings.WIDTH * 0.5F + (PADDING + AbstractCard.IMG_WIDTH) * 2.0F;
                break;
            default:
                this.card.target_x = MathUtils.random((float)Settings.WIDTH * 0.1F, (float)Settings.WIDTH * 0.9F);
                this.card.target_y = MathUtils.random((float)Settings.HEIGHT * 0.2F, (float)Settings.HEIGHT * 0.8F);
        }

        this.card.current_x = this.card.target_x;
        this.card.current_y = this.card.target_y - 200.0F * Settings.scale;
        AbstractDungeon.effectsQueue.add(new CardPoofEffect(this.card.target_x, this.card.target_y));
    }

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        this.card.update();
        if (this.duration < 0.0F) {
            this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        if (!this.isDone) {
            this.card.render(sb);
        }

    }

    public void dispose() {
    }

    static {
        PADDING = 25.0F * Settings.scale;
    }
}