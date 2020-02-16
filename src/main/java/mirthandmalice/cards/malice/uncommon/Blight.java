package mirthandmalice.cards.malice.uncommon;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.powers.AtrophyPower;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Blight extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Blight",
            0,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.ALL_ENEMY,
            AbstractCard.CardRarity.UNCOMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int DEBUFF = 2;

    public Blight()
    {
        super(cardInfo, true);

        setMagic(DEBUFF);
        setExhaust(true);

        this.isMultiDamage = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (upgraded)
        {
            applyAll(new AtrophyPower(p, null, this.magicNumber), true, AbstractGameAction.AttackEffect.POISON);
        }
        applyAll(new AtrophyPower(p, null, this.magicNumber), AbstractGameAction.AttackEffect.POISON);

        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 1));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Blight();
    }
}