package mirthandmalice.cards.malice.common;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.actions.cards.SpiteAction;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Spite extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Spite",
            1,
            CardType.SKILL,
            CardTarget.ENEMY,
            CardRarity.COMMON);
    // skill

    public static final String ID = makeID(cardInfo.cardName);


    private static final int MAGIC = 16;
    private static final int UPG_MAGIC = 4;

    public Spite() {
        super(cardInfo, false);

        setMagic(MAGIC, UPG_MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new SpiteAction(m, p, this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Spite();
    }
}