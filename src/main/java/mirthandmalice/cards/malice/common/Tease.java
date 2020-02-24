package mirthandmalice.cards.malice.common;

import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Tease extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Tease",
            0,
            CardType.SKILL,
            CardTarget.ENEMY,
            CardRarity.COMMON);

    public static final String ID = makeID(cardInfo.cardName);

    private static final int HP_LOSS = 4;

    private static final int MAGIC = 1;
    private static final int UPG_MAGIC = 1;

    public Tease() {
        super(cardInfo, false);

        setMagic(MAGIC, UPG_MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new LoseHPAction(m, p, HP_LOSS));
        applySingle(m, getVuln(m, this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Tease();
    }
}