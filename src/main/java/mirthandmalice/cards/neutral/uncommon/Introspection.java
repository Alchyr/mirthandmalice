package mirthandmalice.cards.neutral.uncommon;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.NeutralCard;
import mirthandmalice.actions.cards.IntrospectionAction;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Introspection extends NeutralCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Introspection",
            0,
            CardType.SKILL,
            CardTarget.NONE,
            CardRarity.UNCOMMON);
    // skill

    public static final String ID = makeID(cardInfo.cardName);


    private static final int MAGIC = 2;

    public Introspection() {
        super(cardInfo, true);

        setMagic(MAGIC);
        setExhaust(true, false);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new IntrospectionAction(TrackCardSource.useOtherEnergy, this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Introspection();
    }
}