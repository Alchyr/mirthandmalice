package mirthandmalice.cards.mirth.common;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.actions.character.DontUseSpecificEnergyAction;
import mirthandmalice.actions.character.ForceDrawAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class InnerConnection extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "InnerConnection",
            1,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.COMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int BLOCK = 5;
    private static final int UPG_BLOCK = 3;

    public InnerConnection()
    {
        super(cardInfo, false);

        setBlock(BLOCK, UPG_BLOCK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            AbstractDungeon.actionManager.addToBottom(new ForceDrawAction(TrackCardSource.useOtherEnergy, 1));
            AbstractDungeon.actionManager.addToBottom(new ForceDrawAction(!TrackCardSource.useOtherEnergy, 1));
        }
        else
        {
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, 1));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new InnerConnection();
    }
}