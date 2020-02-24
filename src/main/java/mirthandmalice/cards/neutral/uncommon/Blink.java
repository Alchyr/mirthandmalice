package mirthandmalice.cards.neutral.uncommon;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.NeutralCard;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.patch.manifestation.ManifestField;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Blink extends NeutralCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Blink",
            0,
            CardType.SKILL,
            CardTarget.SELF,
            CardRarity.UNCOMMON);
    // skill

    public static final String ID = makeID(cardInfo.cardName);


    public Blink() {
        super(cardInfo, true);

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (TrackCardSource.useMyEnergy)
        {
            if (ManifestField.isManifested())
            {
                relinquish();
            }
            else
            {
                manifest();
            }
        }
        else
        {
            if (ManifestField.otherManifested())
            {
                relinquish();
            }
            else
            {
                manifest();
            }
        }

        if (upgraded)
            drawCards(1);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Blink();
    }
}