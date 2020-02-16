package mirthandmalice.cards.malice.uncommon;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.powers.ForgetPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Hemophilia extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Hemophilia",
            1,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.ENEMY,
            AbstractCard.CardRarity.UNCOMMON
    );
    //1 - Whenever the enemy loses hp this turn, apply 1 [2] Weak to it.
    public final static String ID = makeID(cardInfo.cardName);

    private static final int DEBUFF = 1;
    private static final int UPG_DEBUFF = 1;

    public Hemophilia()
    {
        super(cardInfo, false);

        setMagic(DEBUFF, UPG_DEBUFF);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new ForgetPower(m, p, this.magicNumber), this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Hemophilia();
    }
}