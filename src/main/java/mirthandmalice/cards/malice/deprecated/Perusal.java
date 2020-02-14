package mirthandmalice.cards.malice.deprecated;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.actions.character.MakeTempCardInOtherHandAction;
import mirthandmalice.cards.colorless.deprecated.Fragment;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.CardInfo;
import mirthandmalice.util.annotations.Disabled;

import static mirthandmalice.MirthAndMaliceMod.makeID;

@Disabled
public class Perusal extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Perusal",
            1,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.COMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int GENERATE = 1;
    private static final int UPG_GENERATE = 1;

    public Perusal()
    {
        super(cardInfo, true);
        setMagic(GENERATE, UPG_GENERATE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (TrackCardSource.useOtherEnergy && AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE)
        {
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInOtherHandAction(new Fragment(), this.magicNumber));
        }
        else
        {
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new Fragment(), this.magicNumber));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Perusal();
    }
}