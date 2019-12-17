package mirthandmalice.cards.yin.rare;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.actions.character.SummonSparkAction;
import mirthandmalice.powers.SealPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Release extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Release",
            1,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.RARE
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int UPG_COST = 0;

    public Release()
    {
        super(cardInfo, false);

        setCostUpgrade(UPG_COST);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (p.hasPower(SealPower.POWER_ID))
        {
            int amt = p.getPower(SealPower.POWER_ID).amount;

            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(p, p, SealPower.POWER_ID));
            for (int i = 0; i < amt; ++i)
            {
                AbstractDungeon.actionManager.addToBottom(new SummonSparkAction());
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Release();
    }
}