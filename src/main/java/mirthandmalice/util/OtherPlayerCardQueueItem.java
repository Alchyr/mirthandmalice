package mirthandmalice.util;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class OtherPlayerCardQueueItem extends CardQueueItem {
    public OtherPlayerCardQueueItem(AbstractCard card, AbstractMonster monster) {
        this(card, monster, EnergyPanel.getCurrentEnergy());
    }

    public OtherPlayerCardQueueItem(AbstractCard card, AbstractMonster monster, int setEnergyOnUse) {
        super(card, monster, setEnergyOnUse);
    }

    public OtherPlayerCardQueueItem(AbstractCard card, AbstractMonster monster, int setEnergyOnUse, boolean ignoreEnergyTotal) {
        super(card, monster, setEnergyOnUse, ignoreEnergyTotal, false);
    }

    public OtherPlayerCardQueueItem(AbstractCard card, AbstractMonster monster, int setEnergyOnUse, boolean ignoreEnergyTotal, boolean autoplayCard) {
        super(card, monster, setEnergyOnUse, ignoreEnergyTotal, autoplayCard);
    }

    public OtherPlayerCardQueueItem(AbstractCard card, boolean isEndTurnAutoPlay) {
        this(card, null);
        this.isEndTurnAutoPlay = isEndTurnAutoPlay;
    }
}
