package mirthandmalice.cards.neutral.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.NeutralCard;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Flicker extends NeutralCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Flicker",
            1,
            CardType.ATTACK,
            CardTarget.ALL_ENEMY,
            CardRarity.COMMON);

    public static final String ID = makeID(cardInfo.cardName);


    private static final int DAMAGE = 6;
    private static final int UPG_DAMAGE = 3;


    public Flicker() {
        super(cardInfo, false);

        setDamage(DAMAGE, UPG_DAMAGE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        damageAll(AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
        relinquish();
    }

    @Override
    public AbstractCard makeCopy() {
        return new Flicker();
    }
}