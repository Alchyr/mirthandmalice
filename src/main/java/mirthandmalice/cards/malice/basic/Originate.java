package mirthandmalice.cards.malice.basic;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.actions.cards.OriginateAction;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Originate extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Originate",
            0,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.BASIC
    );

    public final static String ID = makeID(cardInfo.cardName);

    public Originate()
    {
        super(cardInfo, true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new OriginateAction(upgraded));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Originate();
    }
}