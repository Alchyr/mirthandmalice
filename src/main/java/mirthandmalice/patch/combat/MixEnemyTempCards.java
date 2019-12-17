package mirthandmalice.patch.combat;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.actions.character.MakeTempCardInOtherDiscardAction;
import mirthandmalice.actions.character.MakeTempCardInOtherDrawAction;
import mirthandmalice.actions.character.MakeTempCardInOtherHandAction;
import mirthandmalice.character.MirthAndMalice;

public class MixEnemyTempCards {
    public static boolean toMirth = true;

    @SpirePatch(
            clz = GameActionManager.class,
            method = "addToBottom"
    )
    public static class ChangeTarget
    {
        @SpirePrefixPatch
        public static void changeAction(GameActionManager __instance, @ByRef(type="com.megacrit.cardcrawl.actions.AbstractGameAction") Object[] action)
        {
            if (AbstractDungeon.player instanceof MirthAndMalice && !__instance.monsterQueue.isEmpty()) {
                //This action occurred during enemy turn
                if (action[0] instanceof MakeTempCardInDiscardAction)
                {
                    boolean sameUUID = (boolean)ReflectionHacks.getPrivate(action[0], MakeTempCardInDiscardAction.class, "sameUUID");
                    if (sameUUID)
                    {
                        return;
                    }

                    if (toMirth ^ ((MirthAndMalice) AbstractDungeon.player).isMirth) //targeting other player
                    {
                        AbstractCard c = (AbstractCard)ReflectionHacks.getPrivate(action[0], MakeTempCardInDiscardAction.class, "cardToMake");
                        int amount = (int)ReflectionHacks.getPrivate(action[0], MakeTempCardInDiscardAction.class, "numCards");

                        action[0] = new MakeTempCardInOtherDiscardAction(c, amount);
                    }

                    toMirth = !toMirth;
                }
                else if (action[0] instanceof MakeTempCardInHandAction)
                {
                    boolean sameUUID = (boolean)ReflectionHacks.getPrivate(action[0], MakeTempCardInHandAction.class, "sameUUID");
                    if (sameUUID)
                    {
                        return;
                    }

                    if (toMirth ^ ((MirthAndMalice) AbstractDungeon.player).isMirth) //targeting other player
                    {
                        boolean isOtherCardInCenter = (boolean)ReflectionHacks.getPrivate(action[0], MakeTempCardInHandAction.class, "isOtherCardInCenter");
                        AbstractCard c = (AbstractCard)ReflectionHacks.getPrivate(action[0], MakeTempCardInHandAction.class, "c");

                        action[0] = new MakeTempCardInOtherHandAction(c, ((AbstractGameAction)action[0]).amount, isOtherCardInCenter);
                    }

                    toMirth = !toMirth;
                }
                else if (action[0] instanceof MakeTempCardInDrawPileAction)
                {
                    if (toMirth ^ ((MirthAndMalice) AbstractDungeon.player).isMirth) //targeting other player
                    {
                        boolean randomSpot = (boolean)ReflectionHacks.getPrivate(action[0], MakeTempCardInDrawPileAction.class, "randomSpot");
                        boolean autoPosition = (boolean)ReflectionHacks.getPrivate(action[0], MakeTempCardInDrawPileAction.class, "autoPosition");
                        boolean toBottom = (boolean)ReflectionHacks.getPrivate(action[0], MakeTempCardInDrawPileAction.class, "toBottom");
                        float x = (float)ReflectionHacks.getPrivate(action[0], MakeTempCardInDrawPileAction.class, "x");
                        float y = (float)ReflectionHacks.getPrivate(action[0], MakeTempCardInDrawPileAction.class, "y");
                        AbstractCard c = (AbstractCard)ReflectionHacks.getPrivate(action[0], MakeTempCardInDrawPileAction.class, "cardToMake");

                        action[0] = new MakeTempCardInOtherDrawAction(c, ((AbstractGameAction)action[0]).amount, randomSpot, autoPosition, toBottom, x, y);
                    }

                    toMirth = !toMirth;
                }
            }
        }
    }

    /*
    private static AbstractGameAction lastAction = null;

    //Check if isPlayerTurn
    @SpirePatch(
            clz = MakeTempCardInDiscardAction.class,
            method = "update"
    )
    public static class changeDestination
    {
        @SpirePrefixPatch
        public static SpireReturn adjust(MakeTempCardInDiscardAction __instance)
        {
            if (AbstractDungeon.player instanceof YinAndYang && AbstractDungeon.actionManager.turnHasEnded && __instance != lastAction)
            {
                //This action occurred during enemy turn
                toYin = !toYin; //swap target
                lastAction = __instance; //don't check more than once
                if (toYin ^ ((YinAndYang) AbstractDungeon.player).isYin) //The target is other player
                {
                    float d = (float)ReflectionHacks.getPrivate(__instance, AbstractGameAction.class, "duration");
                    if (d == Settings.ACTION_DUR_FAST)
                    {
                        //do the stuff, but for other discard pile.
                        int amt = (int)ReflectionHacks.getPrivate(__instance, MakeTempCardInDiscardAction.class, "numCards");
                        AbstractCard base = (AbstractCard)ReflectionHacks.getPrivate(__instance, MakeTempCardInDiscardAction.class, "cardToMake");
                        AbstractCard c;

                        for(int i = 0; i < amt; ++i) {
                            c = base.makeStatEquivalentCopy();
                            AbstractDungeon.effectList.add(new ShowCardAndAddToOtherDiscardEffect(c));
                        }

                        ReflectionHacks.setPrivate(__instance, AbstractGameAction.class, "duration", d - Gdx.graphics.getDeltaTime());
                    }
                    return SpireReturn.Return(null);
                }
            }
            return SpireReturn.Continue();
        }
    }*/
}
