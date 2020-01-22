package mirthandmalice.patch.ui;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.CardGlowBorder;
import mirthandmalice.patch.card_use.LastCardType;
import mirthandmalice.patch.combat.BurstActive;
import mirthandmalice.patch.enums.CustomCardTags;
import mirthandmalice.patch.manifestation.ManifestField;

import java.lang.reflect.Field;

import static mirthandmalice.MirthAndMaliceMod.logger;

@SpirePatch(
        clz = CardGlowBorder.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = { AbstractCard.class, Color.class }
)
public class CardGlow {
    private static Field hoveredMonsterField;
    private static Field colorField;

    static {
        try {
            hoveredMonsterField = AbstractPlayer.class.getDeclaredField("hoveredMonster");
            hoveredMonsterField.setAccessible(true);

            colorField = AbstractGameEffect.class.getDeclaredField("color");
            colorField.setAccessible(true);
        }
        catch (Exception e) {
            logger.error("Failed to initialize a Field for color patch.", e);
        }
    }

    @SpirePostfixPatch
    public static void PostFix(CardGlowBorder __instance, AbstractCard c) throws IllegalAccessException {
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            if (c.hasTag(CustomCardTags.MK_ECHO_ATTACK) && LastCardType.type == AbstractCard.CardType.ATTACK) {
                colorField.set(__instance, Color.RED.cpy());
            }
            if (c.hasTag(CustomCardTags.MK_ECHO_SKILL) && LastCardType.type == AbstractCard.CardType.SKILL) {
                colorField.set(__instance, Color.GREEN.cpy());
            }
            if (c.hasTag(CustomCardTags.MK_ECHO_POWER) && LastCardType.type == AbstractCard.CardType.POWER) {
                colorField.set(__instance, Color.GOLD.cpy());
            }

            if ((c.target == AbstractCard.CardTarget.ENEMY || c.target == AbstractCard.CardTarget.SELF_AND_ENEMY) &&
                    c.equals(AbstractDungeon.player.hoveredCard) && //avoid possibility of null hovered card error
                    c.hasTag(CustomCardTags.MK_BURST))
            {
                try {
                    AbstractMonster target = (AbstractMonster) hoveredMonsterField.get(AbstractDungeon.player);
                    if (target != null)
                    {
                        if (BurstActive.active.get(target))
                        {
                            colorField.set(__instance, Color.RED.cpy());
                        }
                    }
                    else
                    {
                        int amt = 0;
                        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters)
                        {
                            if (!m.isDeadOrEscaped())
                            {
                                target = m;
                                ++amt;
                            }
                        }

                        if (amt == 1)
                        {
                            if (BurstActive.active.get(target))
                            {
                                colorField.set(__instance, Color.RED.cpy());
                            }
                        }
                    }
                }
                catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

            if (!ManifestField.isManifested())
            {
                Color color = (Color) colorField.get(__instance);

                color.r = (color.r + 0.8f) / 2.0f;
                color.g *= 0.3f;
                color.b *= 0.3f;
            }
        }
    }
}
