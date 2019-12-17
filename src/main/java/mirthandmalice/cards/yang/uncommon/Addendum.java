package mirthandmalice.cards.yang.uncommon;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.actions.character.SetEnergyGainAction;
import mirthandmalice.patch.card_use.LastCardType;
import mirthandmalice.patch.enums.CustomCardTags;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Addendum extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Addendum",
            2,
            AbstractCard.CardType.ATTACK,
            AbstractCard.CardTarget.ENEMY,
            AbstractCard.CardRarity.UNCOMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int DAMAGE = 7;
    private static final int UPG_DAMAGE = 3;

    private static final int DRAW = 1;

    public Addendum()
    {
        super(cardInfo, false);

        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(DRAW);

        tags.add(CustomCardTags.MK_ECHO_ATTACK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        if (LastCardType.type == AbstractCard.CardType.ATTACK)
        {
            AbstractDungeon.actionManager.addToBottom(new SetEnergyGainAction(false));
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(2));
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, this.magicNumber));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Addendum();
    }
}