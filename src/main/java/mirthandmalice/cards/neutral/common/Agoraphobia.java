package mirthandmalice.cards.neutral.common;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.NeutralCard;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.powers.AgoraphobiaPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Agoraphobia extends NeutralCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Agoraphobia",
            0,
            CardType.SKILL,
            CardTarget.SELF,
            CardRarity.COMMON);
    // skill

    public static final String ID = makeID(cardInfo.cardName);


    private static final int MAGIC = 4;
    private static final int UPG_MAGIC = 2;

    public Agoraphobia() {
        super(cardInfo, false);

        setMagic(MAGIC, UPG_MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        applySelf(new AgoraphobiaPower(p, TrackCardSource.useOtherEnergy, this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Agoraphobia();
    }
}