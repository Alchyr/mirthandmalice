package mirthandmalice.util;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.ui.buttons.SingingBowlButton;
import com.megacrit.cardcrawl.ui.buttons.SkipCardButton;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static mirthandmalice.MirthAndMaliceMod.logger;
import static mirthandmalice.MirthAndMaliceMod.makeID;

public class CardChoiceBuilder {
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(makeID("CardChoice")).TEXT;

    private static Method placeCards = null;

    private ArrayList<AbstractCard> options = new ArrayList<>();

    public CardChoiceBuilder addOption(AbstractCard c)
    {
        options.add(c.makeStatEquivalentCopy());
        return this;
    }

    public void open()
    {
        AbstractDungeon.cardRewardScreen.rItem = null;
        ReflectionHacks.setPrivate(AbstractDungeon.cardRewardScreen, CardRewardScreen.class, "codex", false);
        ReflectionHacks.setPrivate(AbstractDungeon.cardRewardScreen, CardRewardScreen.class, "discovery", true);
        AbstractDungeon.cardRewardScreen.discoveryCard = null;
        ReflectionHacks.setPrivate(AbstractDungeon.cardRewardScreen, CardRewardScreen.class, "draft", false);
        AbstractDungeon.cardRewardScreen.codexCard = null;
        ((SingingBowlButton)ReflectionHacks.getPrivate(AbstractDungeon.cardRewardScreen, CardRewardScreen.class, "bowlButton")).hide();
        ((SkipCardButton)ReflectionHacks.getPrivate(AbstractDungeon.cardRewardScreen, CardRewardScreen.class, "skipButton")).hide();
        AbstractDungeon.cardRewardScreen.onCardSelect = true;
        AbstractDungeon.topPanel.unhoverHitboxes();

        AbstractDungeon.cardRewardScreen.rewardGroup = options;
        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.screen = AbstractDungeon.CurrentScreen.CARD_REWARD;
        AbstractDungeon.dynamicBanner.appear(TEXT[0]);
        AbstractDungeon.overlayMenu.showBlackScreen();

        try
        {
            if (placeCards == null)
            {
                placeCards = CardRewardScreen.class.getDeclaredMethod("placeCards", float.class, float.class);
                placeCards.setAccessible(true);
            }

            placeCards.invoke(AbstractDungeon.cardRewardScreen, (float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT * 0.45F);
        }
        catch (Exception e)
        {
            logger.error("Failed to invoke method placeCards in CardRewardScreen.");
            logger.error(e.getMessage());
        }

        for (AbstractCard c : options)
        {
            UnlockTracker.markCardAsSeen(c.cardID);
        }
    }
}
