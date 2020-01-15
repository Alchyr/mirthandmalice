package mirthandmalice.cards.malice.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.patch.card_use.LastCardType;
import mirthandmalice.patch.enums.CustomCardTags;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Intrigue extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Intrigue",
            1,
            AbstractCard.CardType.ATTACK,
            AbstractCard.CardTarget.ENEMY,
            AbstractCard.CardRarity.COMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int DAMAGE = 6;
    private static final int UPG_DAMAGE = 2;

    private static final int BONUS_DAMAGE = 4;
    private static final int UPG_BONUS_DAMAGE = 2;

    public Intrigue()
    {
        super(cardInfo, false);

        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(BONUS_DAMAGE, UPG_BONUS_DAMAGE);

        tags.add(CustomCardTags.MK_ECHO_ATTACK);
    }

    @Override
    public void applyPowers() {
        int originalDamage = this.baseDamage;

        this.baseDamage = this.baseMagicNumber;
        super.applyPowers();

        this.magicNumber = this.damage;
        this.isMagicNumberModified = this.isDamageModified;

        this.baseDamage = originalDamage;
        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int originalDamage = this.baseDamage;

        this.baseDamage = this.baseMagicNumber;
        super.calculateCardDamage(mo);

        this.magicNumber = this.damage;
        this.isMagicNumberModified = this.isDamageModified;

        this.baseDamage = originalDamage;
        super.calculateCardDamage(mo);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        if (LastCardType.type == AbstractCard.CardType.ATTACK)
        {
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.magicNumber, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Intrigue();
    }
}
