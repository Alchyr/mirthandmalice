package mirthandmalice.cards.malice.uncommon;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.effects.BleedEffect;
import mirthandmalice.patch.combat.BurstActive;
import mirthandmalice.patch.enums.CustomCardTags;
import mirthandmalice.util.CardInfo;
import mirthandmalice.util.annotations.Disabled;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Desiccate extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Desiccate",
            3,
            AbstractCard.CardType.ATTACK,
            AbstractCard.CardTarget.ENEMY,
            CardRarity.UNCOMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int DAMAGE = 8;
    private static final int UPG_DAMAGE = 2;

    private static final int HP_LOSS = 16;
    private static final int UPG_LOSS = 4;

    public Desiccate()
    {
        super(cardInfo, false);

        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(HP_LOSS, UPG_LOSS);

        this.tags.add(CustomCardTags.MM_BURST);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        damageSingle(m, AbstractGameAction.AttackEffect.FIRE);

        if (m != null)
        {
            if (BurstActive.active.get(m))
            {
                addToBot(new VFXAction(new BleedEffect(m)));
                addToBot(new LoseHPAction(m, p, HP_LOSS));
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Desiccate();
    }
}
