package mirthandmalice.cards.mirth.uncommon;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class ComboKick extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "ComboKick",
            1,
            CardType.ATTACK,
            CardTarget.ENEMY,
            CardRarity.UNCOMMON);
    // attack

    public static final String ID = makeID(cardInfo.cardName);


    private static final int DAMAGE = 4;
    private static final int UPG_DAMAGE = 1;

    private static final int ENERGY = 1;
    private static final int DRAW = 1;

    public ComboKick() {
        super(cardInfo, false);

        setDamage(DAMAGE, UPG_DAMAGE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        damageSingle(m, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
        damageSingle(m, AbstractGameAction.AttackEffect.BLUNT_HEAVY);

        if (p.hasPower(VigorPower.POWER_ID))
        {
            gainEnergy(ENERGY);
            drawCards(DRAW);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new ComboKick();
    }
}