package mirthandmalice.patch.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CtBehavior;
import mirthandmalice.util.MultiplayerHelper;

@SpirePatch(
        clz = TopPanel.class,
        method = "render"
)
public class RenderPing {
    @SpireInsertPatch(
            locator=Locator.class
    )
    public static void Insert(TopPanel __instance, SpriteBatch sb)
    {
        if (MultiplayerHelper.active)
        {
            FontHelper.renderFontRightTopAligned(
                    sb,
                    FontHelper.cardDescFont_N,
                    "Ping: " + MultiplayerHelper.lastPing,
                    Settings.WIDTH - 16.0F * Settings.scale,
                    Settings.HEIGHT - 152.0F * Settings.scale,
                    new Color(1, 1, 1, 0.35f)
            );
        }
    }
    @SpireInsertPatch(
            locator=AltLocator.class
    )
    public static void OtherInsert(TopPanel __instance, SpriteBatch sb)
    {
        if (MultiplayerHelper.active && !CardCrawlGame.displayVersion)
        {
            FontHelper.renderFontRightTopAligned(
                    sb,
                    FontHelper.cardDescFont_N,
                    "Ping: " + MultiplayerHelper.lastPing,
                    Settings.WIDTH - 16.0F * Settings.scale,
                    Settings.HEIGHT - 80.0F * Settings.scale,
                    new Color(1, 1, 1, 0.35f)
            );
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(SeedHelper.class, "getUserFacingSeedString");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    private static class AltLocator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "screen");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
