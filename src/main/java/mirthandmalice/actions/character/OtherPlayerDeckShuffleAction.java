package mirthandmalice.actions.character;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.combat.SoulAltShuffle;

import java.util.Iterator;

public class OtherPlayerDeckShuffleAction extends AbstractGameAction {
    private boolean shuffled = false;
    private boolean vfxDone = false;
    private int count = 0;

    public OtherPlayerDeckShuffleAction() {
        this.actionType = ActionType.SHUFFLE;

        for (AbstractRelic r : AbstractDungeon.player.relics)
        {
            r.onShuffle();
        }
    }

    public void update() {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            MirthAndMalice player = (MirthAndMalice)AbstractDungeon.player;

            if (!this.shuffled) {
                this.shuffled = true;
                player.otherPlayerDiscard.shuffle(AbstractDungeon.shuffleRng);
            }

            if (!this.vfxDone) {
                Iterator<AbstractCard> c = player.otherPlayerDiscard.group.iterator();
                if (c.hasNext()) {
                    ++this.count;
                    AbstractCard e = c.next();
                    c.remove();
                    SoulAltShuffle.altGroup = player.otherPlayerDraw;
                    if (this.count < 11) {
                        AbstractDungeon.getCurrRoom().souls.shuffle(e, false);
                    } else {
                        AbstractDungeon.getCurrRoom().souls.shuffle(e, true);
                    }

                    return;
                }

                this.vfxDone = true;
            }
        }
        this.isDone = true;
    }
}