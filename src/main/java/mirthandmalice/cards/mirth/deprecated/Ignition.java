package mirthandmalice.cards.mirth.deprecated;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.powers.IgnitionPower;
import mirthandmalice.util.CardInfo;
import mirthandmalice.util.annotations.Disabled;

import static mirthandmalice.MirthAndMaliceMod.makeID;

@Disabled
public class Ignition extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Ignition",
            2,
            AbstractCard.CardType.POWER,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.RARE
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int UPG_COST = 1;

    public Ignition()
    {
        super(cardInfo, false);
        setCostUpgrade(UPG_COST);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new IgnitionPower(p, 1), 1));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Ignition();
    }
}