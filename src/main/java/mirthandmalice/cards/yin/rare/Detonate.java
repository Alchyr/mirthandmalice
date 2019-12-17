package mirthandmalice.cards.yin.rare;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.actions.general.IfKilledActionAction;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Detonate extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Detonate",
            2,
            AbstractCard.CardType.ATTACK,
            AbstractCard.CardTarget.ENEMY,
            AbstractCard.CardRarity.RARE
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int DAMAGE = 15;
    private static final int UPG_DAMAGE = 5;
    private static final int EXPLOSION_DAMAGE = 20;

    public Detonate()
    {
        super(cardInfo, false);

        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(EXPLOSION_DAMAGE);
    }

    @Override
    public void applyPowers() {
        int originalDamage = baseDamage;
        baseDamage = baseMagicNumber;
        super.applyPowers();
        magicNumber = damage;
        isMagicNumberModified = isDamageModified;
        baseDamage = originalDamage;
        super.applyPowers();
    }
    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null)
        {
            AbstractDungeon.actionManager.addToBottom(new VFXAction(new ExplosionSmallEffect(m.hb.cX + MathUtils.random(-35.0f * Settings.scale, 35.0f * Settings.scale), m.hb.cY + MathUtils.random(0, 20.0f * Settings.scale))));
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
            int[] dmg = DamageInfo.createDamageMatrix(magicNumber);
            AbstractDungeon.actionManager.addToBottom(new IfKilledActionAction(m, new DamageAllEnemiesAction(p, dmg, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.FIRE)));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Detonate();
    }
}