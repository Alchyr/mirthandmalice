package mirthandmalice.cards.neutral.uncommon;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.NeutralCard;
import mirthandmalice.actions.cards.BalanceAction;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Balance extends NeutralCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Balance",
            1,
            CardType.SKILL,
            CardTarget.NONE,
            CardRarity.UNCOMMON);
    // skill

    public static final String ID = makeID(cardInfo.cardName);


    public Balance() {
        super(cardInfo, true);

        setExhaust(true, false);
    }

    @Override
    public void upgrade() {
        if (!upgraded)
        {
            this.name = cardStrings.EXTENDED_DESCRIPTION[0];
        }
        super.upgrade();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new BalanceAction(TrackCardSource.isPlayedByMirth(), this.upgraded));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Balance();
    }
}