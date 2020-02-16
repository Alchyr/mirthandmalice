package mirthandmalice.cards.malice.deprecated;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.powers.ChroniclePower;
import mirthandmalice.util.CardInfo;
import mirthandmalice.util.annotations.Disabled;

import static mirthandmalice.MirthAndMaliceMod.makeID;

@Disabled
public class Chronicle extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Chronicle",
            3,
            AbstractCard.CardType.POWER,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.RARE
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int UPG_COST = 2;
    private static final int BUFF = 1;

    public Chronicle()
    {
        super(cardInfo, false);
        setCostUpgrade(UPG_COST);
        setMagic(BUFF);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new ChroniclePower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Chronicle();
    }
}