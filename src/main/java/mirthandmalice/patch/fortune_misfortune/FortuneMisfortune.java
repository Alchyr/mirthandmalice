package mirthandmalice.patch.fortune_misfortune;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import mirthandmalice.actions.general.AllEnemyLoseHPAction;
import mirthandmalice.actions.general.ImageAboveCreatureAction;
import mirthandmalice.interfaces.OnFortunePower;
import mirthandmalice.interfaces.OnMisfortunePower;
import mirthandmalice.util.TextureLoader;
import org.clapper.util.classutil.*;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static mirthandmalice.MirthAndMaliceMod.assetPath;
import static mirthandmalice.MirthAndMaliceMod.logger;

public class FortuneMisfortune {
    private static Texture FORTUNE_TEXTURE = TextureLoader.getTexture(assetPath("img/ui/fortune.png"));
    private static Texture MISFORTUNE_TEXTURE = TextureLoader.getTexture(assetPath("img/ui/misfortune.png"));

    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CLASS
    )
    public static class Fields {
        public static SpireField<Integer> fortune = new SpireField<>(()->0);
        public static SpireField<Integer> misfortune = new SpireField<>(()->0);
    }

    @SpirePatch(
            clz=AbstractCard.class,
            method="renderImage"
    )
    public static class CardRendering
    {
        private static final float SPACING = 35.0f * Settings.scale;
        private static final int SIZE = 30;
        private static final float CENTER = SIZE / 2.0f;

        private static final int PER_LINE = 5;

        private static Field renderColor;

        static
        {
            try {
                renderColor = AbstractCard.class.getDeclaredField("renderColor");
                renderColor.setAccessible(true);
            } catch (NoSuchFieldException e) {
                logger.error("Failed to get field renderColor in AbstractCard.", e);
            }
        }

        public static void Prefix(AbstractCard __instance, SpriteBatch sb, boolean hovered, boolean selected) throws IllegalAccessException
        {
            if (Fields.fortune.get(__instance) == 0 && Fields.misfortune.get(__instance) == 0) {
                return;
            }

            Color oldColor = sb.getColor();
            sb.setColor((Color) renderColor.get(__instance));

            int val = Fields.fortune.get(__instance);
            int i = 0;

            if (val > 0)
            {
                for (i = 0; i < val; ++i) {
                    Vector2 offset = new Vector2(-120 + (i % PER_LINE) * SPACING, 190 + (i / PER_LINE) * 30.0f);
                    offset.rotate(__instance.angle);
                    offset.scl(__instance.drawScale * Settings.scale);
                    sb.draw(
                            FORTUNE_TEXTURE,
                            __instance.current_x + offset.x,
                            __instance.current_y + offset.y,
                            CENTER,
                            CENTER,
                            SIZE,
                            SIZE,
                            __instance.drawScale * Settings.scale,
                            __instance.drawScale * Settings.scale,
                            __instance.angle,
                            0,
                            0,
                            SIZE,
                            SIZE,
                            false,
                            false
                    );
                }
            }

            val = Fields.misfortune.get(__instance);
            if (val > 0)
            {
                for (val += i; i < val; ++i) {
                    Vector2 offset = new Vector2(-100 + 18.0f * __instance.drawScale + (i % PER_LINE) * SPACING, 200 +14.0f * __instance.drawScale + (i / PER_LINE) * SPACING);
                    offset.rotate(__instance.angle);
                    offset.scl(__instance.drawScale * Settings.scale);
                    sb.draw(
                            MISFORTUNE_TEXTURE,
                            __instance.current_x + offset.x,
                            __instance.current_y + offset.y,
                            CENTER,
                            CENTER,
                            SIZE,
                            SIZE,
                            __instance.drawScale * Settings.scale,
                            __instance.drawScale * Settings.scale,
                            __instance.angle,
                            0,
                            0,
                            SIZE,
                            SIZE,
                            false,
                            false
                    );
                }
            }

            sb.setColor(oldColor);
        }
    }


    @SpirePatch(
            clz = CardCrawlGame.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class AutoOnDrawPatch
    {
        public static void Raw(CtBehavior ctBehavior) throws NotFoundException {
            logger.info("Starting Fortune/Misfortune automatic patch:");

            ClassFinder finder = new ClassFinder();

            finder.add(new File(Loader.STS_JAR));

            for (ModInfo modInfo : Loader.MODINFOS) {
                if (modInfo.jarURL != null) {
                    try {
                        finder.add(new File(modInfo.jarURL.toURI()));
                    } catch (URISyntaxException e) {
                        // do nothing
                    }
                }
            }

            // Get all classes, triggerOnDraw should only be called in AbstractPlayer (and maybe some overriding player classes like my own.)
            ClassFilter filter = new AndClassFilter(
                    new NotClassFilter(new InterfaceOnlyClassFilter()),
                    new ClassModifiersClassFilter(Modifier.PUBLIC),
                    new OrClassFilter(
                            new org.clapper.util.classutil.SubclassClassFilter(AbstractPlayer.class),
                            (classInfo, classFinder) -> classInfo.getClassName().equals(AbstractPlayer.class.getName())
                    )
                );

            ArrayList<ClassInfo> foundClasses = new ArrayList<>();
            finder.findClasses(foundClasses, filter);

            logger.info("\t- Done Finding Classes.\n\t- Patching:");

            for (ClassInfo classInfo : foundClasses) {
                CtClass ctClass = ctBehavior.getDeclaringClass().getClassPool().get(classInfo.getClassName());

                logger.info("\t\t- Patching Class: " + ctClass.getSimpleName());
                try {
                    ctClass.instrument(new TriggerDraw());
                    logger.info("\n\t\t\tSuccess.\n");
                } catch(CannotCompileException e) {
                    logger.error("\n\t\t\tFailure.\n");
                    e.printStackTrace();
                }
            }
            logger.info("\t- Done Patching.");
        }

        private static class TriggerDraw extends ExprEditor {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m == null)
                    return;

                if(m.getMethodName().equals("triggerWhenDrawn")) {
                    logger.info("\t\t\t- Modifying Method Call: " + m.getMethodName());
                    m.replace("{" +
                            "mirthandmalice.patch.fortune_misfortune.FortuneMisfortune.onDraw($0);" +
                            "$proceed($$);" +
                            "}");
                }
            }
        }
    }

    private static final int FORTUNE_BLOCK = 3;
    private static final int MISFORTUNE_LOSS = 5;

    public static void onDraw(AbstractCard c)
    {
        int val = Fields.fortune.get(c);

        if (val > 0)
        {
            int fortuneAmount = FORTUNE_BLOCK;

            for (AbstractPower p : AbstractDungeon.player.powers)
            {
                if (p instanceof OnFortunePower)
                {
                    fortuneAmount = ((OnFortunePower) p).onFortune(c);
                }
            }

            for (AbstractMonster m : AbstractDungeon.getMonsters().monsters)
            {
                if (!m.isDeadOrEscaped())
                {
                    AbstractDungeon.actionManager.addToTop(new GainBlockAction(m, AbstractDungeon.player, fortuneAmount * val, true));
                    AbstractDungeon.actionManager.addToTop(new ImageAboveCreatureAction(m, FORTUNE_TEXTURE));
                }
            }
            AbstractDungeon.actionManager.addToTop(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, fortuneAmount, true));
            AbstractDungeon.actionManager.addToTop(new ImageAboveCreatureAction(AbstractDungeon.player, FORTUNE_TEXTURE));


            Fields.fortune.set(c, 0);
        }

        val = Fields.misfortune.get(c);
        if (val > 0)
        {
            int misfortuneAmount = MISFORTUNE_LOSS;

            for (AbstractPower p : AbstractDungeon.player.powers)
            {
                if (p instanceof OnMisfortunePower)
                {
                    misfortuneAmount = ((OnMisfortunePower) p).onMisfortune(c);
                }
            }

            AbstractDungeon.actionManager.addToTop(new AllEnemyLoseHPAction(AbstractDungeon.player, misfortuneAmount * val, AbstractGameAction.AttackEffect.FIRE));
            for (AbstractMonster m : AbstractDungeon.getMonsters().monsters)
            {
                if (!m.isDeadOrEscaped() && !m.halfDead)
                {
                    AbstractDungeon.actionManager.addToTop(new ImageAboveCreatureAction(m, MISFORTUNE_TEXTURE));
                }
            }

            Fields.misfortune.set(c, 0);
        }
    }
}
