package mirthandmalice.cards.neutral.rare;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.NeutralCard;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.powers.LullPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Lull extends NeutralCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Lull",
            2,
            CardType.SKILL,
            CardTarget.SELF,
            CardRarity.RARE);

    public final static String ID = makeID(cardInfo.cardName);

    private static final int BLOCK = 25;
    private static final int UPG_BLOCK = 7;


    public Lull() {
        super(cardInfo, false);

        setBlock(BLOCK, UPG_BLOCK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        block();
        applySelf(new LullPower(p, TrackCardSource.isPlayedByMirth()));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Lull();
    }
}