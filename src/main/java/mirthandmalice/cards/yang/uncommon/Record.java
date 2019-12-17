package mirthandmalice.cards.yang.uncommon;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.powers.RecordPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Record extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Record",
            0,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.UNCOMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int COPY = 2;
    private static final int UPG_COPY = 1;

    public Record()
    {
        super(cardInfo, false);

        setMagic(COPY, UPG_COPY);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new RecordPower(p, this.magicNumber, TrackCardSource.useOtherEnergy), this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Record();
    }
}