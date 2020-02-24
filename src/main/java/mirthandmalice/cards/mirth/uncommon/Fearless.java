package mirthandmalice.cards.mirth.uncommon;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.blue.Blizzard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.patch.manifestation.ManifestField;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Fearless extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Fearless",
            2,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.SELF,
            AbstractCard.CardRarity.COMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int BLOCK = 8;

    private static final int MULTIPLIER = 3;
    private static final int UPG_MULTIPLIER = 1;

    private boolean isMultiplied;

    public Fearless()
    {
        super(cardInfo, false);

        setBlock(BLOCK);
        setMagic(MULTIPLIER, UPG_MULTIPLIER);

        isMultiplied = false;
    }

    @Override
    public void triggerOnGlowCheck() { //cards in other hand do not glow, so no need for any fancy checks
        if (ManifestField.isManifested())
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        else
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
    }

    @Override
    public void applyPowers() {
        super.applyPowers();

        if (!ManifestField.inHandManifested(this))
        {
            isMultiplied = true;
            this.block *= this.magicNumber;
            if (this.block != this.baseBlock)
                isBlockModified = true;
        }
        else
        {
            if (TrackCardSource.useMyEnergy) //bonus check
            {
                if (ManifestField.otherManifested()) //I played it, I'm not manifested.
                {
                    isMultiplied = true;
                    this.block *= this.magicNumber;
                    if (this.block != this.baseBlock)
                        isBlockModified = true;
                }
            }
            else if (TrackCardSource.useOtherEnergy)
            {
                if (ManifestField.isManifested()) //Other guy played it, I'm manifested.
                {
                    isMultiplied = true;
                    this.block *= this.magicNumber;
                    if (this.block != this.baseBlock)
                        isBlockModified = true;
                }
            }
            else
            {
                isMultiplied = false;
            }
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!isMultiplied) //Safety check, in case apply powers was called from a location other than hand (which would mean it is not multiplied properly)
        {
            if (TrackCardSource.useMyEnergy)
            {
                if (ManifestField.otherManifested()) //I played it, I'm not manifested.
                {
                    isMultiplied = true;
                    this.block *= this.magicNumber;
                    if (this.block != this.baseBlock)
                        isBlockModified = true;
                }
            }
            else
            {
                if (ManifestField.isManifested()) //Other guy played it, I'm manifested.
                {
                    isMultiplied = true;
                    this.block *= this.magicNumber;
                    if (this.block != this.baseBlock)
                        isBlockModified = true;
                }
            }
        }
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
        manifest();
    }

    @Override
    public void onMoveToDiscard() {
        isMultiplied = false;
    }

    @Override
    public void resetAttributes() {
        super.resetAttributes();
        isMultiplied = false;
    }

    @Override
    public AbstractCard makeCopy() {
        return new Fearless();
    }
}