package mirthandmalice.cards.mirth.rare;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.actions.cards.TrustAction;
import mirthandmalice.actions.general.PerformXAction;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Trust extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Trust",
            -1,
            CardType.SKILL,
            CardTarget.SELF,
            CardRarity.RARE);
    // skill

    public static final String ID = makeID(cardInfo.cardName);


    public Trust() {
        super(cardInfo, true);
    }

    @Override
    public void upgrade() {
        this.upgraded = false;
        super.upgrade();
    }

    @Override
    protected void upgradeName() {
        ++this.timesUpgraded;
        this.upgraded = true;
        this.name = cardStrings.NAME + "+" + this.timesUpgraded;
        this.initializeTitle();
    }

    public boolean canUpgrade() {
        return true;
    }

    @Override
    public void upgradeDescription() {
        super.upgradeDescription();
        this.rawDescription = this.rawDescription.replace("?", Integer.toString(this.timesUpgraded));
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        TrustAction action = new TrustAction(TrackCardSource.useMyEnergy, this.timesUpgraded);
        AbstractDungeon.actionManager.addToBottom(new PerformXAction(action, p, this.energyOnUse, this.freeToPlayOnce));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Trust();
    }
}