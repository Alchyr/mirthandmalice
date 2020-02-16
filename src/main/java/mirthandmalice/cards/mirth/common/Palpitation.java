package mirthandmalice.cards.mirth.common;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.powers.SealPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Palpitation extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Palpitation",
            1,
            AbstractCard.CardType.SKILL,
            CardTarget.SELF,
            AbstractCard.CardRarity.COMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int BLOCK = 6;
    private static final int UPG_BLOCK = 2;

    private static final int BUFF = 2;
    private static final int UPG_BUFF = 1;

    public Palpitation()
    {
        super(cardInfo, false);

        setBlock(BLOCK, UPG_BLOCK);
        setMagic(BUFF, UPG_BUFF);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        block();
        applySelf(new VigorPower(p, this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Palpitation();
    }
}