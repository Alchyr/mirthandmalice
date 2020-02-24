package mirthandmalice.cards.malice.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.powers.AtrophyPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Carve extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Carve",
            2,
            CardType.ATTACK,
            CardTarget.ENEMY,
            CardRarity.COMMON);
    // attack

    public static final String ID = makeID(cardInfo.cardName);


    private static final int DAMAGE = 10;
    private static final int UPG_DAMAGE = 2;

    private static final int MAGIC = 4;
    private static final int UPG_MAGIC = 1;

    public Carve() {
        super(cardInfo, false);

        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(MAGIC, UPG_MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        damageSingle(m, AbstractGameAction.AttackEffect.SLASH_HEAVY);
        applySingle(m, new AtrophyPower(p, m, this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Carve();
    }
}