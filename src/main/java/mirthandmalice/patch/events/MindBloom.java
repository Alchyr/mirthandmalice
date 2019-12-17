/*package mirthandmalice.patch.events;

This patch is no longer necessary, as it was fixed.

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import javassist.CtBehavior;
import mirthandmalice.character.YinAndYang;

@SpirePatch(
        clz = com.megacrit.cardcrawl.events.beyond.MindBloom.class,
        method = "buttonEffect"
)
public class MindBloom {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void upgradeOtherDeck(com.megacrit.cardcrawl.events.beyond.MindBloom __instance, int buttonPressed)
    {
        if (AbstractDungeon.player instanceof YinAndYang)
        {
            int effectCount = 0;

            for (AbstractCard c : ((YinAndYang) AbstractDungeon.player).otherPlayerMasterDeck.group)
            {
                if (c.canUpgrade()) {
                    ++effectCount;
                    if (effectCount <= 10) {
                        float x = MathUtils.random(0.1F, 0.9F) * (float) Settings.WIDTH;
                        float y = MathUtils.random(0.2F, 0.8F) * (float)Settings.HEIGHT;
                        AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), x, y));
                        AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(x, y));
                    }
                    c.upgrade();
                }
            }
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "masterDeck");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
*/