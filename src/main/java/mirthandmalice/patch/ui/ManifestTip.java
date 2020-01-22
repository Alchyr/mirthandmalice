package mirthandmalice.patch.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import mirthandmalice.character.MirthAndMalice;

import java.util.ArrayList;

@SpirePatch(
        clz = TipHelper.class,
        method = "renderKeywords"
)
public class ManifestTip {
    public static String manifestKeyword = "manifest";

    @SpireInsertPatch(
            rloc = 0
    )
    public static void addManifestTip(float x, float y, SpriteBatch sb, ArrayList<String> keywords, AbstractCard ___card)
    {
        if (___card != null && ___card.type == AbstractCard.CardType.ATTACK && AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (!keywords.contains(manifestKeyword))
                keywords.add(0, manifestKeyword);
        }
    }
}
