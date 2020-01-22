package mirthandmalice.cards.malice.basic;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.util.CardInfo;

import static basemod.helpers.BaseModCardTags.BASIC_DEFEND;
import static mirthandmalice.MirthAndMaliceMod.makeID;

public class MaliceDefend extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "K_Defend",
            1,
            CardType.SKILL,
            CardTarget.SELF,
            CardRarity.BASIC
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int BLOCK = 4;
    private static final int UPG_BLOCK = 3;

    public MaliceDefend()
    {
        super(cardInfo, false);

        setBlock(BLOCK, UPG_BLOCK);

        tags.add(BASIC_DEFEND);
        tags.add(CardTags.STARTER_DEFEND);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
    }

    @Override
    public AbstractCard makeCopy() {
        return new MaliceDefend();
    }
}
