package mirthandmalice.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNetworking;
import com.codedisaster.steamworks.SteamNetworkingCallback;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSleepEffect;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSleepScreenCoverEffect;
import mirthandmalice.abstracts.ReceiveSignalCardsAction;
import mirthandmalice.actions.character.*;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.card_use.PlayCardCheck;
import mirthandmalice.patch.combat.RequireDoubleEndTurn;
import mirthandmalice.patch.combat.ShowHover;
import mirthandmalice.patch.deck_changes.ReportObtainCard;
import mirthandmalice.patch.deck_changes.ReportRemoveCard;
import mirthandmalice.patch.deck_changes.ReportUpgradeCard;
import mirthandmalice.patch.events.BonfireSpirits;
import mirthandmalice.patch.events.GenericEventVoting;
import mirthandmalice.patch.events.RoomEventVoting;
import mirthandmalice.patch.map.BossRoomVoting;
import mirthandmalice.patch.map.MapRoomVoting;
import mirthandmalice.patch.relics.GiryaPatch;
import mirthandmalice.patch.relics.ReportBottling;
import mirthandmalice.patch.relics.ReportPurchase;
import mirthandmalice.patch.relics.VoteBossRelic;
import mirthandmalice.patch.resting.SyncResting;
import mirthandmalice.patch.resting.TakeRedKey;
import mirthandmalice.patch.rewards.ObtainRewards;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static com.megacrit.cardcrawl.characters.AbstractPlayer.HOVER_CARD_Y_POSITION;
import static mirthandmalice.patch.combat.PotionUse.*;
import static mirthandmalice.patch.combat.ShowHover.otherHoveredCard;
import static mirthandmalice.patch.rewards.ObtainPotions.*;
import static mirthandmalice.MirthAndMaliceMod.*;
import static mirthandmalice.util.HandleMatchmaking.*;

//Handles all multiplayer post-game start. For setting up multiplayer, see HandleMatchmaking.java as well as UseMultiplayerQueue.java
public class MultiplayerHelper implements SteamNetworkingCallback {
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final float EXTRA_DRAG_PUSH = 100.0f * Settings.scale;

    private static final int defaultChannel = 1;

    public static SteamNetworking communication;
    public static MultiplayerHelper callback;
    public static SteamID currentPartner;
    public static String partnerName;

    public static boolean active;
    //When second player joins, send signal to start game start timer
    //Upon game start, host then sends necessary information (seed, which player is which character) to second player.

    private static ByteBuffer packetSendBuffer = ByteBuffer.allocateDirect(4096);
    private static ByteBuffer packetReceiveBuffer = ByteBuffer.allocateDirect(4096);

    public static ArrayList<String> otherPlayerPotions = new ArrayList<>();
    public static int otherPlayerGold = -1; //set at start of run

    private static float ping = 0;
    public static int lastPing = 0;

    public static void init()
    {
        dispose();

        callback = new MultiplayerHelper();
        communication = new SteamNetworking(callback, SteamNetworking.API.Client);
    }

    public static boolean getIsMirthFromOther(boolean other)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (((MirthAndMalice) AbstractDungeon.player).isMirth)
            {
                if (FULL_DEBUG)
                    logger.info("getIsMirthFromOther: I am Mirth. Looking for self: " + !other + ". Result: " + !other);
                return !other;
            }
            else
            {
                if (FULL_DEBUG)
                    logger.info("getIsMirthFromOther: I am not Mirth. Looking for self: " + !other + ". Result: " + other);
                return other;
            }
        }
        else
        {
            return true;
        }
    }
    public static boolean getIsOtherFromMirth(boolean isMirth)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            if (isMirth)
            {
                if (FULL_DEBUG)
                    logger.info("getIsOtherFromMirth: Looking for Mirth. I am Mirth: " + ((MirthAndMalice) AbstractDungeon.player).isMirth + ". Is Other Result: " + !((MirthAndMalice) AbstractDungeon.player).isMirth);
                return !((MirthAndMalice) AbstractDungeon.player).isMirth;
            }
            else
            {
                if (FULL_DEBUG)
                    logger.info("getIsOtherFromMirth: Looking for Malice. I am Mirth: " + ((MirthAndMalice) AbstractDungeon.player).isMirth + ". Is Other Result: " + ((MirthAndMalice) AbstractDungeon.player).isMirth);
                return ((MirthAndMalice) AbstractDungeon.player).isMirth;
            }
        }
        else
        {
            return false;
        }
    }

    public static String getName(boolean other)
    {
        if (other)
            return partnerName;
        return AbstractDungeon.player.name;
    }

    public static void dispose()
    {
        if (communication != null)
        {
            communication.dispose();
        }
        callback = null;
    }

    public static void sendP2PString(SteamID dest, String msg)
    {
        if (communication != null && dest != null && dest.isValid())
        {
            try
            {
                packetSendBuffer.clear();

                packetSendBuffer.put(msg.getBytes(CHARSET));

                packetSendBuffer.flip();

                if (!msg.equals("ping") && !msg.contains("hover") && !msg.startsWith("drag")) //these messages spam too much and are unhelpful
                    logger.info("Sending P2P message: " + msg);

                communication.sendP2PPacket(dest, packetSendBuffer, SteamNetworking.P2PSend.Reliable, defaultChannel);
                currentPartner = dest;
            }
            catch (Exception e)
            {
                logger.error(e.getMessage());
            }
        }
    }
    public static void sendP2PString(String msg)
    {
        if (active)
            sendP2PString(currentPartner, msg);
    }
    public static void sendP2PMessage(String msg)
    {
        if (active)
        {
            if (chat != null)
            {
                chat.receiveMessage(msg);
            }
            String finalMessage = "chat_message" + msg;

            sendP2PString(currentPartner, finalMessage);
        }
    }

    public static void readPostUpdate()
    {
        try
        {
            if (communication != null)
            {
                if (communication.isP2PPacketAvailable(defaultChannel) > 0)
                {
                    packetReceiveBuffer.clear();

                    SteamID sender = new SteamID();

                    if (communication.readP2PPacket(sender, packetReceiveBuffer, defaultChannel) > 0)
                    {
                        //int received = packetReceiveBuffer.limit();
                        //logger.info("Received " + received + " bytes from " + sender.getAccountID());

                        String msg = CHARSET.decode(packetReceiveBuffer).toString();
                        if (!msg.equals("ping")) //don't want to spam logger with the pings
                            logger.info("Received message: " + msg);

                        processMessage(sender, msg);
                    }
                }
                ping += Gdx.graphics.getDeltaTime();

                if (ping > 5)
                {
                    ping = 0;
                    lastPing = 999;
                    //sendP2PString("ping");
                }
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
    }

    private static void processMessage(SteamID sender, String msg)
    {
        //Note to future self: Use 3 character code as first three characters of message so that you can just use a switch.
        //Every time I add something to this I die inside a little, but I'm too lazy to fix all of the messages.

        if (msg.equals("ping"))
        {
            lastPing = MathUtils.floor(500 * (lastPing / 1000.0f + ping)); //convert to milliseconds average
            ping = 0;
            sendP2PString("ping");
        }
        else if (msg.startsWith("chat_message"))
        {
            msg = msg.substring(12);
            chat.receiveMessage(msg);
        }
        else if (msg.startsWith("other_play_card")) //Host played a card.
        {
            if (!tryOtherPlayCard(msg.substring(15)))
            {
                logger.error("Host's card play attempt failed. Storing in card attempt queue to be attempted at next card play.");
                PlayCardCheck.failedAttempts.addLast(msg.substring(15));
            }
        }
        else if (msg.startsWith("try_play_card")) //Player that isn't host played a card.
        {
            String args = msg.substring(13);
            if (tryOtherPlayCard(args))
            {
                //Send confirmation to play the card.
                sendP2PString("confirm_play_card" + args);
            }
            else
            {
                logger.error("Card play attempt failed. Storing in card attempt queue to be attempted at next card play.");
                PlayCardCheck.failedAttempts.addLast(args);
            }
        }
        else if (msg.startsWith("confirm_play_card"))
        {
            tryPlayCard(msg.substring(17));
        }
        else if (msg.startsWith("hover"))
        {
            hover(msg.substring(5));
        }
        else if (msg.startsWith("drag"))
        {
            drag(msg.substring(4));
        }
        else if (msg.equals("stophover"))
        {
            stopHover();
        }
        else if (msg.startsWith("use_potion")) //Host used a potion.
        {
            usePotion(msg.substring(10));
        }
        else if (msg.startsWith("try_use_potion")) //Not Host used a potion
        {
            String info = msg.substring(14);
            if (otherCanUsePotion(info))
            {
                otherUsePotion(info);
                sendP2PString("confirm_use_potion" + info);
            }
        }
        else if (msg.startsWith("confirm_use_potion"))
        {
            confirmUsePotion(msg.substring(18));
        }
        else if (msg.startsWith("update_potions"))
        {
            receivePotionUpdate(msg.substring(14));
        }
        else if (msg.startsWith("signalcard"))
        {
            ReceiveSignalCardsAction.receiveCardString(msg.substring(10));
        }
        else if (msg.startsWith("signal"))
        {
            WaitForSignalAction.signal += 1;
            if (msg.length() > 6)
            {
                processMessage(sender, msg.substring(6));
            }
        }
        else if (msg.equals("end_turn"))
        {
            RequireDoubleEndTurn.otherPlayerEndTurn();
        }
        else if (msg.equals("full_end_turn"))
        {
            RequireDoubleEndTurn.fullEndTurn();
        }
        else if (msg.startsWith("exhaust"))
        {
            String[] args = msg.substring(7).split(" ");
            if (args.length == 2)
            {
                int index = Integer.parseInt(args[1]);
                if (index >= 0)
                {
                    switch (args[0])
                    {
                        case "draw":
                            if (index <= AbstractDungeon.player.drawPile.group.size())
                            {
                                AbstractCard toExhaust = AbstractDungeon.player.drawPile.group.get(index);
                                AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(toExhaust, AbstractDungeon.player.drawPile, true));
                            }
                            break;
                        case "other_draw":
                            if (AbstractDungeon.player instanceof MirthAndMalice && index <= ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.group.size())
                            {
                                AbstractCard toExhaust = ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw.group.get(index);
                                AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(toExhaust, ((MirthAndMalice) AbstractDungeon.player).otherPlayerDraw, true));
                            }
                            break;
                        case "discard":
                            if (index <= AbstractDungeon.player.discardPile.group.size())
                            {
                                AbstractCard toExhaust = AbstractDungeon.player.discardPile.group.get(index);
                                AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(toExhaust, AbstractDungeon.player.discardPile, true));
                            }
                            break;
                        case "other_discard":
                            if (AbstractDungeon.player instanceof MirthAndMalice && index <= ((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard.group.size())
                            {
                                AbstractCard toExhaust = ((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard.group.get(index);
                                AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(toExhaust, ((MirthAndMalice) AbstractDungeon.player).otherPlayerDiscard, true));
                            }
                            break;
                        case "hand":
                            if (index <= AbstractDungeon.player.hand.group.size())
                            {
                                AbstractCard toExhaust = AbstractDungeon.player.hand.group.get(index);
                                AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(toExhaust, AbstractDungeon.player.hand, true));
                            }
                            break;
                        case "other_hand":
                            if (AbstractDungeon.player instanceof MirthAndMalice && index <= ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.group.size())
                            {
                                AbstractCard toExhaust = ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.group.get(index);
                                AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(toExhaust, ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand, true));
                            }
                            break;
                    }
                }
            }
        }
        else if (msg.startsWith("other_discard"))
        {
            if (AbstractDungeon.player instanceof MirthAndMalice)
            {
                int index = Integer.parseInt(msg.substring(13));
                if (index >= 0 && index < ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.group.size())
                {
                    AbstractDungeon.actionManager.addToTop(new DiscardSpecificOtherPlayerCardAction(((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.group.get(index)));
                }
            }
        }
        else if (msg.startsWith("draw"))
        {
            int amt = Integer.parseInt(msg.substring(4));

            if (amt >= 1)
            {
                AbstractDungeon.actionManager.addToTop(new DrawCardAction(AbstractDungeon.player, amt));
            }
        }
        else if (msg.equals("hand_select")) //other player has opened a hand select screen
        {
            logger.info("Other played opened hand select.");
            logger.info("Clearing card queue.");

            if (AbstractDungeon.player instanceof MirthAndMalice)
            {
                AbstractDungeon.actionManager.cardQueue.removeIf((i)->((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.contains(i.card) || AbstractDungeon.player.hand.contains(i.card));
            }
            else
            {
                AbstractDungeon.actionManager.cardQueue.removeIf((i)->AbstractDungeon.player.hand.contains(i.card)); //this doesn't really make sense but I'm gonna put it here anyways.
            }
            AbstractDungeon.actionManager.addToTop(new HandCardSelectAction());
        }
        else if (msg.startsWith("hand_select_indexes"))
        {
            String[] data = msg.substring(19).split(" ");
            HandCardSelectAction.setFinalPositions(data);
        }
        else if (msg.startsWith("discover_card"))
        {
            AbstractDungeon.actionManager.addToTop(new MakeTempCardInOtherHandAction(MessageHelper.cardFromInfo(msg.substring(13))));
        }
        else if (msg.startsWith("other_obtain_card"))
        {
            ReportObtainCard.receiveOtherObtainCard(msg.substring(17));
        }
        else if (msg.startsWith("other_upgrade_card"))
        {
            ReportUpgradeCard.receiveOtherUpgradeCard(msg.substring(18));
        }
        else if (msg.startsWith("other_remove_card"))
        {
            ReportRemoveCard.receiveOtherRemoveCard(msg.substring(17));
        }
        else if (msg.startsWith("room_option_choose"))
        {
            RoomEventVoting.selectOption(Integer.parseInt(msg.substring(18)));
        }
        else if (msg.startsWith("room_option"))
        {
            RoomEventVoting.receiveVote(Integer.parseInt(msg.substring(11)));
        }
        else if (msg.startsWith("generic_option_choose"))
        {
            GenericEventVoting.selectOption(Integer.parseInt(msg.substring(21)));
        }
        else if (msg.startsWith("generic_option"))
        {
            GenericEventVoting.receiveVote(Integer.parseInt(msg.substring(14)));
        }
        else if (msg.startsWith("vote_node"))
        {
            String[] params = msg.substring(9).split(" ");

            if (params.length == 2)
            {
                MapRoomVoting.receiveVote(params);
            }
            else
            {
                logger.error("ERROR: Invalid map node vote.");
            }
        }
        else if (msg.startsWith("choose_node"))
        {
            String[] params = msg.substring(11).split(" ");

            if (params.length == 2)
            {
                MapRoomVoting.setNode(params);
            }
            else
            {
                logger.error("ERROR: Invalid map node vote.");
            }
        }
        else if (msg.equals("waiting_boss"))
        {
            BossRoomVoting.otherWaitingForBoss = true;
        }
        else if (msg.equals("enter_boss"))
        {
            BossRoomVoting.waitingForBoss = false;
            BossRoomVoting.otherWaitingForBoss = false;

            BossRoomVoting.enterBoss();
        }
        else if (msg.startsWith("claim_reward"))
        {
            ObtainRewards.claimReward(msg.substring(12));
        }
        else if (msg.startsWith("try_purchase_relic")) //second player attempted to purchase relic
        {
            String id = msg.substring(18);
            AbstractRelic tryPurchase = ReportPurchase.forcePurchase(id);
            if (tryPurchase != null) //if true, the relic attempting to be purchased was found in the shop and successfuly obtained.
            {
                MultiplayerHelper.sendP2PMessage(MultiplayerHelper.partnerName + HandleMatchmaking.TEXT[6] + RelicLibrary.getRelic(id).name + ".");
                MultiplayerHelper.sendP2PString("confirm_purchase_relic" + id);
            }
        }
        else if (msg.startsWith("confirm_purchase_relic")) //receiving confirmation from host to buy a relic
        {
            ReportPurchase.normalPurchase(msg.substring(22));
        }
        else if (msg.startsWith("purchase_relic")) //host bought a relic
        {
            ReportPurchase.forcePurchase(msg.substring(14));
        }
        else if (msg.startsWith("boss_relic_vote"))
        {
            VoteBossRelic.receiveVote(msg.substring(15));
        }
        else if (msg.startsWith("boss_relic_choose"))
        {
            VoteBossRelic.chooseRelic(msg.substring(17));
        }
        else if (msg.startsWith("bottle"))
        {
            char bottleChar = msg.charAt(6);
            int index = Integer.parseInt(msg.substring(7));
            if (index >= 0)
            {
                ReportBottling.receiveBottling(bottleChar, index);
            }
        }
        else if (msg.startsWith("claim_potion"))
        {
            removePotionReward(msg.substring(12));
        }
        else if (msg.startsWith("try_claim_potion"))
        {
            String id = msg.substring(16);
            if (removePotionReward(id))
            {
                sendP2PString("confirm_claim_potion" + id);
            }
            else
            {
                sendP2PString("confirm_lose_potion" + id);
            }
        }
        else if (msg.startsWith("confirm_claim_potion"))
        {
            confirmClaimPotion(msg.substring(20));
        }
        else if (msg.startsWith("confirm_lose_potion"))
        {
            confirmLosePotion(msg.substring(19));
        }
        else if (msg.startsWith("gold"))
        {
            otherPlayerGold = Integer.parseInt(msg.substring(4));
            logger.info("Other player new gold value: " + otherPlayerGold);
        }
        else if (msg.equals("gain_gold"))
        {
            logger.info("Other player gained gold.");
            for (AbstractRelic r : AbstractDungeon.player.relics)
            {
                r.onGainGold();
            }
        }
        else if (msg.equals("lose_gold"))
        {
            logger.info("Other player lost gold.");
            for (AbstractRelic r : AbstractDungeon.player.relics)
            {
                r.onLoseGold();
            }
        }
        else if (msg.equals("rest"))
        {
            if (AbstractDungeon.player != null)
            {
                SyncResting.otherPlayerRest = true;
                CardCrawlGame.sound.play("SLEEP_BLANKET");
                AbstractDungeon.effectList.add(new CampfireSleepEffect());
                for(int i = 0; i < 20; ++i) {
                    AbstractDungeon.topLevelEffects.add(new CampfireSleepScreenCoverEffect());
                }
            }
        }
        else if (msg.startsWith("crrng")) //card random rng
        {
            int amt = Integer.parseInt(msg.substring(5));
            if (amt >= 0)
            {
                while (AbstractDungeon.cardRandomRng.counter < amt)
                {
                    AbstractDungeon.cardRandomRng.random();
                }
            }
        }
        else if (msg.equals("LIFT"))
        {
            if (AbstractDungeon.player != null)
            {
                GiryaPatch.doLift();
            }
        }
        else if (msg.equals("recall"))
        {
            TakeRedKey.obtainKey();
        }
        else if (msg.startsWith("lose_max_hp"))
        {
            if (AbstractDungeon.player != null)
            {
                AbstractDungeon.player.decreaseMaxHealth(Integer.parseInt(msg.substring(11)));
            }
        }
        else if (msg.startsWith("bonfire"))
        {
            BonfireSpirits.receiveReward(msg.substring(7));
        }
        else if (msg.equals("start_game"))
        {
            //Host has send start game message, and both parties are properly connected.
            sendP2PString("leave");
            startSetupGame();
        }
        else if (msg.equals("leave"))
        {
            HandleMatchmaking.leave();
        }
        else if (msg.equals("stop"))
        {
            active = false;
            stopGameStart();
            chat.receiveMessage(HandleMatchmaking.TEXT[5]);
            logger.info("Other player left.");
            currentPartner = null;
        }
        else if (msg.startsWith("trial"))
        {
            msg = msg.substring(5);
            Settings.specialSeed = Long.valueOf(msg);
            Settings.isTrial = true;
        }
        else if (msg.startsWith("ascension"))
        {
            int level = Integer.parseInt(msg.substring(9));
            if (level != 0)
            {
                AbstractDungeon.isAscensionMode = true;
            }
            AbstractDungeon.ascensionLevel = level;
        }
        else if (msg.startsWith("seedset"))
        {
            msg = msg.substring(7);
            Settings.seed = Long.valueOf(msg);
            Settings.seedSet = true;
        }
        else if (msg.startsWith("seed"))
        {
            msg = msg.substring(4);
            Settings.seed = Long.valueOf(msg);
        }
        else if (msg.startsWith("success"))
        {
            active = true;
            currentPartner = sender;
            partnerName = msg.substring(7);

            if (HandleMatchmaking.isHost)
            {
                lobbyMenu.displayLobbyInfo(new ActiveLobbyData(matchmaking.getLobbyData(currentLobbyID, HandleMatchmaking.lobbyNameKey), Integer.parseInt(matchmaking.getLobbyData(currentLobbyID, HandleMatchmaking.lobbyAscensionKey)), CardCrawlGame.playerName, partnerName, matchmaking.getLobbyData(currentLobbyID, hostIsMirthKey).equals(metadataTrue)));

                //chat.receiveMessage(HandleMatchmaking.TEXT[4]);
                logger.info("Connection established.");
                sendP2PString("ping");
                ping = 0;
                lastPing = 0;
            }
            else
            {
                lobbyMenu.displayLobbyInfo(new ActiveLobbyData(matchmaking.getLobbyData(currentLobbyID, HandleMatchmaking.lobbyNameKey), Integer.parseInt(matchmaking.getLobbyData(currentLobbyID, HandleMatchmaking.lobbyAscensionKey)), partnerName, CardCrawlGame.playerName, matchmaking.getLobbyData(currentLobbyID, hostIsMirthKey).equals(metadataTrue)));
                sendP2PString("success" + CardCrawlGame.playerName);
            }
        }
        else if (msg.startsWith("connect"))
        {
            active = true;
            currentPartner = sender;
            partnerName = msg.substring(7);
            sendP2PString("success" + CardCrawlGame.playerName);


            if (HandleMatchmaking.isHost)
            {
                lobbyMenu.displayLobbyInfo(new ActiveLobbyData(matchmaking.getLobbyData(currentLobbyID, HandleMatchmaking.lobbyNameKey), Integer.parseInt(matchmaking.getLobbyData(currentLobbyID, HandleMatchmaking.lobbyAscensionKey)), CardCrawlGame.playerName, partnerName, matchmaking.getLobbyData(currentLobbyID, hostIsMirthKey).equals(metadataTrue)));
            }
            else
            {
                lobbyMenu.displayLobbyInfo(new ActiveLobbyData(matchmaking.getLobbyData(currentLobbyID, HandleMatchmaking.lobbyNameKey), Integer.parseInt(matchmaking.getLobbyData(currentLobbyID, HandleMatchmaking.lobbyAscensionKey)), partnerName, CardCrawlGame.playerName, matchmaking.getLobbyData(currentLobbyID, hostIsMirthKey).equals(metadataTrue)));
            }
        }
        else if (msg.equals("hide"))
        {
            lobbyMenu.hide();
        }
    }

    public static void reset()
    {
        active = false;
        communication.closeP2PSessionWithUser(currentPartner);
        currentPartner = null;
        partnerName = "";
        logger.info("Disconnected.");
    }


    @Override
    public void onP2PSessionConnectFail(SteamID steamID, SteamNetworking.P2PSessionError p2pSessionError) {
        logger.error("Failed to connect to lobby partner.");
        logger.error(p2pSessionError);
        //currentPartner = null;
        sendP2PString(steamID, "connect" + CardCrawlGame.playerName);
    }

    @Override
    public void onP2PSessionRequest(SteamID steamID) {
        logger.info("Received session request from " + steamID.getAccountID());
        if (HandleMatchmaking.inLobby(steamID) || steamID.equals(currentPartner))
        {
            logger.info("Accepted session request.");
            currentPartner = steamID;
            communication.acceptP2PSessionWithUser(steamID);
        }
    }


    private static void hover(String args)
    {
        if (active)
        {
            if (otherHoveredCard != null)
            {
                stopHover();
            }

            int index = Integer.parseInt(args);

            if (index >= 0 && index < ((MirthAndMalice)AbstractDungeon.player).otherPlayerHand.size())
            {
                otherHoveredCard = ((MirthAndMalice)AbstractDungeon.player).otherPlayerHand.group.get(index);
                otherHoveredCard.hover();
                otherHoveredCard.current_y = HOVER_CARD_Y_POSITION;
                otherHoveredCard.target_y = HOVER_CARD_Y_POSITION;
                otherHoveredCard.setAngle(0.0F, true);
                ((MirthAndMalice)AbstractDungeon.player).otherPlayerHand.hoverCardPush(otherHoveredCard);
            }
        }
    }
    private static void drag(String args)
    {
        if (active)
        {
            if (!args.isEmpty())
            {
                if (otherHoveredCard != null)
                {
                    stopHover();
                }

                int index = Integer.parseInt(args);

                if (index >= 0 && index < ((MirthAndMalice)AbstractDungeon.player).otherPlayerHand.size())
                {
                    otherHoveredCard = ((MirthAndMalice)AbstractDungeon.player).otherPlayerHand.group.get(index);
                    otherHoveredCard.hover();
                    otherHoveredCard.current_y = HOVER_CARD_Y_POSITION;
                    otherHoveredCard.target_y = HOVER_CARD_Y_POSITION;
                    otherHoveredCard.setAngle(0.0F, true);
                    ((MirthAndMalice)AbstractDungeon.player).otherPlayerHand.hoverCardPush(otherHoveredCard);
                }
            }

            otherHoveredCard.target_y += EXTRA_DRAG_PUSH;
            ShowHover.isDragging = true;
        }
    }
    private static void stopHover()
    {
        if (otherHoveredCard != null) {
            otherHoveredCard.unhover();

            otherHoveredCard = null;
            ShowHover.isDragging = false;

            ((MirthAndMalice) AbstractDungeon.player).otherPlayerHand.refreshHandLayout();
        }
    }


    public static boolean tryOtherPlayCard(String args)
    {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            MirthAndMalice p = (MirthAndMalice)AbstractDungeon.player;
            String[] params = args.split(" ");

            if (params.length == 5)
            {
                logger.info("Other player played a card.");
                int cardIndex = Integer.parseInt(params[0]);
                String cardID = params[1];
                int targetIndex = Integer.parseInt(params[2]);
                float x = Float.parseFloat(params[3]);
                float y = Float.parseFloat(params[4]);

                if (cardIndex >= 0 && cardIndex < p.otherPlayerHand.size())
                {
                    AbstractCard toPlay = p.otherPlayerHand.group.get(cardIndex);

                    if (toPlay.cardID.equals(cardID))
                    {
                        p.otherPlayerHand.removeCard(toPlay);

                        AbstractMonster target = null;
                        if (targetIndex >= 0 && targetIndex < AbstractDungeon.getMonsters().monsters.size()) {
                            target = AbstractDungeon.getMonsters().monsters.get(targetIndex);
                            if (target != null) {
                                toPlay.calculateCardDamage(target);
                            }
                        }

                        //AbstractDungeon.player.limbo.addToBottom(toPlay);
                        toPlay.target_x = toPlay.current_x = x;
                        toPlay.target_y = toPlay.current_y = y;

                        AbstractDungeon.actionManager.cardQueue.add(new OtherPlayerCardQueueItem(toPlay, target));
                        return true;
                    }
                    else
                    {
                        logger.error("ERROR: Specified index does not have correct ID. " + cardID + " != " + toPlay.cardID);
                    }
                }
                else
                {
                    logger.error("ERROR: Attempted to play card with invalid index.");
                }
            }
        }
        else
        {
            logger.info("ERROR: Received invalid attempt to play a card.");
        }
        return false;
    }

    private static void tryPlayCard(String args)
    {
        String[] params = args.split(" ");

        if (params.length == 5)
        {
            logger.info("Received confirmation to play a card.");
            int cardIndex = Integer.parseInt(params[0]);
            int targetIndex = Integer.parseInt(params[2]);
            float x = Float.parseFloat(params[3]);
            float y = Float.parseFloat(params[4]);

            if (cardIndex >= 0 && cardIndex < AbstractDungeon.player.hand.size())
            {
                AbstractCard toPlay = AbstractDungeon.player.hand.group.get(cardIndex);

                AbstractMonster target = null;
                if (targetIndex >= 0 && targetIndex < AbstractDungeon.getMonsters().monsters.size()) {
                    target = AbstractDungeon.getMonsters().monsters.get(targetIndex);
                    if (target != null) {
                        toPlay.calculateCardDamage(target);
                    }
                }

                //AbstractDungeon.player.limbo.addToBottom(toPlay);
                toPlay.target_x = toPlay.current_x = x;
                toPlay.target_y = toPlay.current_y = y;

                AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(toPlay, target));
            }
            else
            {
                logger.error("ERROR: Attempted to play card with invalid index.");
            }
        }
    }
}