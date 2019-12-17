package mirthandmalice.enemies;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import mirthandmalice.effects.CalmFireEffect;
import mirthandmalice.effects.CasualFlameParticleEffect;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Spark extends CustomMonster {
    //Has 20 hp.
    //Summon action should give a value that determines explosion damage, on death.
    //Check upon monster death if all enemies are dead. If so, die.
    public static final String ID = makeID("Spark");

    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);

    private static final float HB_WIDTH = 140.0F; //scale is all multiplied in abstract monster class
    private static final float HB_HEIGHT = 140.0F;

    private static final float MAX_OFFSET = 20.0F * Settings.scale;
    private static final float SPARK_CHANCE = 0.3f;

    private float fireTimer = 0.0F;
    private static final float FIRE_TIME = 0.025F;

    public Spark(float hb_x, float hb_y)
    {
        super(monsterStrings.NAME, ID, 20, hb_x, hb_y, HB_WIDTH, HB_HEIGHT, null);
    }

    @Override
    public void takeTurn() {
        //spark don't do things.
    }

    @Override
    protected void getMove(int i) {
        setMove((byte)0, Intent.NONE);
    }

    @Override
    public void flashIntent() {
    }

    @Override
    public void update() {
        super.update();
        if (!this.isDying) {
            this.fireTimer -= Gdx.graphics.getDeltaTime();
            if (this.fireTimer < 0.0F) {
                this.fireTimer = FIRE_TIME;
                AbstractDungeon.effectList.add(new CalmFireEffect(hb.cX, hb.cY));
                if (MathUtils.randomBoolean(SPARK_CHANCE))
                {
                    float distance = MathUtils.random(MAX_OFFSET);
                    float direction = MathUtils.random(MathUtils.PI2);
                    float xOffset = MathUtils.cos(direction) * distance;
                    float yOffset = MathUtils.sin(direction) * distance;

                    AbstractDungeon.effectList.add(new CasualFlameParticleEffect(hb.cX + xOffset,hb.cY + yOffset));
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!this.isDead && !this.escaped) {
            /*if (this.damageFlash) {
                ShaderHelper.setShader(sb, ShaderHelper.Shader.WHITE_SILHOUETTE);
            }

            if (this == AbstractDungeon.getCurrRoom().monsters.hoveredMonster) {

            }

            if (this.damageFlash) {
                ShaderHelper.setShader(sb, ShaderHelper.Shader.DEFAULT);
                --this.damageFlashFrames;
                if (this.damageFlashFrames == 0) {
                    this.damageFlash = false;
                }
            }*/

            /*if (!this.isDying && !this.isEscaping && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.player.isDead && !AbstractDungeon.player.hasRelic("Runic Dome") && this.intent != Intent.NONE && !Settings.hideCombatElements) {
                this.renderIntentVfxBehind(sb);
                this.renderIntent(sb);
                this.renderIntentVfxAfter(sb);
                this.renderDamageRange(sb);
            }*/

            this.hb.render(sb);
            //this.intentHb.render(sb);
            this.healthHb.render(sb);
        }

        if (!AbstractDungeon.player.isDead) {
            this.renderHealth(sb);
            this.renderName(sb);
        }
    }
}
