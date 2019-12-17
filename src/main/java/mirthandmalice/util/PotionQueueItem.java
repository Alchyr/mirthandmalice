package mirthandmalice.util;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PotionQueueItem {
    public int slot;
    public AbstractMonster target;

    public PotionQueueItem(int slot, AbstractMonster m)
    {
        this.slot = slot;
        this.target = m;
    }
}
