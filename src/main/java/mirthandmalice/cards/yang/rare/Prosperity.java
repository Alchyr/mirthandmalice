package mirthandmalice.cards.yang.rare;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.actions.general.PlayCardAction;
import mirthandmalice.patch.card_use.LastCardType;
import mirthandmalice.patch.enums.CustomCardTags;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Prosperity extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Prosperity",
            2,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.RARE
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int BLOCK = 10;
    private static final int UPG_BLOCK = 4;

    public Prosperity()
    {
        super(cardInfo, false);

        setBlock(BLOCK, UPG_BLOCK);

        tags.add(CustomCardTags.MK_ECHO_POWER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
        if (LastCardType.type == AbstractCard.CardType.POWER && LastCardType.lastCardCopy != null)
        {
            AbstractDungeon.actionManager.addToBottom(new PlayCardAction(LastCardType.lastCardCopy.makeStatEquivalentCopy(), null, false));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Prosperity();
    }
}
