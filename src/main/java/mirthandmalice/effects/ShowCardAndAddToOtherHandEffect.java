package mirthandmalice.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.CorruptionPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import com.megacrit.cardcrawl.vfx.combat.CardPoofEffect;
import mirthandmalice.character.MirthAndMalice;

import java.util.Iterator;

public class ShowCardAndAddToOtherHandEffect extends AbstractGameEffect {
    private static final float EFFECT_DUR = 0.8F;
    private AbstractCard card;
    private static final float PADDING;

    public ShowCardAndAddToOtherHandEffect(AbstractCard card, float offsetX, float offsetY) {
        this.card = card;// 21
        UnlockTracker.markCardAsSeen(card.cardID);// 22
        card.current_x = (float) Settings.WIDTH / 2.0F;// 23
        card.current_y = (float)Settings.HEIGHT / 2.0F;// 24
        card.target_x = offsetX;// 25
        card.target_y = offsetY;// 26
        this.duration = EFFECT_DUR;// 27
        card.drawScale = 0.75F;// 28
        card.targetDrawScale = 0.75F;// 29
        card.transparency = 0.01F;// 30
        card.targetTransparency = 1.0F;// 31
        card.fadingOut = false;// 32
        this.playCardObtainSfx();// 33
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.addToHand(card);
        }
        else
        {
            AbstractDungeon.player.hand.addToHand(card);
        }
        card.triggerWhenCopied();
        AbstractDungeon.player.hand.refreshHandLayout();// 37
        AbstractDungeon.player.hand.applyPowers();// 38
        AbstractDungeon.player.onCardDrawOrDiscard();// 39
        if (AbstractDungeon.player.hasPower(CorruptionPower.POWER_ID) && card.type == AbstractCard.CardType.SKILL) {// 41
            card.setCostForTurn(0);// 42
        }

    }

    public ShowCardAndAddToOtherHandEffect(AbstractCard card, boolean isCopy) {
        this.card = card;// 47
        this.identifySpawnLocation();// 48
        this.duration = EFFECT_DUR;// 49
        card.drawScale = 0.75F;// 50
        card.targetDrawScale = 0.75F;// 51
        card.transparency = 0.01F;// 52
        card.targetTransparency = 1.0F;// 53
        card.fadingOut = false;// 54
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.addToHand(card);
        }
        else
        {
            AbstractDungeon.player.hand.addToHand(card);
        }

        if (isCopy)
            card.triggerWhenCopied();

        AbstractDungeon.player.hand.refreshHandLayout();
        AbstractDungeon.player.hand.applyPowers();
        AbstractDungeon.player.onCardDrawOrDiscard();
        if (AbstractDungeon.player.hasPower(CorruptionPower.POWER_ID) && card.type == AbstractCard.CardType.SKILL) {
            card.setCostForTurn(0);
        }
    }

    public ShowCardAndAddToOtherHandEffect(AbstractCard card) {
        this.card = card;// 47
        this.identifySpawnLocation();// 48
        this.duration = EFFECT_DUR;// 49
        card.drawScale = 0.75F;// 50
        card.targetDrawScale = 0.75F;// 51
        card.transparency = 0.01F;// 52
        card.targetTransparency = 1.0F;// 53
        card.fadingOut = false;// 54
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.addToHand(card);
        }
        else
        {
            AbstractDungeon.player.hand.addToHand(card);
        }
        card.triggerWhenCopied();
        AbstractDungeon.player.hand.refreshHandLayout();
        AbstractDungeon.player.hand.applyPowers();
        AbstractDungeon.player.onCardDrawOrDiscard();
        if (AbstractDungeon.player.hasPower(CorruptionPower.POWER_ID) && card.type == AbstractCard.CardType.SKILL) {
            card.setCostForTurn(0);
        }
    }

    private void playCardObtainSfx() {
        int effectCount = 0;// 68
        Iterator var2 = AbstractDungeon.effectList.iterator();// 69

        while(var2.hasNext()) {
            AbstractGameEffect e = (AbstractGameEffect)var2.next();
            if (e instanceof ShowCardAndAddToHandEffect || e instanceof ShowCardAndAddToOtherHandEffect) {// 70
                ++effectCount;// 71
            }
        }

        if (effectCount < 2) {// 74
            CardCrawlGame.sound.play("CARD_OBTAIN");// 75
        }

    }// 77

    private void identifySpawnLocation() {
        int effectCount = 0;
        Iterator var4 = AbstractDungeon.effectList.iterator();

        while(var4.hasNext()) {
            AbstractGameEffect e = (AbstractGameEffect)var4.next();
            if (e instanceof ShowCardAndAddToHandEffect || e instanceof ShowCardAndAddToOtherHandEffect) {// 83
                ++effectCount;
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