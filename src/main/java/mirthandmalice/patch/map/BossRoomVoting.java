package mirthandmalice.patch.map;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import javassist.CtBehavior;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.MultiplayerHelper;

public class BossRoomVoting {
    public static boolean waitingForBoss = false;
    public static boolean otherWaitingForBoss = false;

    @SpirePatch(
            clz = DungeonMap.class,
            method = "update"
    )
    public static class ReadyForBoss
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn myBodyIsReadyButIsYours(DungeonMap __instance)
        {
            if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
            {
                if (!waitingForBoss)
                {
                    MultiplayerHelper.sendP2PMessage(CardCrawlGame.playerName + " is ready to face the boss.");
                }
                if (otherWaitingForBoss)
                {
                    otherWaitingForBoss = false;
                    waitingForBoss = false;
                    MultiplayerHelper.sendP2PString("enter_boss");
                }
                else
                {
                    waitingForBoss = true;
                    if (!__instance.bossHb.hovered && !__instance.atBoss) {
                        ((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "bossNodeColor")).lerp(new Color(0.34F, 0.34F, 0.34F, 1.0F), Gdx.graphics.getDeltaTime() * 8.0F);
                    } else {
                        ReflectionHacks.setPrivate(__instance, DungeonMap.class, "bossNodeColor", MapRoomNode.AVAILABLE_COLOR.cpy());
                    }

                    ((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "bossNodeColor")).a = ((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "baseMapColor")).a;

                    MultiplayerHelper.sendP2PString("waiting_boss");

                    return SpireReturn.Return(null);
                }
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(MapRoomNode.class, "taken");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }


    public static void enterBoss()
    {
        AbstractDungeon.getCurrMapNode().taken = true;
        MapRoomNode node = AbstractDungeon.getCurrMapNode();

        for (MapEdge e : node.getEdges())
        {
            if (e != null)
                e.markAsTaken();
        }

        InputHelper.justClickedLeft = false;
        CardCrawlGame.music.fadeOutTempBGM();
        MapRoomNode bossNode = new MapRoomNode(-1, 15);
        bossNode.room = new MonsterRoomBoss();// 75
        AbstractDungeon.nextRoom = bossNode;// 76
        if (AbstractDungeon.pathY.size() > 1) {// 78
            AbstractDungeon.pathX.add(AbstractDungeon.pathX.get(AbstractDungeon.pathX.size() - 1));// 79
            AbstractDungeon.pathY.add(AbstractDungeon.pathY.get(AbstractDungeon.pathY.size() - 1) + 1);// 80
        } else {
            AbstractDungeon.pathX.add(1);// 82
            AbstractDungeon.pathY.add(15);// 83
        }

        AbstractDungeon.nextRoomTransitionStart();// 85
        AbstractDungeon.dungeonMapScreen.map.bossHb.hovered = false;// 86
    }
}
