package mirthandmalice.patch.actions;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.unique.ApplyStasisAction;
import com.megacrit.cardcrawl.actions.utility.ShowCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StasisPower;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.powers.Stasis;

import java.util.ArrayList;

@SpirePatch(
        clz = ApplyStasisAction.class,
        method = "update"
)
public class StasisAction {
    private static boolean stoleFromOther = false;
    private static AbstractCard lastCard = null;

    //Controls who card is stolen from, and ensures consistent rng usage.

    @SpirePrefixPatch
    public static SpireReturn stealFromOther(ApplyStasisAction __instance)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            float duration = (float)ReflectionHacks.getPrivate(__instance, AbstractGameAction.class, "duration");

            if (duration == (float) ReflectionHacks.getPrivate(__instance, ApplyStasisAction.class, "startingDuration")) {
                //first run
                MirthAndMalice p = (MirthAndMalice)AbstractDungeon.player;

                boolean targetMokou = AbstractDungeon.aiRng.randomBoolean();

                boolean targetOther = targetMokou ^ ((MirthAndMalice) AbstractDungeon.player).isMirth;

                boolean otherEmpty = p.otherPlayerDraw.isEmpty() && p.otherPlayerDiscard.isEmpty();
                boolean selfEmpty = p.drawPile.isEmpty() && p.discardPile.isEmpty();

                if (otherEmpty && selfEmpty)
                {
                    __instance.isDone = true; //no cards in any pile
                    return SpireReturn.Return(null); //this is done
                }
                else if (targetOther && otherEmpty) {
                    targetOther = false;
                }
                else if (!targetOther && selfEmpty) {
                    targetOther = true;
                }

                //Target should be guaranteed to have a card in one of the piles.
                stoleFromOther = targetOther; //target is now set

                AbstractCard c; //card to steal
                ArrayList<AbstractCard> possibleCards = new ArrayList<>();

                if (targetOther) {
                    if (p.otherPlayerDraw.isEmpty()) {
                        //draw is empty, take from discard. (Should be impossible for both to be empty.)
                        for (AbstractCard card : p.otherPlayerDiscard.group) {
                            if (card.rarity == AbstractCard.CardRarity.RARE)
                                possibleCards.add(card);
                        }
                        if (possibleCards.isEmpty()) {
                            for (AbstractCard card : p.otherPlayerDiscard.group) {
                                if (card.rarity == AbstractCard.CardRarity.UNCOMMON)
                                    possibleCards.add(card);
                            }
                            if (possibleCards.isEmpty()) {
                                for (AbstractCard card : p.otherPlayerDiscard.group) {
                                    if (card.rarity == AbstractCard.CardRarity.COMMON)
                                        possibleCards.add(card);
                                }
                                if (possibleCards.isEmpty()) {
                                    possibleCards.addAll((p.otherPlayerDiscard.group));
                                }
                            }
                        }

                        c = possibleCards.get(AbstractDungeon.cardRandomRng.random(0, possibleCards.size() - 1));
                        p.otherPlayerDiscard.removeCard(c);
                    }
                    else { //draw is not empty.
                        for (AbstractCard card : p.otherPlayerDraw.group) {
                            if (card.rarity == AbstractCard.CardRarity.RARE)
                                possibleCards.add(card);
                        }
                        if (possibleCards.isEmpty()) {
                            for (AbstractCard card : p.otherPlayerDraw.group) {
                                if (card.rarity == AbstractCard.CardRarity.UNCOMMON)
                                    possibleCards.add(card);
                            }
                            if (possibleCards.isEmpty()) {
                                for (AbstractCard card : p.otherPlayerDraw.group) {
                                    if (card.rarity == AbstractCard.CardRarity.COMMON)
                                        possibleCards.add(card);
                                }
                                if (possibleCards.isEmpty()) {
                                    possibleCards.addAll((p.otherPlayerDraw.group));
                                }
                            }
                        }

                        c = possibleCards.get(AbstractDungeon.cardRandomRng.random(0, possibleCards.size() - 1));
                        p.otherPlayerDraw.removeCard(c);
                    }
                }
                else { //targeting self
                    if (p.drawPile.isEmpty()) {
                        //draw is empty, take from discard. (Should be impossible for both to be empty.)
                        for (AbstractCard card : p.discardPile.group) {
                            if (card.rarity == AbstractCard.CardRarity.RARE)
                                possibleCards.add(card);
                        }
                        if (possibleCards.isEmpty()) {
                            for (AbstractCard card : p.discardPile.group) {
                                if (card.rarity == AbstractCard.CardRarity.UNCOMMON)
                                    possibleCards.add(card);
                            }
                            if (possibleCards.isEmpty()) {
                                for (AbstractCard card : p.discardPile.group) {
                                    if (card.rarity == AbstractCard.CardRarity.COMMON)
                                        possibleCards.add(card);
                                }
                                if (possibleCards.isEmpty()) {
                                    possibleCards.addAll((p.discardPile.group));
                                }
                            }
                        }

                        c = possibleCards.get(AbstractDungeon.cardRandomRng.random(0, possibleCards.size() - 1));
                        p.discardPile.removeCard(c);
                    }
                    else { //draw is not empty.
                        for (AbstractCard card : p.drawPile.group) {
                            if (card.rarity == AbstractCard.CardRarity.RARE)
                                possibleCards.add(card);
                        }
                        if (possibleCards.isEmpty()) {
                            for (AbstractCard card : p.drawPile.group) {
                                if (card.rarity == AbstractCard.CardRarity.UNCOMMON)
                                    possibleCards.add(card);
                            }
                            if (possibleCards.isEmpty()) {
                                for (AbstractCard card : p.drawPile.group) {
                                    if (card.rarity == AbstractCard.CardRarity.COMMON)
                                        possibleCards.add(card);
                                }
                                if (possibleCards.isEmpty()) {
                                    possibleCards.addAll((p.drawPile.group));
                                }
                            }
                        }

                        c = possibleCards.get(AbstractDungeon.cardRandomRng.random(0, possibleCards.size() - 1));
                        p.drawPile.removeCard(c);
                    }
                }
                ReflectionHacks.setPrivate(__instance, ApplyStasisAction.class, "card", c);

                p.limbo.addToBottom(c);
                c.setAngle(0.0F);
                c.targetDrawScale = 0.75F;
                c.target_x = (float) Settings.WIDTH / 2.0F;
                c.target_y = (float)Settings.HEIGHT / 2.0F;
                c.lighten(false);
                c.unfadeOut();
                c.unhover();
                c.untip();
                c.stopGlowing();

                lastCard = c;
            }

            duration -= Gdx.graphics.getDeltaTime();
            ReflectionHacks.setPrivate(__instance, AbstractGameAction.class, "duration", duration);

            if (duration <= 0)
            {
                __instance.isDone = true;
                if (lastCard != null)
                {
                    AbstractCard c = (AbstractCard) ReflectionHacks.getPrivate(__instance, ApplyStasisAction.class, "card");
                    if (lastCard.equals(c)) //lastCard is not null, so even if c is null this will not be null pointer
                    {
                        AbstractCreature owner = (AbstractCreature) ReflectionHacks.getPrivate(__instance, ApplyStasisAction.class, "owner");

                        StasisPower youDirtyThief = new StasisPower(owner, c);
                        Stasis.StasisFields.stoleOther.set(youDirtyThief, stoleFromOther);

                        AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(owner, owner, youDirtyThief));
                        AbstractDungeon.actionManager.addToTop(new ShowCardAction(c));


                        lastCard = null;
                    }
                }
            }

            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}
