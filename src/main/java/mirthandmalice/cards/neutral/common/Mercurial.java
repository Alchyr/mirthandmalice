package mirthandmalice.cards.neutral.common;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.NeutralCard;
import mirthandmalice.actions.character.ForceDrawAction;
import mirthandmalice.actions.character.OtherPlayerDiscardAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Mercurial extends NeutralCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Mercurial",
            1,
            CardType.ATTACK,
            CardTarget.ENEMY,
            CardRarity.COMMON);
    // attack

    public final static String ID = makeID(cardInfo.cardName);

    private static final int DAMAGE = 8;
    private static final int UPG_DAMAGE = 3;


    public Mercurial() {
        super(cardInfo, false);

        setDamage(DAMAGE, UPG_DAMAGE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), MathUtils.randomBoolean() ? AbstractGameAction.AttackEffect.SLASH_VERTICAL : AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));

        AbstractDungeon.actionManager.addToBottom(new ForceDrawAction(TrackCardSource.useOtherEnergy, 1));
        AbstractDungeon.actionManager.addToBottom(new ForceDrawAction(TrackCardSource.useMyEnergy, 1));

        //The one who plays it discards, then the one who does not play it discards.
        AbstractDungeon.actionManager.addToBottom(new DiscardAction(p, p, 1, false, false));
        if (p instanceof MirthAndMalice)
        {
            AbstractDungeon.actionManager.addToBottom(new OtherPlayerDiscardAction((MirthAndMalice)p, p, 1, false, false));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Mercurial();
    }
}