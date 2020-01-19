package mirthandmalice.actions.character;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.actions.general.UpdateHandAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.manifestation.ManifestField;

public class ManifestAction extends AbstractGameAction {
    private boolean toMirth;

    public ManifestAction(boolean toMirth)
    {
        this.toMirth = toMirth;
    }

    @Override
    public void update() {
        if (AbstractDungeon.player instanceof MirthAndMalice && toMirth != ManifestField.mirthManifested.get(AbstractDungeon.player))
        {
            ManifestField.mirthManifested.set(AbstractDungeon.player, toMirth);

            CardCrawlGame.sound.playAV("ORB_DARK_CHANNEL", MathUtils.random(-0.15f, 0.15f),0.6f);

            AbstractDungeon.actionManager.addToTop(new UpdateHandAction());
            AbstractDungeon.actionManager.addToTop(new WaitAction(0.3f));
        }
        this.isDone = true;
    }
}
