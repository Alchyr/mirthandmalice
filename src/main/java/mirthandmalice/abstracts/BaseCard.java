package mirthandmalice.abstracts;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import mirthandmalice.actions.character.ManifestAction;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.util.CardInfo;
import mirthandmalice.util.TextureLoader;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public abstract class BaseCard extends CustomCard {
    protected CardStrings cardStrings;
    protected String img;

    protected boolean upgradesDescription;

    protected int baseCost;

    protected boolean upgradeCost;
    protected boolean upgradeDamage;
    protected boolean upgradeBlock;
    protected boolean upgradeMagic;

    protected int costUpgrade;
    protected int damageUpgrade;
    protected int blockUpgrade;
    protected int magicUpgrade;

    protected boolean baseExhaust;
    protected boolean upgExhaust;
    protected boolean baseInnate;
    protected boolean upgInnate;

    public BaseCard(CardColor color, CardInfo cardInfo, boolean upgradesDescription)
    {
        this(color, cardInfo.cardName, cardInfo.cardCost, cardInfo.cardType, cardInfo.cardTarget, cardInfo.cardRarity, upgradesDescription);
    }

    public BaseCard(CardColor color, String cardName, int cost, CardType cardType, CardTarget target, CardRarity rarity, boolean upgradesDescription)
    {
        super(makeID(cardName), "", (String)null, cost, "", cardType, color, rarity, target);

        cardStrings = CardCrawlGame.languagePack.getCardStrings(cardID);

        img = TextureLoader.getAndLoadCardTextureString(cardName, cardType);
        this.textureImg = img;
        loadCardImage(textureImg);

        this.rawDescription = cardStrings.DESCRIPTION;
        this.originalName = cardStrings.NAME;
        this.name = originalName;

        this.baseCost = cost;

        this.upgradesDescription = upgradesDescription;

        this.upgradeCost = false;
        this.upgradeDamage = false;
        this.upgradeBlock = false;
        this.upgradeMagic = false;

        this.costUpgrade = cost;
        this.damageUpgrade = 0;
        this.blockUpgrade = 0;
        this.magicUpgrade = 0;

        InitializeCard();
    }

    //Methods meant for constructor use
    public void setDamage(int damage)
    {
        this.setDamage(damage, 0);
    }
    public void setBlock(int block)
    {
        this.setBlock(block, 0);
    }
    public void setMagic(int magic)
    {
        this.setMagic(magic, 0);
    }
    public void setCostUpgrade(int costUpgrade)
    {
        this.costUpgrade = costUpgrade;
        this.upgradeCost = true;
    }
    public void setExhaust(boolean exhaust) { this.setExhaust(exhaust, exhaust); }
    public void setDamage(int damage, int damageUpgrade)
    {
        this.baseDamage = this.damage = damage;
        if (damageUpgrade != 0)
        {
            this.upgradeDamage = true;
            this.damageUpgrade = damageUpgrade;
        }
    }
    public void setBlock(int block, int blockUpgrade)
    {
        this.baseBlock = this.block = block;
        if (blockUpgrade != 0)
        {
            this.upgradeBlock = true;
            this.blockUpgrade = blockUpgrade;
        }
    }
    public void setMagic(int magic, int magicUpgrade)
    {
        this.baseMagicNumber = this.magicNumber = magic;
        if (magicUpgrade != 0)
        {
            this.upgradeMagic = true;
            this.magicUpgrade = magicUpgrade;
        }
    }
    public void setExhaust(boolean baseExhaust, boolean upgExhaust)
    {
        this.baseExhaust = baseExhaust;
        this.upgExhaust = upgExhaust;
        this.exhaust = baseExhaust;
    }
    public void setInnate(boolean baseInnate, boolean upgInnate)
    {
        this.baseInnate = baseInnate;
        this.isInnate = baseInnate;
        this.upgInnate = upgInnate;
    }

    public void setMultiDamage(boolean isMultiDamage)
    {
        this.isMultiDamage = isMultiDamage;
    }

    @Override
    public AbstractCard makeStatEquivalentCopy() {
        AbstractCard card = super.makeStatEquivalentCopy();

        if (card instanceof BaseCard)
        {
            card.rawDescription = this.rawDescription;
            ((BaseCard) card).upgradesDescription = this.upgradesDescription;

            ((BaseCard) card).baseCost = this.baseCost;

            ((BaseCard) card).upgradeCost = this.upgradeCost;
            ((BaseCard) card).upgradeDamage = this.upgradeDamage;
            ((BaseCard) card).upgradeBlock = this.upgradeBlock;
            ((BaseCard) card).upgradeMagic = this.upgradeMagic;

            ((BaseCard) card).costUpgrade = this.costUpgrade;
            ((BaseCard) card).damageUpgrade = this.damageUpgrade;
            ((BaseCard) card).blockUpgrade = this.blockUpgrade;
            ((BaseCard) card).magicUpgrade = this.magicUpgrade;

            ((BaseCard) card).baseExhaust = this.baseExhaust;
            ((BaseCard) card).upgExhaust = this.upgExhaust;
            ((BaseCard) card).baseInnate = this.baseInnate;
            ((BaseCard) card).upgInnate = this.upgInnate;
        }

        return card;
    }

    @Override
    public void upgrade()
    {
        if (!upgraded)
        {
            this.upgradeName();

            if (this.upgradesDescription)
                this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;

            if (upgradeCost)
            {
                int diff = this.baseCost - this.cost; //positive if cost is reduced

                this.upgradeBaseCost(costUpgrade);
                this.cost -= diff;
                this.costForTurn -= diff;
                if (cost < 0)
                    cost = 0;

                if (costForTurn < 0)
                    costForTurn = 0;
            }

            if (upgradeDamage)
                this.upgradeDamage(damageUpgrade);

            if (upgradeBlock)
                this.upgradeBlock(blockUpgrade);

            if (upgradeMagic)
                this.upgradeMagicNumber(magicUpgrade);

            if (baseExhaust ^ upgExhaust) //different
                this.exhaust = upgExhaust;

            if (baseInnate ^ upgInnate) //different
                this.isInnate = upgInnate;


            this.initializeDescription();
        }
    }

    public void InitializeCard()
    {
        FontHelper.cardDescFont_N.getData().setScale(1.0f);
        this.initializeTitle();
        this.initializeDescription();
    }

    @Override
    public void update() {
        super.update();
    }


    //utility methods
    protected void manifest() {
        addToBot(new ManifestAction(TrackCardSource.useOtherEnergy));
    }
    protected void relinquish() {
        addToBot(new ManifestAction(TrackCardSource.useMyEnergy));
    }
    protected void drawCards(int amount) {
        addToBot(new DrawCardAction(amount));
    }
    protected void block() {
        addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, this.block));
    }
    protected void damageSingle(AbstractMonster m, AbstractGameAction.AttackEffect effect)
    {
        addToBot(new DamageAction(m, new DamageInfo(AbstractDungeon.player, this.damage, this.damageTypeForTurn), effect));
    }
    protected void damageSingle(AbstractMonster m, int amount, AbstractGameAction.AttackEffect effect)
    {
        addToBot(new DamageAction(m, new DamageInfo(AbstractDungeon.player, amount, this.damageTypeForTurn), effect));
    }
    protected void damageAll(AbstractGameAction.AttackEffect effect)
    {
        addToBot(new DamageAllEnemiesAction(AbstractDungeon.player, this.multiDamage, this.damageTypeForTurn, effect));
    }

    protected void applySingle(AbstractMonster m, AbstractPower power)
    {
        addToBot(new ApplyPowerAction(m, AbstractDungeon.player, power, power.amount));
    }

    protected void applySelf(AbstractPower power)
    {
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, power, power.amount));
    }

    protected WeakPower getWeak(AbstractMonster m, int amount)
    {
        return new WeakPower(m, amount, false);
    }
    protected VulnerablePower getVuln(AbstractMonster m, int amount)
    {
        return new VulnerablePower(m, amount, false);
    }
}