package mirthandmalice.cards.mirth.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Caprice extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Caprice",
            1,
            CardType.ATTACK,
            CardTarget.ENEMY,
            CardRarity.COMMON);

    public static final String ID = makeID(cardInfo.cardName);


    private static final int DAMAGE = 10;
    private static final int UPG_DAMAGE = 3;

    private static final int MAGIC = 6;

    public Caprice() {
        super(cardInfo, false);

        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        damageSingle(m, AbstractGameAction.AttackEffect.SHIELD);
        giveBlock(m, this.magicNumber);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Caprice();
    }
}