package mirthandmalice.cards.neutral.common;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.NeutralCard;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Mercurial extends NeutralCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Mercurial",
            1,
            CardType.ATTACK,
            CardTarget.NONE,
            CardRarity.COMMON);
    // attack

    public final static String ID = makeID(cardInfo.cardName);

    private static final int DAMAGE = 8;
    private static final int UPG_DAMAGE = 2;


    public Mercurial() {
        super(cardInfo, false);

        setDamage(DAMAGE, UPG_DAMAGE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // :)
    }

    @Override
    public AbstractCard makeCopy() {
        return new Mercurial();
    }
}