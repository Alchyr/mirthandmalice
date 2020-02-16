package mirthandmalice.cards.mirth.rare;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.powers.SealPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Invigorate extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Invigorate",
            0,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.RARE
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int EFFECT = 6;
    private static final int UPG_EFFECT = 4;

    public Invigorate()
    {
        super(cardInfo, false);

        setMagic(EFFECT, UPG_EFFECT);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        drawCards(2);
        applySelf(new VigorPower(p, this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Invigorate();
    }
}