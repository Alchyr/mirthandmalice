package mirthandmalice.cards.malice.rare;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.powers.TraumaPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Trauma extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Trauma",
            3,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.ENEMY,
            AbstractCard.CardRarity.RARE
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int UPG_COST = 2;

    public Trauma()
    {
        super(cardInfo, false);

        setCostUpgrade(UPG_COST);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new TraumaPower(m), 1));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Trauma();
    }
}
