package mirthandmalice.cards.malice.basic;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.actions.general.MarkCardAction;
import mirthandmalice.powers.AtrophyPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Wilt extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Wilt",
            0,
            CardType.SKILL,
            CardTarget.ENEMY,
            CardRarity.BASIC);
    // s

    public final static String ID = makeID(cardInfo.cardName);

    private static final int MAGIC = 3;
    private static final int UPG_MAGIC = 2;

    public Wilt() {
        super(cardInfo, false);

        setMagic(MAGIC, UPG_MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        applySingle(m, new AtrophyPower(p, m, this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Wilt();
    }
}