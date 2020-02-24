package mirthandmalice.cards.malice.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.combat.CardPoofEffect;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.actions.general.DamageRandomConditionalEnemyAction;
import mirthandmalice.util.CardInfo;
import mirthandmalice.util.annotations.Disabled;

import static mirthandmalice.MirthAndMaliceMod.makeID;

@Disabled
public class Misdirect extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Misdirect",
            1,
            AbstractCard.CardType.ATTACK,
            AbstractCard.CardTarget.ENEMY,
            AbstractCard.CardRarity.COMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int DAMAGE = 7;
    private static final int UPG_DAMAGE = 2;

    private static final int DEBUFF = 1;
    private static final int UPG_DEBUFF = 1;

    public Misdirect()
    {
        super(cardInfo, false);

        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(DEBUFF, UPG_DEBUFF);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null)
        {
            AbstractDungeon.actionManager.addToBottom(new VFXAction(new CardPoofEffect(m.hb.cX, m.hb.cY)));
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new VulnerablePower(m, this.magicNumber, false), this.magicNumber));
        }
        AbstractDungeon.actionManager.addToBottom(new DamageRandomConditionalEnemyAction((monster)->!monster.equals(m), new DamageInfo(p, this.baseDamage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Misdirect();
    }
}