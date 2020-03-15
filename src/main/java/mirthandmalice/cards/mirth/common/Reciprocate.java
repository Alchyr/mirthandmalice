package mirthandmalice.cards.mirth.common;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.actions.cards.ReciprocateAction;
import mirthandmalice.powers.AtrophyPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Reciprocate extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Reciprocate",
            1,
            CardType.SKILL,
            CardTarget.ENEMY,
            CardRarity.COMMON);
    // skill

    public static final String ID = makeID(cardInfo.cardName);


    private static final int BLOCK = 5;
    private static final int UPG_BLOCK = 2;


    public Reciprocate() {
        super(cardInfo, false);

        setBlock(BLOCK, UPG_BLOCK);
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        this.rawDescription = cardStrings.DESCRIPTION;
        this.initializeDescription();
    }
    @Override
    public void onMoveToDiscard() {
        this.rawDescription = cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);

        if (mo != null)
        {
            this.baseMagicNumber = this.magicNumber = mo.currentBlock;

            this.baseMagicNumber += this.baseBlock;
            this.magicNumber += this.block;
            this.isMagicNumberModified = this.isBlockModified;

            this.rawDescription = cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0];
            this.initializeDescription();
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null)
        {
            giveBlock(m, this.block);
            addToBot(new ReciprocateAction(p, m));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Reciprocate();
    }
}