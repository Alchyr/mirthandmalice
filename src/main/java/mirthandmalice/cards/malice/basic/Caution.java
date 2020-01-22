package mirthandmalice.cards.malice.basic;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.actions.character.ManifestAction;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Caution extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Caution",
            2,
            CardType.SKILL,
            CardTarget.SELF,
            CardRarity.BASIC);
    // s

    public final static String ID = makeID(cardInfo.cardName);

    private static final int BLOCK = 10;
    private static final int UPG_BLOCK = 4;


    public Caution() {
        super(cardInfo, false);

        setBlock(BLOCK, UPG_BLOCK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        block();
        fade();
    }

    @Override
    public AbstractCard makeCopy() {
        return new Caution();
    }
}