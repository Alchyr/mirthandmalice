package mirthandmalice.cards.malice.rare;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Crush extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Crush",
            1,
            CardType.ATTACK,
            CardTarget.ENEMY,
            CardRarity.RARE);
    // attack

    public static final String ID = makeID(cardInfo.cardName);


    private static final int DAMAGE = 8;

    private boolean blockTrigger;

    public Crush() {
        super(cardInfo, true);

        setDamage(DAMAGE);

        blockTrigger = false;
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        blockTrigger = false;
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int origBase = baseDamage; //Making it work properly with other things that modify damage is annoying :(

        this.baseDamage += mo.currentBlock * (upgraded ? 2 : 1);

        blockTrigger = mo.currentBlock > 0;

        super.calculateCardDamage(mo);

        this.baseDamage = origBase;

        this.isDamageModified = this.damage != this.baseDamage;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        damageSingle(m, blockTrigger ? AbstractGameAction.AttackEffect.SMASH : AbstractGameAction.AttackEffect.BLUNT_HEAVY);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Crush();
    }
}