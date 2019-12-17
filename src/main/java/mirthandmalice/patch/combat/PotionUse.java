package mirthandmalice.patch.combat;

import basemod.ReflectionHacks;
import com.badlogic.gdx.utils.Queue;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CtBehavior;
import mirthandmalice.actions.character.DontUseSpecificEnergyAction;
import mirthandmalice.actions.character.SetEnergyGainAction;
import mirthandmalice.actions.character.UseSpecificEnergyAction;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.HandleMatchmaking;
import mirthandmalice.util.MultiplayerHelper;
import mirthandmalice.util.PotionQueueItem;

import java.util.ArrayList;

import static mirthandmalice.util.MultiplayerHelper.partnerName;
import static mirthandmalice.util.MultiplayerHelper.sendP2PMessage;
import static mirthandmalice.MirthAndMaliceMod.*;

public class PotionUse {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("PotionUse"));
    private static final String[] TEXT = uiStrings.TEXT;

    public static Queue<PotionQueueItem> queuedPotionUse = new Queue<>();
    private static boolean addReset = false;

    public static void updatePotionInfo()
    {
        StringBuilder sb = new StringBuilder("update_potions");
        boolean first = true;
        for (AbstractPotion p : AbstractDungeon.player.potions)
        {
            if (!(p instanceof PotionSlot))
            {
                if (!first)
                {
                    sb.append("!!!");
                }
                first = false;
                sb.append(p.ID);
            }
        }
        MultiplayerHelper.sendP2PString(sb.toString());
    }
    public static void receivePotionUpdate(String info)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            MultiplayerHelper.otherPlayerPotions.clear();
            StringBuilder log = new StringBuilder("Other player's potions: ");

            String[] ids = info.split("!!!");
            for (String id : ids)
            {
                if (!id.isEmpty())
                {
                    AbstractPotion p = PotionHelper.getPotion(id);
                    if (p != null) //just to ensure the ID is a real potion that exists.
                    {
                        MultiplayerHelper.otherPlayerPotions.add(id);
                        log.append(p.name).append(" ");
                    }
                }
            }
            logger.info(log.toString());
        }
    }

    public static boolean otherCanUsePotion(String info)
    {
        String[] args = info.split("!!!");
        if (args.length == 3 && AbstractDungeon.player instanceof MirthAndMalice)
        {
            return MultiplayerHelper.otherPlayerPotions.remove(args[0]);
        }
        return false;
    }

    public static void usePotion(String info)
    {
        String[] args = info.split("!!!");
        if (args.length == 2 && AbstractDungeon.player instanceof MirthAndMalice)
        {
            AbstractPotion toUse = PotionHelper.getPotion(args[0]);
            if (MultiplayerHelper.otherPlayerPotions.contains(toUse.ID))
            {
                int index = Integer.valueOf(args[1]);

                AbstractDungeon.actionManager.addToBottom(new UseSpecificEnergyAction(true));
                AbstractDungeon.actionManager.addToBottom(new SetEnergyGainAction(false));
                if (index >= 0)
                {
                    AbstractMonster target = AbstractDungeon.getMonsters().monsters.get(index);
                    toUse.use(target);
                    sendP2PMessage(partnerName + TEXT[0] + toUse.name + TEXT[1] + target.name + TEXT[2]);
                }
                else if (index == -2) //target self
                {
                    toUse.use(AbstractDungeon.player);
                    sendP2PMessage(partnerName + TEXT[0] + toUse.name + TEXT[2]);
                }
                else if (!toUse.targetRequired)
                {
                    toUse.use(null);
                    sendP2PMessage(partnerName + TEXT[0] + toUse.name + TEXT[2]);
                }

                for (AbstractRelic r : AbstractDungeon.player.relics)
                {
                    r.onUsePotion();
                }

                AbstractDungeon.actionManager.addToBottom(new DontUseSpecificEnergyAction());
            }
            else {
                logger.error("Host used a potion they don't even have???");
            }
        }
    }
    public static void otherUsePotion(String info)
    {
        String[] args = info.split("!!!");
        if (args.length == 3 && AbstractDungeon.player instanceof MirthAndMalice)
        {
            //can use is checked before this is called
            AbstractPotion toUse = PotionHelper.getPotion(args[0]);

            int index = Integer.valueOf(args[2]);

            AbstractDungeon.actionManager.addToBottom(new UseSpecificEnergyAction(true));
            AbstractDungeon.actionManager.addToBottom(new SetEnergyGainAction(false));
            if (index >= 0)
            {
                AbstractMonster target = AbstractDungeon.getMonsters().monsters.get(index);
                toUse.use(target);
                sendP2PMessage(partnerName + TEXT[0] + toUse.name + TEXT[1] + target.name + TEXT[2]);
            }
            else if (index == -2) //target self
            {
                toUse.use(AbstractDungeon.player);
                sendP2PMessage(partnerName + TEXT[0] + toUse.name + TEXT[2]);
            }
            else if (!toUse.targetRequired)
            {
                toUse.use(null);
                sendP2PMessage(partnerName + TEXT[0] + toUse.name + TEXT[2]);
            }

            for (AbstractRelic r : AbstractDungeon.player.relics)
            {
                r.onUsePotion();
            }

            AbstractDungeon.actionManager.addToBottom(new DontUseSpecificEnergyAction());
        }
    }

    public static void confirmUsePotion(String info)
    {
        String[] args = info.split("!!!");
        if (args.length == 3)
        {
            if (!(AbstractDungeon.player.potions.get(Integer.valueOf(args[1])) instanceof PotionSlot)) //it has mysteriously vanished.
            {
                int targetIndex = Integer.valueOf(args[2]);
                if (targetIndex >= 0)
                {
                    if (targetIndex < AbstractDungeon.getMonsters().monsters.size()) {
                        queuedPotionUse.addLast(new PotionQueueItem(Integer.valueOf(args[1]), AbstractDungeon.getMonsters().monsters.get(targetIndex)));
                    }
                    else {
                        logger.error("Attempted to use potion on a target index that doesn't exist.");
                    }
                }
                else
                {
                    queuedPotionUse.addLast(new PotionQueueItem(Integer.valueOf(args[1]), null));
                }
            }
        }
    }

    @SpirePatch(
            clz = PotionPopUp.class,
            method = "update"
    )
    public static class ForceTargeting
    {
        @SpirePrefixPatch
        public static void executeQueue(PotionPopUp __instance)
        {
            if (queuedPotionUse.size > 0)
            {
                if (AbstractDungeon.player.potions.get(queuedPotionUse.first().slot) instanceof PotionSlot)
                {
                    queuedPotionUse.removeFirst();
                    return;
                }

                ReflectionHacks.setPrivate(__instance, PotionPopUp.class, "potion", AbstractDungeon.player.potions.get(queuedPotionUse.first().slot));
                ReflectionHacks.setPrivate(__instance, PotionPopUp.class, "slot", queuedPotionUse.first().slot);

                if (queuedPotionUse.first().target != null)
                {
                    queuedPotionUse.first().target.hb.hovered = true;
                    __instance.targetMode = true;
                    if (Settings.isControllerMode)
                    {
                        ReflectionHacks.setPrivate(__instance, PotionPopUp.class, "autoTargetFirst", false);
                    }
                    InputHelper.justClickedLeft = true;
                }
                else
                {
                    __instance.isHidden = false;
                    ((Hitbox)ReflectionHacks.getPrivate(__instance, PotionPopUp.class, "hbTop")).clicked = true;
                }
            }
        }
    }

    @SpirePatch(
            clz = PotionPopUp.class,
            method = "updateInput"
    )
    public static class NonTargeted
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars={ "potion", "slot" }
        )
        public static SpireReturn PreUse(PotionPopUp __instance, AbstractPotion potion, int slot)
        {
            if (AbstractDungeon.player instanceof MirthAndMalice && MultiplayerHelper.active) {
                if (HandleMatchmaking.isHost)
                {
                    AbstractDungeon.actionManager.addToBottom(new UseSpecificEnergyAction(false));
                    AbstractDungeon.actionManager.addToBottom(new SetEnergyGainAction(false));
                    addReset = true;
                    MultiplayerHelper.sendP2PString("use_potion" + potion.ID + "!!!-1");
                }
                else
                {
                    if (queuedPotionUse.size > 0) {
                        if (queuedPotionUse.first().slot == slot) {
                            queuedPotionUse.removeFirst();
                            AbstractDungeon.actionManager.addToBottom(new UseSpecificEnergyAction(false));
                            AbstractDungeon.actionManager.addToBottom(new SetEnergyGainAction(false));
                            addReset = true;
                            return SpireReturn.Continue();
                        }
                    }
                    MultiplayerHelper.sendP2PString("try_use_potion" + potion.ID + "!!!" + slot + "!!!-1");
                    return SpireReturn.Return(null);
                }
            }
            return SpireReturn.Continue();
        }

        @SpirePostfixPatch
        public static void reset(PotionPopUp __instance)
        {
            if (addReset)
            {
                AbstractDungeon.actionManager.addToBottom(new DontUseSpecificEnergyAction());
            }
        }
    }

    @SpirePatch(
            clz = PotionPopUp.class,
            method = "updateTargetMode"
    )
    public static class Targeted
    {
        @SpireInsertPatch(
                locator = TargetedLocator.class,
                localvars={ "potion", "slot", "hoveredMonster" }
        )
        public static SpireReturn PreUse(PotionPopUp __instance, AbstractPotion potion, int slot, AbstractMonster hovered)
        {
            if (AbstractDungeon.player instanceof MirthAndMalice && MultiplayerHelper.active) {
                int index = AbstractDungeon.getMonsters().monsters.indexOf(hovered);
                if (HandleMatchmaking.isHost)
                {
                    AbstractDungeon.actionManager.addToBottom(new UseSpecificEnergyAction(false));
                    AbstractDungeon.actionManager.addToBottom(new SetEnergyGainAction(false));
                    addReset = true;
                    MultiplayerHelper.sendP2PString("use_potion" + potion.ID + "!!!" + index);
                }
                else
                {
                    if (queuedPotionUse.size > 0) {
                        if (queuedPotionUse.first().slot == slot) {
                            queuedPotionUse.removeFirst();
                            AbstractDungeon.actionManager.addToBottom(new UseSpecificEnergyAction(false));
                            AbstractDungeon.actionManager.addToBottom(new SetEnergyGainAction(false));
                            addReset = true;
                            return SpireReturn.Continue();
                        }
                    }
                    MultiplayerHelper.sendP2PString("try_use_potion" + potion.ID + "!!!" + slot + "!!!" + index); //to ensure if player has two of same potion, correct one is used on confirmation
                    return SpireReturn.Return(null);
                }
            }
            return SpireReturn.Continue();
        }

        @SpirePostfixPatch
        public static void reset(PotionPopUp __instance)
        {
            if (addReset)
            {
                AbstractDungeon.actionManager.addToBottom(new DontUseSpecificEnergyAction());
            }
        }
    }



    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add"); //adding metric
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
    private static class TargetedLocator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasPower");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "removePotion"
    )
    public static class OnLosePotion
    {
        @SpirePostfixPatch
        public static void UpdatePotion(AbstractPlayer __instance, AbstractPotion toLose)
        {
            if (__instance.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
            {
                updatePotionInfo();
            }
        }
    }
    @SpirePatch(
            clz = TopPanel.class,
            method = "destroyPotion"
    )
    public static class OnDestroyPotion
    {
        @SpirePostfixPatch
        public static void UpdatePotion(TopPanel __instance, int toLose)
        {
            if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
            {
                updatePotionInfo();
            }
        }
    }
}
