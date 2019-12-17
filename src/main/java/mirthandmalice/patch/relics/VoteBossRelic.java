package mirthandmalice.patch.relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.screens.select.BossRelicSelectScreen;
import javassist.CtBehavior;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.util.MultiplayerHelper;

import java.util.ArrayList;

import static mirthandmalice.util.HandleMatchmaking.isHost;
import static mirthandmalice.util.MultiplayerHelper.partnerName;
import static mirthandmalice.MirthAndMaliceMod.*;

public class VoteBossRelic {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("BossRelic"));
    private static final String[] TEXT = uiStrings.TEXT;

    public static String votedRelic = null;
    public static String otherVotedRelic = null;

    public static AbstractRelic chosenRelic = null;

    @SpirePatch(
            clz = TreasureRoomBoss.class,
            method = "onPlayerEntry"
    )
    public static class ResetVoting
    {
        @SpirePrefixPatch
        public static void reset(TreasureRoomBoss __instance)
        {
            votedRelic = null;
            chosenRelic = null;
        }
    }

    @SpirePatch(
            clz = BossRelicSelectScreen.class,
            method = "open"
    )
    public static class TestOtherVote
    {
        @SpirePostfixPatch
        public static void checkValidity(BossRelicSelectScreen __instance, ArrayList<AbstractRelic> relics)
        {
            if (otherVotedRelic != null) {
                boolean valid = false;
                for (AbstractRelic r : relics)
                {
                    if (r.relicId.equals(otherVotedRelic))
                    {
                        valid = true;
                        break;
                    }
                }
                if (!valid)
                {
                    otherVotedRelic = null;
                }
            }
        }
    }

    @SpirePatch(
            clz = BossRelicSelectScreen.class,
            method = "render"
    )
    public static class RenderVotes
    {
        private static float rotation = 0;
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = { "r" }
        )
        public static void renderAura(BossRelicSelectScreen __instance, SpriteBatch sb, AbstractRelic r)
        {
            sb.setColor(Color.WHITE);
            rotation += Gdx.graphics.getDeltaTime() * 0.5f;
            if (r.relicId.equals(votedRelic))
            {
                sb.draw(ImageMaster.EXHAUST_L, r.currentX - ImageMaster.EXHAUST_L.packedWidth / 2.0f, r.currentY - ImageMaster.EXHAUST_L.packedHeight / 2.0f, ImageMaster.EXHAUST_L.packedWidth / 2.0f, ImageMaster.EXHAUST_L.packedHeight / 2.0f, ImageMaster.EXHAUST_L.packedWidth, ImageMaster.EXHAUST_L.packedHeight, r.scale, r.scale, rotation);
            }
            if (r.relicId.equals(otherVotedRelic))
            {
                sb.draw(ImageMaster.EXHAUST_L, r.currentX - ImageMaster.EXHAUST_L.packedWidth / 2.0f, r.currentY - ImageMaster.EXHAUST_L.packedHeight / 2.0f, ImageMaster.EXHAUST_L.packedWidth / 2.0f, ImageMaster.EXHAUST_L.packedHeight / 2.0f, ImageMaster.EXHAUST_L.packedWidth, ImageMaster.EXHAUST_L.packedHeight, r.scale, r.scale, rotation + MathUtils.PI);
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractRelic.class, "render");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "bossObtainLogic"
    )
    public static class OnTryObtain
    {
        @SpirePrefixPatch
        public static SpireReturn voteNotTake(AbstractRelic __instance)
        {
            if (AbstractDungeon.player instanceof MirthAndMalice && MultiplayerHelper.active)
            {
                if (__instance.equals(chosenRelic))
                {
                    votedRelic = null;
                    otherVotedRelic = null;
                    chosenRelic = null;
                    return SpireReturn.Continue();
                }
                if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.BOSS_REWARD)
                {
                    if (!__instance.relicId.equals(votedRelic))
                    {
                        votedRelic = __instance.relicId;
                        MultiplayerHelper.sendP2PMessage(partnerName + TEXT[2] + __instance.name + TEXT[3]);
                    }
                    MultiplayerHelper.sendP2PString("boss_relic_vote" + votedRelic);
                    if (isHost && otherVotedRelic != null)
                    {
                        handleSelection();
                    }
                    return SpireReturn.Return(null);
                }
            }
            return SpireReturn.Continue();
        }

        /*@SpireInsertPatch(
                locator = ForceLocator.class
        )
        public static void forceTakeChosen(AbstractRelic __instance)
        {
            if (__instance.equals(chosenRelic))
            {
                if (AbstractDungeon.getCurrRoom() instanceof TreasureRoomBoss)
                {
                    if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.BOSS_REWARD)
                    {
                        ((TreasureRoomBoss) AbstractDungeon.getCurrRoom()).chest.open(false); //I don't think anything actually cares about this parameter lol
                    }
                }
                __instance.hb.hovered = true;
                InputHelper.justClickedLeft = true;
            }
        }*/

        /*private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractRelic.class, "relicId");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
        private static class ForceLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(Hitbox.class, "hovered");
                return new int[] { LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[2] };
            }
        }*/
    }


    private static void handleSelection()
    {
        if (votedRelic != null && otherVotedRelic != null)
        {
            if (votedRelic.equals(otherVotedRelic)) {
                stopBossRelicChooseTimer();

                boolean success = false;

                for (AbstractRelic r : AbstractDungeon.bossRelicScreen.relics)
                {
                    if (r.relicId.equals(votedRelic))
                    {
                        chosenRelic = r;
                        r.bossObtainLogic();
                        success = true;

                        MultiplayerHelper.sendP2PString("boss_relic_choose" + r.relicId);
                        MultiplayerHelper.sendP2PMessage(TEXT[0]);

                        break;
                    }
                }
                if (!success)
                {
                    logger.error("A relic that doesn't exist won the vote??");
                }
            }
            else {
                startBossRelicChooseTimer(60);
                MultiplayerHelper.sendP2PMessage(TEXT[1]);
            }
        }
    }

    public static void receiveVote(String id)
    {
        otherVotedRelic = id;
        if (isHost && votedRelic != null)
        {
            handleSelection();
        }
    }
    public static void chooseRelic(String id)
    {
        for (AbstractRelic r : AbstractDungeon.bossRelicScreen.relics)
        {
            if (r.relicId.equals(id))
            {
                chosenRelic = r;
                r.bossObtainLogic();
                break;
            }
        }
        if (chosenRelic == null)
        {
            logger.error("A relic that doesn't exist won the vote??");
        }
    }
    public static void resolveConflict()
    {
        if (isHost)
        {
            MultiplayerHelper.sendP2PMessage("Resolving...");

            String choose;
            if (MathUtils.randomBoolean() && otherVotedRelic != null)
            {
                choose = otherVotedRelic;
            }
            else
            {
                choose = votedRelic;
            }

            for (AbstractRelic r : AbstractDungeon.bossRelicScreen.relics)
            {
                if (r.relicId.equals(choose))
                {
                    chosenRelic = r;
                    r.bossObtainLogic();
                    break;
                }
            }
            if (chosenRelic == null)
            {
                logger.error("A relic that doesn't exist won the vote??");
            }
            else
            {
                MultiplayerHelper.sendP2PString("boss_relic_choose" + chosenRelic.relicId);
                MultiplayerHelper.sendP2PMessage(TEXT[0]);
            }
        }
    }
}
