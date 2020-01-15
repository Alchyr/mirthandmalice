package mirthandmalice.actions.cards;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import java.util.ArrayList;
import java.util.Iterator;

import static mirthandmalice.MirthAndMaliceMod.logger;

public class _IMPROVE extends AbstractGameAction {
    public static ArrayList<Texture> tempTextures = new ArrayList<>();

    private AbstractMonster _materia;

    public static void _clean() {
        for (Texture t : tempTextures)
        {
            t.dispose();
        }

        tempTextures.clear();
    }

    public _IMPROVE(AbstractMonster _target, AbstractCreature source) {
        this._materia = _target;
        this.source = source;
        this.actionType = ActionType.SPECIAL;
    }

    public void update() {
        try {
            TextureAtlas _form = (TextureAtlas)ReflectionHacks.getPrivate(this._materia, AbstractCreature.class, "atlas");
            if (_form != null) {
                ArrayList<Texture> regionTextures = new ArrayList<>();
                _form.getRegions();
                Iterator var3 = _form.getRegions().iterator();

                AtlasRegion r;
                while(var3.hasNext()) {
                    r = (AtlasRegion)var3.next();
                    regionTextures.add(r.getTexture());
                }

                for(int i = 0; i < regionTextures.size(); ++i) {
                    _form.getTextures().remove(regionTextures.get(i));
                    Texture _reconstructed = _refactor((Texture)regionTextures.get(i), true);
                    regionTextures.set(i, _reconstructed);
                    _form.getTextures().add(_reconstructed);
                }

                var3 = _form.getRegions().iterator();

                while(var3.hasNext()) {
                    r = (AtlasRegion)var3.next();
                    r.setTexture((Texture)regionTextures.remove(0));
                }

                CardCrawlGame.sound.play("___S");
                //AbstractDungeon.actionManager.addToTop(new DamageAction(this._materia, new DamageInfo(this.source, MathUtils.ceil(MathUtils.random(5.0F * (Float)_enablecorefix.scale.get(AbstractDungeon.player), 10.0F * (Float)_enablecorefix.scale.get(AbstractDungeon.player))), DamageType.HP_LOSS), AttackEffect.NONE));
            } else {
                Texture img = (Texture)ReflectionHacks.getPrivate(this._materia, AbstractMonster.class, "img");
                if (img != null) {
                    img = _refactor(img, false);
                    tempTextures.add(img);
                    ReflectionHacks.setPrivate(this._materia, AbstractMonster.class, "img", img);
                    CardCrawlGame.sound.play("___S");
                    //AbstractDungeon.actionManager.addToTop(new DamageAction(this._materia, new DamageInfo(this.source, MathUtils.ceil(MathUtils.random(5.0F * (Float)_enablecorefix.scale.get(AbstractDungeon.player), 10.0F * (Float)_enablecorefix.scale.get(AbstractDungeon.player))), DamageType.HP_LOSS), AttackEffect.NONE));
                } else {
                    logger.error("Materia has no data: " + this._materia.id);
                }
            }
        } catch (Exception var5) {
            logger.error("Failed to reconstruct materia: " + this._materia.id);
        }

        this.isDone = true;
    }



    public static Texture _refactor(Texture t) {
        return _refactor(t, false);
    }

    public static Texture _refactor(Texture t, boolean dispose) {
        try {
            if (!t.getTextureData().isPrepared()) {
                t.getTextureData().prepare();
            }

            Pixmap re = t.getTextureData().consumePixmap();
            int x;
            int __;
            int[][] area;
            int startX;
            int startY;
            int initX;
            int i;
            int initY;
            int b;
            if (MathUtils.randomBoolean()) {
                x = 0;

                for(__ = MathUtils.random(11, 17); x < __; x += 1) {
                    area = new int[MathUtils.random(t.getWidth() / 8, t.getWidth() / 5)][MathUtils.random(t.getHeight() / 5, t.getHeight() / 3)];
                    startX = MathUtils.random(0, re.getWidth() - area.length);
                    startY = MathUtils.random(0, re.getHeight() - area[0].length);
                    initX = startX;

                    for(i = 0; i < area.length; i = (initX = initX + 1) - startX) {
                        initY = startY;

                        for(b = 0; b < area[0].length; b = (initY = initY + 1) - startY) {
                            area[i][b] = re.getPixel(initX, initY);
                        }
                    }

                    startY += MathUtils.random(t.getHeight() / 9, t.getHeight() / 5) * (MathUtils.randomBoolean() ? 1 : -1);
                    initX = startX;

                    for(i = 0; i < area.length; i = (initX = initX + 1) - startX) {
                        initY = startY;

                        for(b = 0; b < area[0].length; b = (initY = initY + 1) - startY) {
                            if (initY >= 0 && initY <= re.getHeight()) {
                                re.drawPixel(initX, initY, area[i][b]);
                            }
                        }
                    }
                }
            } else {
                x = 0;

                for(__ = MathUtils.random(11, 17); x < __; ++x) {
                    area = new int[MathUtils.random(t.getWidth() / 5, t.getWidth() / 3)][MathUtils.random(t.getHeight() / 8, t.getHeight() / 5)];
                    startX = MathUtils.random(0, re.getWidth() - area.length);
                    startY = MathUtils.random(0, re.getHeight() - area[0].length);
                    initX = startX;

                    for(i = 0; i < area.length; i = (initX = initX + 1) - startX) {
                        initY = startY;

                        for(b = 0; b < area[0].length; b = (initY = initY + 1) - startY) {
                            area[i][b] = re.getPixel(initX, initY);
                        }
                    }

                    startX += MathUtils.random(t.getWidth() / 9, t.getWidth() / 5) * (MathUtils.randomBoolean() ? 1 : -1);
                    initX = startX;

                    for(i = 0; i < area.length; i = (initX = initX + 1) - startX) {
                        initY = startY;

                        for(b = 0; b < area[0].length; b = (initY = initY + 1) - startY) {
                            if (initX >= 0 && initX <= re.getWidth()) {
                                re.drawPixel(initX, initY, area[i][b]);
                            }
                        }
                    }
                }
            }

            if (dispose) {
                t.dispose();
            }

            return new Texture(re);
        } catch (Exception var12) {
            return t;
        }
    }
}
