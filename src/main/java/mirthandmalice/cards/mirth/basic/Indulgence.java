package mirthandmalice.cards.mirth.basic;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.actions.general.MarkCardAction;
import mirthandmalice.powers.ExcessPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Indulgence extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Indulgence",
            2,
            CardType.ATTACK,
            CardTarget.ENEMY,
            CardRarity.BASIC);
    // attack

    public final static String ID = makeID(cardInfo.cardName);

    private static final int DAMAGE = 6;
    private static final int UPG_DAMAGE = 2;

    private static final int MAGIC = 6;
    private static final int UPG_MAGIC = 2;

    public Indulgence() {
        super(cardInfo, false);

        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(MAGIC, UPG_MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        damageSingle(m, AbstractGameAction.AttackEffect.POISON);
        applySingle(m, new ExcessPower(p, m, this.magicNumber));
        relinquish();
    }

    @Override
    public AbstractCard makeCopy() {
        return new Indulgence();
    }
}