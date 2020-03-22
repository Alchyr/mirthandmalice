package mirthandmalice.cards.malice.uncommon;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Torture extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Torture",
            1,
            CardType.ATTACK,
            CardTarget.ENEMY,
            CardRarity.UNCOMMON);
    // attack

    public static final String ID = makeID(cardInfo.cardName);


    private static final int DAMAGE = 2;
    private static final int MAGIC = 4;
    private static final int UPG_MAGIC = 1;

    public Torture() {
        super(cardInfo, false);

        setDamage(DAMAGE);
        setMagic(MAGIC, UPG_MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < this.magicNumber; ++i)
            damageSingle(m, MathUtils.randomBoolean(0.33f) ? AbstractGameAction.AttackEffect.SLASH_VERTICAL : (MathUtils.randomBoolean() ? AbstractGameAction.AttackEffect.SLASH_DIAGONAL : AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Torture();
    }
}