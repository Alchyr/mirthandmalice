package mirthandmalice.cards.malice.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.actions.general.MarkRandomCardInDrawAction;
import mirthandmalice.patch.energy_division.TrackCardSource;
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

    public Intrigue()
    {
        super(cardInfo, true);

        setDamage(DAMAGE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

        AbstractDungeon.actionManager.addToBottom(new MarkRandomCardInDrawAction(TrackCardSource.useOtherEnergy, false));

        if (upgraded)
        {
            AbstractDungeon.actionManager.addToBottom(new MarkRandomCardInDrawAction(TrackCardSource.useMyEnergy, false));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Intrigue();
    }
}
