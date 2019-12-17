package mirthandmalice.cards.yang.uncommon;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.actions.cards.TamperingAction;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Tampering extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Tampering",
            0,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.UNCOMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int UPG = 2;
    private static final int UPG_UPG = 1;

    public Tampering()
    {
        super(cardInfo, false);

        setMagic(UPG, UPG_UPG);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new TamperingAction(this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Tampering();
    }
}