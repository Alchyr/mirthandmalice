package mirthandmalice.cards.mirth.deprecated;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.actions.character.SummonSparkAction;
import mirthandmalice.util.CardInfo;
import mirthandmalice.util.annotations.Disabled;

import static mirthandmalice.MirthAndMaliceMod.makeID;

@Disabled
public class Smokescreen extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Smokescreen",
            2,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.COMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int BLOCK = 9;
    private static final int UPG_BLOCK = 4;

    public Smokescreen()
    {
        super(cardInfo, false);

        setBlock(BLOCK, UPG_BLOCK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
        AbstractDungeon.actionManager.addToBottom(new SummonSparkAction());
    }

    @Override
    public AbstractCard makeCopy() {
        return new Smokescreen();
    }
}