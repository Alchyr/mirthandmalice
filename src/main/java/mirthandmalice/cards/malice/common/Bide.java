package mirthandmalice.cards.malice.common;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.actions.character.MakeTempCardInOtherDrawAction;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Bide extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Bide",
            1,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.COMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int BLOCK = 7;
    private static final int UPG_BLOCK = 2;

    public Bide()
    {
        super(cardInfo, false);

        setBlock(BLOCK, UPG_BLOCK);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
        if (TrackCardSource.useOtherEnergy && AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE)
        {
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInOtherDrawAction(this.makeStatEquivalentCopy(), 1, true, true));
        }
        else
        {
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(this.makeStatEquivalentCopy(), 1, true, true));
        }
    }

    @Override
    public AbstractCard makeCopy() {
            return new Bide();
        }
}