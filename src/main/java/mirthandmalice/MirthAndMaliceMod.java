package mirthandmalice;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.Ghosts;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.helpers.TipTracker;
import com.megacrit.cardcrawl.helpers.TrialHelper;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CtClass;
import javassist.NotFoundException;
import mirthandmalice.actions.cards._IMPROVE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.classutil.*;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.card_use.DiscardToCorrectPile;
import mirthandmalice.patch.card_use.LastCardType;
import mirthandmalice.patch.combat.*;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.patch.events.GenericEventVoting;
import mirthandmalice.patch.events.RoomEventVoting;
import mirthandmalice.patch.lobby.UseMultiplayerQueue;
import mirthandmalice.patch.map.BossRoomVoting;
import mirthandmalice.patch.map.MapRoomVoting;
import mirthandmalice.patch.relics.VoteBossRelic;
import mirthandmalice.ui.ChatBox;
import mirthandmalice.ui.LobbyMenu;
import mirthandmalice.util.*;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

/*TODO LIST:
DESYNCS:
Something to do with status added by enemies? Happens quite randomly.
Event voting. Possibility of one player advancing while the other is stuck.

Status cards added directly to draw desync?

heart ai?

fairy in a bottle code - fix


Improve chatbox - allow multiline, increase message length limit


+Upon entering next act and generating map, if other player has already voted, re-check voted node so that it is rendered/works properly.

Cards: Anything that affects order of cards in hand.
Possible: At start of each turn, and after each card is resolved, send array of card uuids.
If card play fails, send back current hand state uuids, and reshuffle cards to match if all uuids match.

+Implement new basegame card draw hook for Corruption and Confusion

+Patch ShowCardAndAddToDrawPileEffect/ShowCardAndAddToDiscardPileEffect to trigger onCopy
Just postfix

EVENTS:
+Patch AbstractDungeon lines ~2300-2310 (in intellij lines) to ensure it checks both players' gold amounts when disabling certain events

Living Wall - Augumenter - Transmogrifier - Does transform work correctly? - Worked fine, once
Transformation of Transmogrifier is not seeded, but it should work fine since the individually obtained cards and card removal should be reported.
Ancient Writing - Upgrading does not upgrade other deck
+Council of Ghosts - Doubled apparitions is kind of op... Maybe remove from pool
+Removal of bottles - Unbottle other player's bottled card?
+Vampires - Probably strike removal won't work, since pandora's doesn't. - note after looking at code - It will not. Removes directly from arraylist.
Falling - Should be fine, but check.
Mind bloom -
    +I Am Awake - Upgrades do not sync
    Other options should be fine.
+A Note For Yourself - Remove from pool
+Bonfire Spirits - report hp changes
Designer In-Spire - ensure transform/upgrades are reported properly
The Divine Fountain - Make sure it works
+We Meet Again/Ranwid - +Gold option: If other player cannot afford it, cannot take
                        +Potion option: Also can take a potion in list used to track other player potions
                        Card option: Fine as is.. probably?
Lab - Ensure when potions are claimed, other player cannot? Should be fine.

Powers:
+Hex - Sync generation based on who played card
+Automaton orbs - Steal from both decks? Return to appropriate deck upon death?

Relics:
+Pandora's Box - Card additions are synced, but card removals are not - Removes directly from cardgroup arraylist.
+Bottle Relics - canSpawn will desync if one player can bottle but other cannot
Ninja Scroll? Silent only, so it should be fine, but... I don't trust things.
+Mummified hand
Test how sundial interacts
Dead Branch - Ensure card generation actually generates the same card for both players
+Du-vu doll - Count curses in both decks
Gambling Chip - test - should be functional.
+Girya - Patch campsite option
Shovel - Test. It probably will need adjustment.
Unceasing Top - If one player's hand is empty, they draw? This one will be a pain. Why is it in so many places :(
Astrolabe - Make sure transformation works properly
Calling Bell - Ensure both players obtain the relics if one of them chooses to take one. This should work, since it's a combat reward screen, but not 100% sure.
+Eternal Feather - Count both decks
Bloody Idol - Heal for 2, triggers on both players' gold gain?
Necronomicon - Test if it works on cards played by ally. Should be fine? They don't have freeToPlayOnce set to true, they just use other energy panel.
Nilry's - Test
Warped Tongs - Upgrade a random card in each player's hand?
Strange Spoon - Should work fine, but test
Toolbox - 1 colorless for each player? Or just 1 colorless in one player's hand?
+Toy Ornithopter/onPotionUse ensure it works when other player uses potion
Choker - Increase limit to 8?
Singing Bowl - Sync hp gain (nerf to 1? eh)

+Fairy Potion - If one player has fairy potion, should work for both players (check tracked other player potions on death)

Colorless cards:
Mind Blast
Secret Weapon/Tool thing
Jack Of All Trades/Transmutation

Curses:
Anything to do with cards in hand.
Pain - Only for the player whose hand it is in?
Regret - Only for cards in that hand
Necronomicurse - Where it goes when Exhausted

Other character cards:
I don't want to figure out all of them right now.
Anything with a screen.
Anything that affects hand, draw pile, or discard pile.

extra features:
Resync command - when sent by host, resyncs other player forcefully

 */

@SpireInitializer
public class MirthAndMaliceMod implements EditCardsSubscriber, EditRelicsSubscriber, EditStringsSubscriber,
        EditCharactersSubscriber, EditKeywordsSubscriber, PostInitializeSubscriber, PreStartGameSubscriber,
        RenderSubscriber, PostUpdateSubscriber, OnStartBattleSubscriber, StartGameSubscriber, PostBattleSubscriber,
        StartActSubscriber
{
    public static final String modID = "mirthandmalice";

    public static final boolean FULL_DEBUG = true;

    public static final Logger logger = LogManager.getLogger(modID);

    // Mod panel stuff
    private static final String BADGE_IMAGE = "img/Badge.png";
    private static final String MODNAME = "Mirth and Malice";
    private static final String AUTHOR = "Alchyr";
    private static final String DESCRIPTION = " . . . ";

    // Card backgrounds/basic images
    private static final String ATTACK_BACK = "bg_attack.png";
    private static final String ATTACK_PORTRAIT = "bg_attack_p.png";

    private static final String SKILL_BACK = "bg_skill.png";
    private static final String SKILL_PORTRAIT = "bg_skill_p.png";
    
    private static final String POWER_BACK = "bg_power.png";
    private static final String POWER_PORTRAIT = "bg_power_p.png";

    private static final String CARD_SMALL_ORB = "card_small_orb.png";
    private static final String CORNER_ORB = "card_orb.png";
    private static final String PORTRAIT_CORNER_ORB = "card_large_orb.png";


    // Character images
    private static final String BUTTON = "img/character/CharacterButton.png";
    private static final String PORTRAIT = "img/character/CharacterPortrait.png";

    // Colors
    public static final Color MIRTH_COLOR = new Color(0.9f, 0.9f, 0.9f, 1.0f);
    public static final Color MALICE_COLOR = new Color(0.3f, 0.25f, 0.25f, 1.0f);
    public static final Color GRAY_COLOR = new Color(0.45f, 0.45f, 0.45f, 1.0f);



    public static String makeID(String partialID)
    {
        return modID + ":" + partialID;
    }
    public static String assetPath(String partialPath)
    {
        return modID + "/" + partialPath;
    }
    public static String cardPath(String partialPath) { return modID + "/img/cards/" + partialPath + "/"; }

    public static LobbyMenu lobbyMenu;
    public static ChatBox chat;

    private static boolean startingGame = false;
    public static boolean gameStarted = false;
    private static float gameStartTimer = 0.0f;
    private static float eventChooseTimer = 0.0f;
    private static float mapChooseTimer = 0.0f;
    private static float bossRelicChooseTimer = 0.0f;

    private static int lastMilestone = 0;


    public static void beginGameStartTimer()
    {
        if (!startingGame && !gameStarted && HandleMatchmaking.activeMultiplayer)
        {
            startingGame = true;
            gameStartTimer = 5.0f;
            lastMilestone = 4;
            HandleMatchmaking.sendMessage("Starting game in 5");
        }
    }
    public static void stopGameStart()
    {
        if (startingGame)
        {
            startingGame = false;
            gameStartTimer = 0.0f;
            lastMilestone = 0;
        }
    }

    public static void startEventChooseTimer(float startTime)
    {
        if (eventChooseTimer <= 0.0f)
        {
            eventChooseTimer = startTime;
            lastMilestone = MathUtils.floor(startTime - 0.5f);
        }
    }
    public static void stopEventChooseTimer()
    {
        eventChooseTimer = 0.0f;
        lastMilestone = 0;
    }
    public static void startBossRelicChooseTimer(float startTime)
    {
        if (bossRelicChooseTimer <= 0.0f)
        {
            bossRelicChooseTimer = startTime;
            lastMilestone = MathUtils.floor(startTime - 0.5f);
        }
    }
    public static void stopBossRelicChooseTimer()
    {
        bossRelicChooseTimer = 0.0f;
        lastMilestone = 0;
    }
    public static void startMapChooseTimer(float startTime)
    {
        if (mapChooseTimer <= 0.0f)
        {
            MultiplayerHelper.sendP2PMessage("Players disagree. Conflict will be automatically resolved in " + 15 + " seconds.");
            mapChooseTimer = startTime;
            lastMilestone = MathUtils.floor(startTime - 0.5f);
        }
    }
    public static void stopMapChooseTimer()
    {
        mapChooseTimer = 0.0f;
        lastMilestone = 0;
    }

    @Override
    public void receiveStartGame() {
        if (AbstractDungeon.player instanceof MirthAndMalice)
        {
            MapRoomVoting.reset();
            BossRoomVoting.waitingForBoss = false;
            BossRoomVoting.otherWaitingForBoss = false;
            VoteBossRelic.votedRelic = null;
            VoteBossRelic.otherVotedRelic = null;
            VoteBossRelic.chosenRelic = null;
        }
    }

    @Override
    public void receivePostUpdate() {
        if (chat != null)
        {
            chat.update();
            if (startingGame)
            {
                if (gameStartTimer > 0)
                {
                    gameStartTimer -= Gdx.graphics.getDeltaTime();
                    if (lastMilestone > gameStartTimer)
                    {
                        HandleMatchmaking.sendMessage(String.valueOf(lastMilestone));
                        updateMilestone();
                    }
                }
                else
                {
                    startingGame = false;
                    gameStarted = true;

                    startGame();
                }
            }

            if (eventChooseTimer > 0.0f)
            {
                eventChooseTimer -= Gdx.graphics.getDeltaTime();
                if (eventChooseTimer <= 0.0f)
                {
                    if (RoomEventVoting.choseOption)
                        RoomEventVoting.resolveConflict();
                    if (GenericEventVoting.choseOption)
                        GenericEventVoting.resolveConflict();
                }
                else if (lastMilestone > eventChooseTimer)
                {
                    HandleMatchmaking.sendMessage(String.valueOf(lastMilestone));
                    updateMilestone();
                }
            }

            if (mapChooseTimer > 0.0f)
            {
                mapChooseTimer -= Gdx.graphics.getDeltaTime();
                if (mapChooseTimer <= 0.0f)
                {
                    MapRoomVoting.resolveConflict();
                }
                else if (lastMilestone > mapChooseTimer)
                {
                    HandleMatchmaking.sendMessage(String.valueOf(lastMilestone));
                    updateMilestone();
                }
            }

            if (bossRelicChooseTimer > 0.0f)
            {
                bossRelicChooseTimer -= Gdx.graphics.getDeltaTime();
                if (bossRelicChooseTimer <= 0.0f)
                {
                    VoteBossRelic.resolveConflict();
                }
                else if (lastMilestone > bossRelicChooseTimer)
                {
                    HandleMatchmaking.sendMessage(String.valueOf(lastMilestone));
                    updateMilestone();
                }
            }
        }

        if (lobbyMenu != null)
        {
            lobbyMenu.update();
        }
        MultiplayerHelper.readPostUpdate();
    }

    private static void updateMilestone()
    {
        if (lastMilestone > 30)
        {
            lastMilestone = (MathUtils.ceil(lastMilestone / 10.0f) - 1) * 10;
        }
        else if (lastMilestone > 5)
        {
            lastMilestone = (MathUtils.ceil(lastMilestone / 5.0f) - 1) * 5;
        }
        else
        {
            --lastMilestone;
        }
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        DiscardToCorrectPile.reset();
        RequireDoubleEndTurn.reset();
        TrackCardSource.useOtherEnergy = false;
        TrackCardSource.useMyEnergy = false;
        MixEnemyTempCards.toMirth = true;
        LastCardType.type = AbstractCard.CardType.CURSE; //to avoid any null issues. Nothing will trigger off of playing curses.
        LastCardType.lastCardCopy = null;
        PotionUse.queuedPotionUse.clear();
        HandCardSelectReordering.reset();
    }
    @Override
    public void receivePostBattle(AbstractRoom abstractRoom) {
        TrackCardSource.useOtherEnergy = false;
        TrackCardSource.useMyEnergy = false;
        MixEnemyTempCards.toMirth = true;
        LastCardType.type = AbstractCard.CardType.CURSE;
        LastCardType.lastCardCopy = null;
        PotionUse.queuedPotionUse.clear();
        HandCardSelectReordering.reset();

        _IMPROVE._clean();
    }

    //patched in hook
    public static void preMonsterTurn(AbstractMonster m) {
        BurstActive.active.set(m, false);
    }

    public void startGame()
    {
        if (CardCrawlGame.mode == CardCrawlGame.GameMode.CHAR_SELECT)
        {
            TipTracker.neverShowAgain("NEOW_SKIP");

            UseMultiplayerQueue.inQueue = false;

            if (Settings.seed == null) {
                long sourceTime = System.nanoTime();
                Random rng = new Random(sourceTime);
                Settings.seedSourceTimestamp = sourceTime;
                Settings.seed = SeedHelper.generateUnoffensiveSeed(rng);
            } else {
                Settings.seedSet = true;
            }

            CardCrawlGame.mainMenuScreen.isFadingOut = true;
            CardCrawlGame.mainMenuScreen.fadeOutMusic();
            Settings.isDailyRun = false;
            boolean isTrialSeed = TrialHelper.isTrialSeed(SeedHelper.getString(Settings.seed));
            if (isTrialSeed) {
                Settings.specialSeed = Settings.seed;
                long sourceTime = System.nanoTime();
                Random rng = new Random(sourceTime);
                Settings.seed = SeedHelper.generateUnoffensiveSeed(rng);
                Settings.isTrial = true;
            }

            if (Settings.seedSet)
                MultiplayerHelper.sendP2PString("seedset" + Settings.seed.toString());
            else
                MultiplayerHelper.sendP2PString("seed" + Settings.seed.toString());

            if (Settings.isTrial)
            {
                MultiplayerHelper.sendP2PString("trial" + Settings.specialSeed);
            }

            ModHelper.setModsFalse();
            AbstractDungeon.generateSeeds();
            AbstractDungeon.isAscensionMode = CardCrawlGame.mainMenuScreen.charSelectScreen.isAscensionMode;
            if (AbstractDungeon.isAscensionMode) {
                AbstractDungeon.ascensionLevel = CardCrawlGame.mainMenuScreen.charSelectScreen.ascensionLevel;
            } else {
                AbstractDungeon.ascensionLevel = 0;
            }
            MultiplayerHelper.sendP2PString("ascension" + AbstractDungeon.ascensionLevel);

            MultiplayerHelper.sendP2PString("start_game");

            MultiplayerHelper.sendP2PMessage("Starting game...");
        }
    }
    public static void startSetupGame() //start game where settings have already been set
    {
        TipTracker.neverShowAgain("NEOW_SKIP");

        UseMultiplayerQueue.inQueue = false;

        CardCrawlGame.mainMenuScreen.isFadingOut = true;
        CardCrawlGame.mainMenuScreen.fadeOutMusic();
        Settings.isDailyRun = false;
        AbstractDungeon.generateSeeds();

        HandleMatchmaking.leave();
    }

    @Override
    public void receiveRender(SpriteBatch sb) {
        if (chat != null)
            chat.render(sb);

        if (lobbyMenu != null)
            lobbyMenu.render(sb);
    }

    public MirthAndMaliceMod()
    {
        BaseMod.subscribe(this);

        addColor(CharacterEnums.MIRTHMALICE_MIRTH, MIRTH_COLOR, "mirth");
        addColor(CharacterEnums.MIRTHMALICE_MALICE, MALICE_COLOR, "malice");
        addColor(CharacterEnums.MIRTHMALICE_NEUTRAL, GRAY_COLOR, "neutral");
    }

    private void addColor(AbstractCard.CardColor cardColor, Color color, String folder)
    {
        BaseMod.addColor(cardColor, color,
                cardPath(folder) + ATTACK_BACK,
                cardPath(folder) + SKILL_BACK,
                cardPath(folder) + POWER_BACK,
                cardPath(folder) + CORNER_ORB,
                cardPath(folder) + ATTACK_PORTRAIT,
                cardPath(folder) + SKILL_PORTRAIT,
                cardPath(folder) + POWER_PORTRAIT,
                cardPath(folder) + PORTRAIT_CORNER_ORB,
                cardPath(folder) + CARD_SMALL_ORB);
    }

    @Override
    public void receiveEditCharacters() {
        BaseMod.addCharacter(new MirthAndMalice(MathUtils.randomBoolean()),
                assetPath(BUTTON), assetPath(PORTRAIT), CharacterEnums.MIRTHMALICE);
    }

    @Override
    public void receiveEditCards() {
        logger.info("Adding Mirth and Malice cards.");
        try {
            autoAddCards();
        } catch (URISyntaxException | IllegalAccessException | InstantiationException | NotFoundException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveEditRelics() {
        //BaseMod.addRelicToCustomPool(new SkyMirror(), CardColorEnum.ASTROLOGER);
    }

    @Override
    public void receiveEditStrings()
    {
        String lang = getLangString();

        try
        {
            BaseMod.loadCustomStringsFile(RelicStrings.class, assetPath("localization/" + lang + "/RelicStrings.json"));
            BaseMod.loadCustomStringsFile(CardStrings.class, assetPath("localization/" + lang + "/CardStrings.json"));
            BaseMod.loadCustomStringsFile(CharacterStrings.class, assetPath("localization/" + lang + "/CharacterStrings.json"));
            BaseMod.loadCustomStringsFile(PowerStrings.class, assetPath("localization/" + lang + "/PowerStrings.json"));
            BaseMod.loadCustomStringsFile(UIStrings.class, assetPath("localization/" + lang + "/UIStrings.json"));
            BaseMod.loadCustomStringsFile(MonsterStrings.class, assetPath("localization/" + lang + "/EnemyStrings.json"));
        }
        catch (Exception e)
        {
            lang = "eng";
            BaseMod.loadCustomStringsFile(RelicStrings.class, assetPath("localization/" + lang + "/RelicStrings.json"));
            BaseMod.loadCustomStringsFile(CardStrings.class, assetPath("localization/" + lang + "/CardStrings.json"));
            BaseMod.loadCustomStringsFile(CharacterStrings.class, assetPath("localization/" + lang + "/CharacterStrings.json"));
            BaseMod.loadCustomStringsFile(PowerStrings.class, assetPath("localization/" + lang + "/PowerStrings.json"));
            BaseMod.loadCustomStringsFile(UIStrings.class, assetPath("localization/" + lang + "/UIStrings.json"));
            BaseMod.loadCustomStringsFile(MonsterStrings.class, assetPath("localization/" + lang + "/EnemyStrings.json"));
        }
    }

    @Override
    public void receiveEditKeywords()
    {
        String lang = getLangString();
        String prefix = modID.toLowerCase();

        try
        {
            Gson gson = new Gson();
            String json = Gdx.files.internal(assetPath("localization/" + lang + "/Keywords.json")).readString(String.valueOf(StandardCharsets.UTF_8));
            KeywordWithProper[] keywords = gson.fromJson(json, KeywordWithProper[].class);

            if (keywords != null) {
                for (KeywordWithProper keyword : keywords) {
                    BaseMod.addKeyword(prefix, keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
                }
            }
        }
        catch (Exception e)
        {
            Gson gson = new Gson();
            String json = Gdx.files.internal(assetPath("localization/eng/Keywords.json")).readString(String.valueOf(StandardCharsets.UTF_8));
            KeywordWithProper[] keywords = gson.fromJson(json, KeywordWithProper[].class);

            if (keywords != null) {
                for (KeywordWithProper keyword : keywords) {
                    BaseMod.addKeyword(prefix, keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
                }
            }
        }
    }

    @Override
    public void receivePreStartGame() {
        //Max hand size is set to 10 immediately before this is called.
        if (CardCrawlGame.chosenCharacter == CharacterEnums.MIRTHMALICE)
            BaseMod.MAX_HAND_SIZE = 6;
    }

    @Override
    public void receiveStartAct() {
        if (CardCrawlGame.chosenCharacter == CharacterEnums.MIRTHMALICE)
        {
            //event list should be initialized at this point
            //remove anything I don't want here
            AbstractDungeon.eventList.remove(Ghosts.ID);
        }
    }

    @Override
    public void receivePostInitialize() {
        //Setup mod menu info stuff
        Texture badgeTexture = TextureLoader.getTexture(assetPath(BADGE_IMAGE));

        if (badgeTexture != null)
        {
            //ModPanel panel = new ModPanel();

            BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, null);
        }

        //Initialize fantasy card effects
        //FantasyEffect.initializeEffectLists();

        //Initialize multiplayer stuff
        HandleMatchmaking.init();
        MultiplayerHelper.init();

        //Initialize UI
        chat = new ChatBox();
        lobbyMenu = new LobbyMenu();
    }





    //I totally didn't copy this from Hubris, made by kiooeht.
    private static void autoAddCards() throws URISyntaxException, IllegalAccessException, InstantiationException, NotFoundException, ClassNotFoundException {
        ClassFinder finder = new ClassFinder();
        URL url = MirthAndMaliceMod.class.getProtectionDomain().getCodeSource().getLocation();
        finder.add(new File(url.toURI()));

        ClassFilter filter =
                new AndClassFilter(
                        new NotClassFilter(new InterfaceOnlyClassFilter()),
                        new NotClassFilter(new AbstractClassFilter()),
                        new ClassModifiersClassFilter(Modifier.PUBLIC),
                        new CardFilter()
                );
        Collection<ClassInfo> foundClasses = new ArrayList<>();
        finder.findClasses(foundClasses, filter);

        for (ClassInfo classInfo : foundClasses) {
            CtClass cls = Loader.getClassPool().get(classInfo.getClassName());

            boolean isCard = false;
            CtClass superCls = cls;
            while (superCls != null) {
                superCls = superCls.getSuperclass();
                if (superCls == null) {
                    break;
                }
                if (superCls.getName().equals(AbstractCard.class.getName())) {
                    isCard = true;
                    break;
                }
            }
            if (!isCard) {
                continue;
            }

            AbstractCard card = (AbstractCard) Loader.getClassPool().getClassLoader().loadClass(cls.getName()).newInstance();

            BaseMod.addCard(card);
            logger.info("Added card " + card.cardID + "\t\tColor " + card.color.name());
        }
    }

    private static String getLangString()
    {
        return Settings.language.name().toLowerCase();
    }

    @SuppressWarnings("unused") public static void initialize() {
        new MirthAndMaliceMod();
    }
}