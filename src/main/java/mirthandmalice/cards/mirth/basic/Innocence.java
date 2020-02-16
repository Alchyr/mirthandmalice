package mirthandmalice.cards.mirth.basic;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MirthCard;
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

    private static final int BLOCK = 5;
    private static final int UPG_BLOCK = 3;


    public Innocence() {
        super(cardInfo, false);

        setBlock(BLOCK, UPG_BLOCK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        block();
        if (ManifestField.isManifested())
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