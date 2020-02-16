package mirthandmalice.cards.mirth.uncommon;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.actions.cards.BlockPerAttackPlayedAction;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Applaud extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Applaud",
            1,
            AbstractCard.CardType.SKILL,
            CardTarget.SELF,
            AbstractCard.CardRarity.UNCOMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int BLOCK = 3;
    private static final int UPG_BLOCK = 1;

    public Applaud()
    {
        super(cardInfo, false);

        setBlock(BLOCK, UPG_BLOCK);
    }

    public void applyPowers() {
        super.applyPowers();

        int count = 0;

        for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisTurn)
        {
            if (c.type == AbstractCard.CardType.ATTACK) {
                ++count;
            }
        }

        this.rawDescription = cardStrings.DESCRIPTION;
        this.rawDescription = this.rawDescription + cardStrings.EXTENDED_DESCRIPTION[0] + count;
        if (count == 1) {
            this.rawDescription = this.rawDescription + cardStrings.EXTENDED_DESCRIPTION[1];
        } else {
            this.rawDescription = this.rawDescription + cardStrings.EXTENDED_DESCRIPTION[2];
        }

        this.initializeDescription();
    }

    public void onMoveToDiscard() {
        this.rawDescription = cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new BlockPerAttackPlayedAction(p, this.block));
        this.rawDescription = cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public AbstractCard makeCopy() {
        return new Applaud();
    }
}