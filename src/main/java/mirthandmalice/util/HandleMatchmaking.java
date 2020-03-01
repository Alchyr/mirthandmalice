package mirthandmalice.util;

import basemod.BaseMod;
import com.codedisaster.steamworks.*;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.ui.LobbyMenu;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static mirthandmalice.util.MultiplayerHelper.CHARSET;
import static mirthandmalice.util.MultiplayerHelper.currentPartner;
import static mirthandmalice.MirthAndMaliceMod.*;

public class HandleMatchmaking implements SteamMatchmakingCallback {
    private final ByteBuffer chatMessage = ByteBuffer.allocateDirect(4096);

    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("Matchmaking"));
    public static final String[] TEXT = uiStrings.TEXT;

    public static final String lobbyNameKey = "name";
    public static final String hostIsMirthKey = "host_is_mirth";
    public static final String lobbyPublicKey = "is_public";
    public static final String lobbyPasswordKey = "password";
    public static final String lobbyAscensionKey = "ascension";
    private static final String lobbyModsKey = "mod_list";
    private static final String lobbyCharacterKey = "character";
    private static final String lobbyKeysUnlockedKey = "final_act";

    public static final String metadataTrue = "true";
    public static final String metadataFalse = "false";

    public static SteamMatchmaking matchmaking;
    public static HandleMatchmaking handler;
    public static SteamUser currentUser;
    public static SteamID hostID;

    private static boolean searching;
    public static boolean joinorcreate;

    public static boolean activeMultiplayer;
    private static boolean triedFar;
    public static SteamID currentLobbyID;

    public static boolean isMirth;

    public static boolean isHost;


    public static void init()
    {
        dispose();

        handler = new HandleMatchmaking();

        matchmaking = new SteamMatchmaking(handler);
        currentUser = new SteamUser(new SteamUserCallbacks());

        searching = false;
        activeMultiplayer = false;
    }

    private void startNormalSearch()
    {
        matchmaking.addRequestLobbyListDistanceFilter(SteamMatchmaking.LobbyDistanceFilter.Far);
        logger.info("distance: far");
        matchmaking.addRequestLobbyListStringFilter(lobbyModsKey, generateModList(), SteamMatchmaking.LobbyComparison.Equal);
        matchmaking.addRequestLobbyListStringFilter(lobbyCharacterKey, CardCrawlGame.chosenCharacter.name(), SteamMatchmaking.LobbyComparison.Equal);
        logger.info("chosen character: " + CardCrawlGame.chosenCharacter.name());
        /*matchmaking.addRequestLobbyListStringFilter(lobbyPublicKey, metadataTrue, SteamMatchmaking.LobbyComparison.Equal);
        logger.info("public: true");*/
        Settings.setFinalActAvailability(); //ensure it's updated.
        matchmaking.addRequestLobbyListStringFilter(lobbyKeysUnlockedKey, Settings.isFinalActAvailable ? metadataTrue : metadataFalse, SteamMatchmaking.LobbyComparison.Equal);
        logger.info("4th act unlocked: " + (Settings.isFinalActAvailable ? metadataTrue : metadataFalse));
        matchmaking.requestLobbyList();
        searching = true;
        triedFar = false;
    }

    private void startFarSearch()
    {
        matchmaking.addRequestLobbyListDistanceFilter(SteamMatchmaking.LobbyDistanceFilter.Worldwide);
        logger.info("distance: worldwide");
        matchmaking.addRequestLobbyListStringFilter(lobbyModsKey, generateModList(), SteamMatchmaking.LobbyComparison.Equal);
        matchmaking.addRequestLobbyListStringFilter(lobbyCharacterKey, CardCrawlGame.chosenCharacter.name(), SteamMatchmaking.LobbyComparison.Equal);
        logger.info("chosen character: " + CardCrawlGame.chosenCharacter.name());
        /*matchmaking.addRequestLobbyListStringFilter(lobbyPublicKey, metadataTrue, SteamMatchmaking.LobbyComparison.Equal);
        logger.info("public: true");*/
        Settings.setFinalActAvailability(); //ensure it's updated.
        matchmaking.addRequestLobbyListStringFilter(lobbyKeysUnlockedKey, Settings.isFinalActAvailable ? metadataTrue : metadataFalse, SteamMatchmaking.LobbyComparison.Equal);
        logger.info("4th act unlocked: " + (Settings.isFinalActAvailable ? metadataTrue : metadataFalse));
        matchmaking.requestLobbyList();
        searching = true;
        triedFar = true;
    }

    public static void startFindLobby()
    {
        if (matchmaking != null && !searching)
        {
            logger.info("Lobby filter: Only lobbies with this exact mod list: " + generateModList());
            logger.info("Only lobbies with this character: " + CardCrawlGame.chosenCharacter.name());
            handler.startNormalSearch();
            joinorcreate = false;
        }
        else
        {
            logger.error("ERROR: Attempting to find Steam Lobby while SteamMatchmaking has not been initialized.");
        }
    }

    public static void stop()
    {
        logger.info("Leaving queue.");
        if (searching)
        {
            searching = false;
        }
        if (joinorcreate)
        {
            joinorcreate = false;
        }
        activeMultiplayer = false;
        if (currentLobbyID != null)
        {
            chat.receiveMessage(TEXT[0]); //Left Lobby.
            logger.info("Left lobby " + currentLobbyID + ".");
            matchmaking.leaveLobby(currentLobbyID);
            currentLobbyID = null;
        }
        if (currentPartner != null)
        {
            MultiplayerHelper.sendP2PString("stop");
            currentPartner = null;
        }
        stopGameStart();
    }
    public static void leave()
    {
        if (currentLobbyID != null)
        {
            logger.info("Left lobby " + currentLobbyID + ".");
            matchmaking.leaveLobby(currentLobbyID);
            currentLobbyID = null;
        }
    }

    public static void dispose()
    {
        if (matchmaking != null)
        {
            matchmaking.dispose();
            matchmaking = null;
        }
        if (currentUser != null)
        {
            currentUser.dispose();
            currentUser = null;
        }
    }

    public static boolean inLobby()
    {
        return currentLobbyID != null && currentLobbyID.isValid();
    }
    public static boolean inLobby(SteamID member)
    {
        if (currentLobbyID != null && currentLobbyID.isValid())
        {
            int max = matchmaking.getNumLobbyMembers(currentLobbyID);
            for (int i = 0; i < max; ++i)
            {
                if (member.equals(matchmaking.getLobbyMemberByIndex(currentLobbyID, i)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onFavoritesListChanged(int i, int i1, int i2, int i3, int i4, boolean b, int i5) {

    }

    @Override
    public void onLobbyInvite(SteamID steamID, SteamID steamID1, long l) {

    }

    @Override
    public void onLobbyEnter(SteamID steamIDLobby, int chatPermissions, boolean blocked, SteamMatchmaking.ChatRoomEnterResponse response) {
        if (joinorcreate)
        {
            logger.info("Lobby entered: " + steamIDLobby);
            logger.info("  - response: " + response);

            int numMembers = matchmaking.getNumLobbyMembers(steamIDLobby);
            logger.info("  - " + numMembers + " members in lobby");
            for (int i = 0; i < numMembers; i++) {
                SteamID member = matchmaking.getLobbyMemberByIndex(steamIDLobby, i);
                logger.info("    - " + i + ": accountID=" + member.getAccountID());
            }

            if (numMembers == 2)
            {
                BaseMod.setRichPresence(TEXT[3]);
                //chat.receiveMessage(TEXT[1]); //Entered game.
            }
            else if (numMembers == 1)
            {
                chat.receiveMessage(TEXT[7]); //Entered invalid game.
                matchmaking.leaveLobby(steamIDLobby);

                HandleMatchmaking.startFindLobby();
                lobbyMenu.show(true);
            }
            else //Too many members??? this should be impossible. Handle this on the host side?
            {
                BaseMod.setRichPresence(TEXT[2] + numMembers + " / 2)");
            }

            isMirth = isHost ? isMirth : !matchmaking.getLobbyData(steamIDLobby, hostIsMirthKey).equals(metadataTrue);

            currentLobbyID = steamIDLobby;
            joinorcreate = false;
            activeMultiplayer = true;

            hostID = matchmaking.getLobbyOwner(steamIDLobby);

            for (int i = 0; i < CardCrawlGame.characterManager.getAllCharacters().size(); ++i)
            {
                if (CardCrawlGame.characterManager.getAllCharacters().get(i) instanceof MirthAndMalice)
                {
                    if (((MirthAndMalice) CardCrawlGame.characterManager.getAllCharacters().get(i)).isMirth ^ isMirth) //doesn't match
                    {
                        ((MirthAndMalice) CardCrawlGame.characterManager.getAllCharacters().get(i)).setMirth(isMirth);
                    }
                }
            }
        }
        else
        {
            logger.info("Lobby entered. As queue has been exited, immediately leaving.");
            matchmaking.leaveLobby(steamIDLobby);
        }
    }

    @Override
    public void onLobbyDataUpdate(SteamID lobbyID, SteamID steamIDMember, boolean success) {
        logger.info("Lobby data update for " + lobbyID);
        logger.info("  - member: " + steamIDMember.getAccountID());
        logger.info("  - success: " + success);
    }

    @Override
    public void onLobbyChatUpdate(SteamID lobbyID, SteamID IDChanged, SteamID IDSource, SteamMatchmaking.ChatMemberStateChange chatMemberStateChange) {
        logger.info("Lobby chat update for " + lobbyID);
        logger.info("  - user changed: " + IDChanged.getAccountID());
        logger.info("  - made by user: " + IDSource.getAccountID());
        logger.info("  - state changed: " + chatMemberStateChange.name());

        if (matchmaking.getNumLobbyMembers(lobbyID) == 2)
        {
            logger.info("Lobby is full!");
            if (isHost)
            {
                logger.info("This is host:");
                logger.info("  - Establishing P2P connection with " + IDChanged.getAccountID());
                MultiplayerHelper.sendP2PString(IDChanged, "connect" + CardCrawlGame.playerName);
            }
        }
        else
        {
            if (isHost)
            {
                if (matchmaking.getNumLobbyMembers(lobbyID) == 1)
                {
                    logger.info("Only one.");
                    lobbyMenu.setOtherPlayer(null); //in case someone joins and leaves very quickly
                }
                else
                {
                    //kick extra players
                }
            }
            else
            {
                leave();
                startNormalSearch();
                lobbyMenu.show(true);
            }
        }
    }

    @Override
    public void onLobbyChatMessage(SteamID lobbyID, SteamID userID, SteamMatchmaking.ChatEntryType chatEntryType, int msgIndex) {
        logger.info("Lobby chat message for " + lobbyID);
        logger.info("  - from user: " + userID.getAccountID());
        logger.info("  - chat entry type: " + chatEntryType);
        logger.info("  - chat id: #" + msgIndex);
        
        try {
            matchmaking.getLobbyChatEntry(lobbyID, msgIndex, new SteamMatchmaking.ChatEntry(), chatMessage);

            /*byte[] bytes = new byte[size];//chatMessage.remaining()];
            chatMessage.get(bytes);
            chatMessage.clear();*/

            chat.receiveMessage(CHARSET.decode(chatMessage).toString());
            chatMessage.clear();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void onLobbyGameCreated(SteamID steamID, SteamID steamID1, int i, short i1) {

    }

    public static void sendMessage(String msg)
    {
        if (activeMultiplayer && currentLobbyID != null && currentLobbyID.isValid())
        {
            logger.info("Sending message: " + msg);
            if (matchmaking.sendLobbyChatMsg(currentLobbyID, msg))
            {
                logger.info("Message sent successfully.");
            }
            else
            {
                logger.error("Message failed to send.");
            }
        }
        else if (currentPartner != null)
        {
            MultiplayerHelper.sendP2PMessage(msg);
        }
        else
        {
            logger.info("No current lobby or p2p connection to send message to!");
        }
    }

    @Override
    public void onLobbyMatchList(int resultCount) {
        if (searching)
        {
            searching = false;

            logger.info("Found " + resultCount + " matching lobbies.");
            //chat.receiveMessage("Found " + resultCount + " valid lobbies.");

            if (resultCount == 0 && !triedFar) {
                logger.info("Trying farther distances.");
                //chat.receiveMessage("Trying farther distances.");
                startFarSearch();
            }
            else if (resultCount == 0)
            {
                /*joinorcreate = true;
                logger.info("Attempting to create a new lobby.");
                logger.info("Creating public lobby for 2 players.");
                chat.receiveMessage("Creating new lobby.");
                matchmaking.createLobby(SteamMatchmaking.LobbyType.Public,2);*/
                lobbyMenu.setLobbies(new ArrayList<>());
            }
            else if (resultCount > 0)
            {
                ArrayList<SteamID> lobbies = new ArrayList<>();
                for (int i = 0; i < resultCount; i++) {
                    SteamID lobby = matchmaking.getLobbyByIndex(i);
                    lobbies.add(lobby);
                }
                listLobbies(lobbies);

                lobbyMenu.setLobbies(lobbies);

                /*
                int lastJoinAttempt = 0;

                logger.info("Joining the first lobby.");
                while (lastJoinAttempt < lobbies.size() && !lobbies.get(lastJoinAttempt).isValid())
                {
                    logger.info("Lobby " + (++lastJoinAttempt) + " is invalid, testing next lobby.");
                }

                if (lastJoinAttempt >= lobbies.size())
                {
                    searching = false;
                    logger.info("Attempting to create a new lobby.");
                    logger.info("Creating public lobby for 2 players.");
                    matchmaking.createLobby(SteamMatchmaking.LobbyType.Public,2);
                    joinorcreate = true;
                }
                else
                {
                    logger.info("Joining...");
                    matchmaking.joinLobby(lobbies.get(lastJoinAttempt));
                    searching = false;
                    isHost = false;
                    joinorcreate = true;
                }*/
            }
        }
    }

    @Override
    public void onLobbyKicked(SteamID lobbyID, SteamID user, boolean dc) {
        System.out.println("Kicked from lobby: " + lobbyID);
        System.out.println("  - by user: " + user.getAccountID());
        System.out.println("  - kicked due to disconnect: " + (dc ? "yes" : "no"));

        activeMultiplayer = false;
    }

    @Override
    public void onLobbyCreated(SteamResult result, SteamID steamIDLobby) {
        if (joinorcreate)
        {
            logger.info("Lobby created: " + steamIDLobby);
            logger.info("  - result: " + result.name());
            if (result == SteamResult.OK) {
                String modList = generateModList();
                logger.info("  - lobby modlist: " + modList);
                matchmaking.setLobbyData(steamIDLobby, lobbyModsKey, modList);
                matchmaking.setLobbyData(steamIDLobby, lobbyCharacterKey, CardCrawlGame.chosenCharacter.name());
                matchmaking.setLobbyData(steamIDLobby, lobbyPublicKey, lobbyMenu.publicRoom.enabled ? metadataTrue : metadataFalse);
                matchmaking.setLobbyData(steamIDLobby, lobbyKeysUnlockedKey, Settings.isFinalActAvailable ? metadataTrue : metadataFalse);
                matchmaking.setLobbyData(steamIDLobby, hostIsMirthKey, isMirth ? metadataTrue : metadataFalse);
                matchmaking.setLobbyData(steamIDLobby, lobbyAscensionKey, AbstractDungeon.isAscensionMode ? String.valueOf(AbstractDungeon.ascensionLevel) : "0");

                matchmaking.setLobbyData(steamIDLobby, lobbyNameKey, lobbyMenu.nameInput.getText());

                if (!lobbyMenu.publicRoom.enabled)
                {
                    matchmaking.setLobbyData(steamIDLobby, lobbyPasswordKey, lobbyMenu.passwordInput.getText());
                }

                isHost = true;

                currentLobbyID = steamIDLobby;
                joinorcreate = false;
                activeMultiplayer = true;
                //matchmaking.joinLobby(steamIDLobby);
                BaseMod.setRichPresence(TEXT[2] + "1 / 2)");
                logger.info(currentUser.getSteamID());

                lobbyMenu.displayLobbyInfo(new ActiveLobbyData(lobbyMenu.nameInput.getText(), AbstractDungeon.isAscensionMode ? AbstractDungeon.ascensionLevel : 0, CardCrawlGame.playerName, null, isMirth));
            }
        }
        else
        {
            if (result == SteamResult.OK)
            {
                logger.info("Lobby created successfully. However, queue has been exited, so immediately leaving lobby.");
                matchmaking.leaveLobby(steamIDLobby);
            }
        }
    }

    @Override
    public void onFavoritesListAccountsUpdated(SteamResult steamResult) {

    }



    private void listLobbies(ArrayList<SteamID> lobbies) {
        int index = 1;
        for (SteamID lobby : lobbies) {
            logger.info("   Match " + index++ + ":");
            if (lobby.isValid()) {
                int members = matchmaking.getNumLobbyMembers(lobby);
                logger.info(members + " members");
            } else {
                logger.error("invalid SteamID!");
            }
        }
    }

    public static String generateModList()
    {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> modData = new ArrayList<>();
        for (ModInfo m : Loader.MODINFOS)
        {
            modData.add(m.ID + ":" + m.ModVersion.toString());
        }
        modData.sort(String::compareTo);
        for (String s : modData)
        {
            sb.append(s).append(" ");
        }
        return sb.toString().trim();
    }
}
