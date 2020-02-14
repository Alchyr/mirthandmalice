package mirthandmalice.cards.colorless.deprecated;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.BaseCard;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.enums.CustomCardTags;
import mirthandmalice.util.CardInfo;
import mirthandmalice.util.annotations.Disabled;

import static mirthandmalice.MirthAndMaliceMod.makeID;

@Disabled
public class Fragment extends BaseCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Fragment",
            0,
            AbstractCard.CardType.ATTACK,
            AbstractCard.CardTarget.ENEMY,
            AbstractCard.CardRarity.SPECIAL
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int DAMAGE = 4;
    private static final int UPG_DAMAGE = 2;

    public Fragment()
    {
        super(AbstractCard.CardColor.COLORLESS, cardInfo, false);
        setMagic(DAMAGE, UPG_DAMAGE);

        this.tags.add(CustomCardTags.MK_FRAGMENT);
    }


    @Override
    public void applyPowers() {
        int amt = fragmentCount(this);
        this.baseDamage = this.baseMagicNumber * amt;
        super.applyPowers();

        this.rawDescription = cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0];
        this.initializeDescription();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int amt = fragmentCount(this);
        this.baseDamage = this.baseMagicNumber * amt;
        super.calculateCardDamage(mo);

        this.rawDescription = cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0];
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.damage > 0)
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HEAVY));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Fragment();
    }

    private static int fragmentCount(AbstractCard source)
    {
        if (AbstractDungeon.player != null)
        {
            int amt = 1;

            if (AbstractDungeon.player instanceof MirthAndMalice)
            {

                for (AbstractCard c : ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.group)
                    if (c.hasTag(CustomCardTags.MK_FRAGMENT) && !c.equals(source))
                        ++amt;

                for (AbstractCard c : ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.group)
                    if (c.hasTag(CustomCardTags.MK_FRAGMENT) && !c.equals(source))
                        ++amt;

                for (AbstractCard c : ((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard.group)
                    if (c.hasTag(CustomCardTags.MK_FRAGMENT) && !c.equals(source))
                        ++amt;
            }

            for (AbstractCard c : AbstractDungeon.player.hand.group)
                if (c.hasTag(CustomCardTags.MK_FRAGMENT) && !c.equals(source))
                    ++amt;

            for (AbstractCard c : AbstractDungeon.player.drawPile.group)
                if (c.hasTag(CustomCardTags.MK_FRAGMENT) && !c.equals(source))
                    ++amt;

            for (AbstractCard c : AbstractDungeon.player.discardPile.group)
                if (c.hasTag(CustomCardTags.MK_FRAGMENT) && !c.equals(source))
                    ++amt;
            return amt;
        }
        return 1;
    }
}