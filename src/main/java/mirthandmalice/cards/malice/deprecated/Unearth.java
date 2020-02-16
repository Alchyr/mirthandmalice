package mirthandmalice.cards.malice.deprecated;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.actions.cards.UnearthAction;
import mirthandmalice.actions.character.OtherPlayerDeckShuffleAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.CardInfo;
import mirthandmalice.util.annotations.Disabled;

import static mirthandmalice.MirthAndMaliceMod.makeID;

@Disabled
public class Unearth extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Unearth",
            0,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.UNCOMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int ENERGY = 1;
    private static final int UPG_ENERGY = 1;

    public Unearth()
    {
        super(cardInfo, false);

        setMagic(ENERGY, UPG_ENERGY);

        this.rawDescription = cardStrings.DESCRIPTION;
        for (int i = 0; i < this.magicNumber; i++)
        {
            this.rawDescription = this.rawDescription.concat(cardStrings.EXTENDED_DESCRIPTION[0]);
        }
        this.rawDescription = this.rawDescription.concat(cardStrings.EXTENDED_DESCRIPTION[1]);
        initializeDescription();
    }

    @Override
    public void upgrade() {
        super.upgrade();

        this.rawDescription = cardStrings.DESCRIPTION;
        for (int i = 0; i < this.magicNumber; i++)
        {
            this.rawDescription = this.rawDescription.concat(cardStrings.EXTENDED_DESCRIPTION[0]);
        }
        this.rawDescription = this.rawDescription.concat(cardStrings.EXTENDED_DESCRIPTION[1]);
        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        boolean other = TrackCardSource.useOtherEnergy && AbstractDungeon.player instanceof MirthAndMalice;
        if (other)
        {
            if (((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.isEmpty()) {// 41
                AbstractDungeon.actionManager.addToBottom(new OtherPlayerDeckShuffleAction());// 42
            }
        }
        else
        {
            if (AbstractDungeon.player.drawPile.isEmpty()) {
                AbstractDungeon.actionManager.addToBottom(new EmptyDeckShuffleAction());
            }
        }

        AbstractDungeon.actionManager.addToBottom(new UnearthAction(this.magicNumber, other));
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 1));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Unearth();
    }
}