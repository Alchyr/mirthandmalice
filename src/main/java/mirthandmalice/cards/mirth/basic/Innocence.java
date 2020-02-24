package mirthandmalice.cards.mirth.basic;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.patch.manifestation.ManifestField;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Innocence extends MirthCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Innocence",
            0,
            CardType.SKILL,
            CardTarget.SELF,
            CardRarity.BASIC);
    // skill

    public final static String ID = makeID(cardInfo.cardName);

    private static final int BLOCK = 6;
    private static final int UPG_BLOCK = 2;


    public Innocence() {
        super(cardInfo, false);

        setBlock(BLOCK, UPG_BLOCK);
    }

    @Override
    public void applyPowers() {
        if (ManifestField.inHandManifested(this))
        {
            this.target = CardTarget.ALL;
        }
        else
        {
            this.target = CardTarget.SELF;
        }
        super.applyPowers();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        block();
        if (TrackCardSource.useMyEnergy && ManifestField.isManifested() ||
                TrackCardSource.useOtherEnergy && ManifestField.otherManifested())
        {
            this.superFlash();
            for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters)
            {
                addToBot(new GainBlockAction(mo, p, this.block));
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Innocence();
    }
}