package mirthandmalice.cards.mirth.uncommon;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.patch.manifestation.ManifestField;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class RisingFrenzy extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "RisingFrenzy",
            0,
            CardType.SKILL,
            CardTarget.NONE,
            CardRarity.UNCOMMON);
    // skill

    public static final String ID = makeID(cardInfo.cardName);


    private static final int MAGIC = 4;
    private static final int UPG_MAGIC = 2;

    public RisingFrenzy() {
        super(cardInfo, false);

        setMagic(MAGIC, UPG_MAGIC);
    }

    @Override
    public void applyPowers() {
        if (ManifestField.inHandManifested(this))
        {
            this.target = CardTarget.ALL;
            this.magicNumber = this.baseMagicNumber;
            this.isMagicNumberModified = false;
        }
        else
        {
            this.target = CardTarget.SELF;
            this.magicNumber = this.baseMagicNumber / 2;
            this.isMagicNumberModified = true;
        }
        super.applyPowers();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (ManifestField.inUseManifested())
        {
            applySelf(new VigorPower(p, this.magicNumber));
            this.superFlash();
            for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters)
            {
                applySingle(mo, new VigorPower(mo, this.magicNumber));
            }
        }
        else
        {
            applySelf(new VigorPower(p, this.magicNumber));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new RisingFrenzy();
    }
}