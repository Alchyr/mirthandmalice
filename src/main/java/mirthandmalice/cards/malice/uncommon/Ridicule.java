package mirthandmalice.cards.malice.uncommon;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Ridicule extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Ridicule",
            1,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.ENEMY,
            AbstractCard.CardRarity.UNCOMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int DEBUFF = 1;
    private static final int UPG_DEBUFF = 1;

    public Ridicule()
    {
        super(cardInfo, false);

        setMagic(DEBUFF, UPG_DEBUFF);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters)
        {
            if (!mo.isDeadOrEscaped())
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new VulnerablePower(m, this.magicNumber, false), this.magicNumber, true));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Ridicule();
    }
}