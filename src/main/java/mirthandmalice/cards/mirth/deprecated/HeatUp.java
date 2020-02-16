package mirthandmalice.cards.mirth.deprecated;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.util.CardInfo;
import mirthandmalice.util.annotations.Disabled;

import static mirthandmalice.MirthAndMaliceMod.makeID;

@Disabled
public class HeatUp extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "HeatUp",
            0,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.UNCOMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int COST = 3;

    private static final int BUFF = 2;
    private static final int UPG_BUFF = 1;

    public HeatUp()
    {
        super(cardInfo, false);

        setMagic(BUFF, UPG_BUFF);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new LoseHPAction(p, p, COST));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new HeatUp();
    }
}