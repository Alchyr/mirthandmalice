package mirthandmalice.cards.mirth.uncommon;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.actions.general.ImageAboveCreatureAction;
import mirthandmalice.actions.general.MarkCardsInHandAction;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class ColdComfort extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "ColdComfort",
            2,
            CardType.SKILL,
            CardTarget.ALL_ENEMY,
            CardRarity.UNCOMMON);

    public static final String ID = makeID(cardInfo.cardName);

    private static final int MAGIC = 10;

    public ColdComfort() {
        super(cardInfo, true);

        setMagic(MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters)
        {
            if (!mo.isDeadOrEscaped())
            {
                AbstractDungeon.actionManager.addToTop(new GainBlockAction(mo, AbstractDungeon.player, this.magicNumber, true));
            }
        }

        addToBot(new MarkCardsInHandAction(false, TrackCardSource.useOtherEnergy, upgraded ? (c)->true : (c)->c.type == CardType.ATTACK));
    }

    @Override
    public AbstractCard makeCopy() {
        return new ColdComfort();
    }
}