package mirthandmalice.actions.character;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import mirthandmalice.actions.general.UpdateHandAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.interfaces.OnManifestPower;
import mirthandmalice.patch.manifestation.ManifestField;
import mirthandmalice.util.MultiplayerHelper;

import static mirthandmalice.MirthAndMaliceMod.FULL_DEBUG;
import static mirthandmalice.MirthAndMaliceMod.logger;

public class ManifestAction extends AbstractGameAction {
    private boolean toMirth;
    private boolean toOther;

    /*public ManifestAction(boolean toMirth)
    {
        this.toMirth = toMirth;
    }*/
    public ManifestAction(boolean toOther)
    {
        this.toOther = toOther;

        this.toMirth = MultiplayerHelper.getIsMirthFromOther(toOther);

        if (FULL_DEBUG)
        {
            logger.info("Manifest action: To " + (toMirth ? "Mirth" : "Malice"));
        }
    }

    @Override
    public void update() {
        if (AbstractDungeon.player instanceof MirthAndMalice && toMirth != ManifestField.mirthManifested.get(AbstractDungeon.player))
        {
            ManifestField.mirthManifested.set(AbstractDungeon.player, toMirth);

            CardCrawlGame.sound.playAV("ORB_DARK_CHANNEL", toMirth ? MathUtils.random(0.3f, 0.4f) : MathUtils.random(-0.3f, -0.2f),0.8f);

            for (AbstractPower p : AbstractDungeon.player.powers)
            {
                if (p instanceof OnManifestPower)
                {
                    ((OnManifestPower) p).onManifest(toOther);
                }
            }

            AbstractDungeon.actionManager.addToTop(new UpdateHandAction());
            AbstractDungeon.actionManager.addToTop(new WaitAction(0.3f));
        }
        this.isDone = true;
    }
}
