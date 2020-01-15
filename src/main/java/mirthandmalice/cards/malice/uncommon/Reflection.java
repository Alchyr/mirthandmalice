package mirthandmalice.cards.malice.uncommon;

import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.actions.character.OtherPlayerDiscardAction;
import mirthandmalice.actions.character.ResetEnergyGainAction;
import mirthandmalice.actions.character.SetEnergyGainAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Reflection extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Reflection",
            2,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.UNCOMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int UPG_COST = 1;

    public Reflection()
    {
        super(cardInfo, false);

        setCostUpgrade(UPG_COST);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int amt = p.hand.size();
        if (p instanceof MirthAndMalice)
        {
            if (TrackCardSource.useOtherEnergy)
            {
                amt = ((MirthAndMalice) p).otherPlayerHand.size();
                AbstractDungeon.actionManager.addToBottom(new OtherPlayerDiscardAction((MirthAndMalice)p, p, amt, false));
            }
            else
            {
                AbstractDungeon.actionManager.addToBottom(new DiscardAction(p, p, amt, false));
            }
            AbstractDungeon.actionManager.addToBottom(new SetEnergyGainAction(true));
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(amt));
            AbstractDungeon.actionManager.addToBottom(new ResetEnergyGainAction());
        }
        else
        {
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(amt));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Reflection();
    }
}