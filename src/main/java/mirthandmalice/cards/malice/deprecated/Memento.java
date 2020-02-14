package mirthandmalice.cards.malice.deprecated;
/*
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import もこけね.abstracts.KeineCard;
import もこけね.character.MokouKeine;
import もこけね.patch.enums.CustomCardTags;
import もこけね.util.CardInfo;

import static もこけね.もこけねは神の国.makeID;

public class Memento extends KeineCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Memento",
            1,
            CardType.ATTACK,
            CardTarget.ENEMY,
            CardRarity.COMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int DAMAGE = 5;
    private static final int UPG_DAMAGE = 2;

    public Memento()
    {
        super(cardInfo, false);

        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(DAMAGE, UPG_DAMAGE);
        tags.add(CustomCardTags.MEMENTO);
    }

    @Override
    public void applyPowers() {
        super.applyPowers();

        int amt = mementoCount();
        this.magicNumber = this.damage * amt;
        this.baseMagicNumber = this.baseDamage * amt;
        this.isMagicNumberModified = this.isDamageModified;

        this.rawDescription = cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0];
        this.initializeDescription();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);

        int amt = mementoCount();
        this.magicNumber = this.damage * amt;
        this.baseMagicNumber = this.baseDamage * amt;
        this.isMagicNumberModified = this.isDamageModified;

        this.rawDescription = cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0];
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(p, this.magicNumber, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HEAVY));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Memento();
    }



    private static int mementoCount()
    {
        if (AbstractDungeon.player != null)
        {
            int amt = 0;

            if (AbstractDungeon.player instanceof MokouKeine)
            {

                for (AbstractCard c : ((MokouKeine) AbstractDungeon.player).otherPlayerHand.group)
                    if (c.hasTag(CustomCardTags.MEMENTO))
                        ++amt;

                for (AbstractCard c : ((MokouKeine) AbstractDungeon.player).otherPlayerDraw.group)
                    if (c.hasTag(CustomCardTags.MEMENTO))
                        ++amt;

                for (AbstractCard c : ((MokouKeine) AbstractDungeon.player).otherPlayerDiscard.group)
                    if (c.hasTag(CustomCardTags.MEMENTO))
                        ++amt;
            }

            for (AbstractCard c : AbstractDungeon.player.hand.group)
                if (c.hasTag(CustomCardTags.MEMENTO))
                    ++amt;

            for (AbstractCard c : AbstractDungeon.player.drawPile.group)
                if (c.hasTag(CustomCardTags.MEMENTO))
                    ++amt;

            for (AbstractCard c : AbstractDungeon.player.discardPile.group)
                if (c.hasTag(CustomCardTags.MEMENTO))
                    ++amt;
            return amt;
        }
        return 1;
    }
}*/