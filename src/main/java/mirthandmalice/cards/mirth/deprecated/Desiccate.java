package mirthandmalice.cards.mirth.deprecated;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.patch.combat.BurstActive;
import mirthandmalice.util.CardInfo;
import mirthandmalice.util.annotations.Disabled;

import static mirthandmalice.MirthAndMaliceMod.makeID;

@Disabled
public class Desiccate extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Desiccate",
            2,
            AbstractCard.CardType.ATTACK,
            AbstractCard.CardTarget.ENEMY,
            AbstractCard.CardRarity.RARE
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int DAMAGE = 12;
    private static final int UPG_DAMAGE = 2;

    private static final int DEBUFF = 1;
    private static final int UPG_DEBUFF = 1;

    public Desiccate()
    {
        super(cardInfo, false);

        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(DEBUFF, UPG_DEBUFF);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));

        if (m != null)
        {
            if (BurstActive.active.get(m))
            {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new StrengthPower(m, -this.magicNumber), -this.magicNumber, AbstractGameAction.AttackEffect.SLASH_VERTICAL));
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Desiccate();
    }
}
