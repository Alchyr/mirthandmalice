package mirthandmalice.cards.yang.uncommon;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.actions.character.DontUseSpecificEnergyAction;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class SagaPeace extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "SagaPeace",
            1,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardTarget.NONE,
            AbstractCard.CardRarity.UNCOMMON
    );

    public final static String ID = makeID(cardInfo.cardName);

    private static final int BLOCK = 8;
    private static final int UPG_BLOCK = 3;

    public SagaPeace()
    {
        super(cardInfo, false);

        setBlock(BLOCK, UPG_BLOCK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
    }

    @Override
    public void triggerWhenCopied() {
        this.superFlash(Color.VIOLET);
        if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE)
        {
            AbstractDungeon.actionManager.addToBottom(new DontUseSpecificEnergyAction());
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, 2));
        }
        else
        {
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, 1));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new SagaPeace();
    }
}