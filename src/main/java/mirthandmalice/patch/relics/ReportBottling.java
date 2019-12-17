package mirthandmalice.patch.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.BottledFlame;
import com.megacrit.cardcrawl.relics.BottledLightning;
import com.megacrit.cardcrawl.relics.BottledTornado;
import javassist.CtBehavior;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.MultiplayerHelper;

import static mirthandmalice.util.MultiplayerHelper.partnerName;
import static mirthandmalice.MirthAndMaliceMod.chat;
import static mirthandmalice.MirthAndMaliceMod.makeID;

public class ReportBottling {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("Bottling"));
    private static final String[] TEXT = uiStrings.TEXT;

    @SpirePatch(
            clz = BottledFlame.class,
            method = "update"
    )
    public static class Flame
    {
        public static SpireField<AbstractCard> otherPlayerBottled = new SpireField<>(null);

        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void onBottle(BottledFlame __instance)
        {
            if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
            {
                MultiplayerHelper.sendP2PString("bottlef" + AbstractDungeon.player.masterDeck.group.indexOf(__instance.card));
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "inBottleFlame");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = BottledLightning.class,
            method = "update"
    )
    public static class Lightning
    {
        public static SpireField<AbstractCard> otherPlayerBottled = new SpireField<>(null);

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void onBottle(BottledLightning __instance)
        {
            if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
            {
                MultiplayerHelper.sendP2PString("bottlel" + AbstractDungeon.player.masterDeck.group.indexOf(__instance.card));
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "inBottleLightning");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = BottledTornado.class,
            method = "update"
    )
    public static class Tornado
    {
        public static SpireField<AbstractCard> otherPlayerBottled = new SpireField<>(null);

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void onBottle(BottledTornado __instance)
        {
            if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
            {
                MultiplayerHelper.sendP2PString("bottlet" + AbstractDungeon.player.masterDeck.group.indexOf(__instance.card));
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "inBottleTornado");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = BottledFlame.class,
            method = "onUnequip"
    )
    @SpirePatch(
            clz = BottledLightning.class,
            method = "onUnequip"
    )
    @SpirePatch(
            clz = BottledTornado.class,
            method = "onUnequip"
    )
    public static class UnbottleOtherCards
    {
        @SpirePrefixPatch
        public static void remove(AbstractRelic __instance)
        {
            if (AbstractDungeon.player instanceof MirthAndMalice)
            {
                try {
                    switch (__instance.relicId)
                    {
                        case BottledFlame.ID:
                            if (Flame.otherPlayerBottled.get(__instance) != null && ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.contains(Flame.otherPlayerBottled.get(__instance)))
                            {
                                Flame.otherPlayerBottled.get(__instance).inBottleFlame = false;
                            }
                            break;
                        case BottledLightning.ID:
                            if (Lightning.otherPlayerBottled.get(__instance) != null && ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.contains(Lightning.otherPlayerBottled.get(__instance)))
                            {
                                Lightning.otherPlayerBottled.get(__instance).inBottleLightning = false;
                            }
                            break;
                        case BottledTornado.ID:
                            if (Tornado.otherPlayerBottled.get(__instance) != null && ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.contains(Tornado.otherPlayerBottled.get(__instance)))
                            {
                                Tornado.otherPlayerBottled.get(__instance).inBottleTornado = false;
                            }
                            break;
                    }
                }
                catch (Exception e)
                {
                    //smh who would cause an exception like this
                }
            }
        }
    }

    public static void receiveBottling(char bottle, int index)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            AbstractCard c = ((MirthAndMalice) AbstractDungeon.player).otherPlayerMasterDeck.group.get(index);
            switch (bottle)
            {
                case 'f':
                    chat.receiveMessage(partnerName + TEXT[0] + c.name + TEXT[1] + RelicLibrary.getRelic(BottledFlame.ID).name + TEXT[2]);
                    c.inBottleFlame = true;
                    for (int i = AbstractDungeon.player.relics.size() - 1; i >= 0; --i)
                    {
                        if (AbstractDungeon.player.relics.get(i).relicId.equals(BottledFlame.ID))
                        {
                            Flame.otherPlayerBottled.set(AbstractDungeon.player.relics.get(i), c);
                            break;
                        }
                    }
                    break;
                case 'l':
                    chat.receiveMessage(partnerName + TEXT[0] + c.name + TEXT[1] + RelicLibrary.getRelic(BottledLightning.ID).name + TEXT[2]);
                    c.inBottleLightning = true;
                    for (int i = AbstractDungeon.player.relics.size() - 1; i >= 0; --i)
                    {
                        if (AbstractDungeon.player.relics.get(i).relicId.equals(BottledLightning.ID))
                        {
                            Lightning.otherPlayerBottled.set(AbstractDungeon.player.relics.get(i), c);
                            break;
                        }
                    }
                    break;
                case 't':
                    chat.receiveMessage(partnerName + TEXT[0] + c.name + TEXT[1] + RelicLibrary.getRelic(BottledTornado.ID).name + TEXT[2]);
                    c.inBottleTornado = true;
                    for (int i = AbstractDungeon.player.relics.size() - 1; i >= 0; --i)
                    {
                        if (AbstractDungeon.player.relics.get(i).relicId.equals(BottledTornado.ID))
                        {
                            Tornado.otherPlayerBottled.set(AbstractDungeon.player.relics.get(i), c);
                            break;
                        }
                    }
                    break;
            }
        }
    }
}
