package mirthandmalice.cards.yang.uncommon;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.powers.ForgetPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Forget extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Forget",
            1,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.ENEMY,
            AbstractCard.CardRarity.UNCOMMON
    );
    //1 [0] - Whenever the enemy takes damage this turn, apply 1 Weak to it.
    public final static String ID = makeID(cardInfo.cardName);

    private static final int UPG_COST = 0;
    private static final int DEBUFF = 1;

    public Forget()
    {
        super(cardInfo, false);

        setCostUpgrade(UPG_COST);
        setMagic(DEBUFF);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new ForgetPower(m, p, this.magicNumber), this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Forget();
    }
}