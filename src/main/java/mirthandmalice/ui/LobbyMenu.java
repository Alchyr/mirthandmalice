package mirthandmalice.ui;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.options.ToggleButton;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.enums.ToggleType;
import mirthandmalice.util.ActiveLobbyData;
import mirthandmalice.util.HandleMatchmaking;
import mirthandmalice.util.LobbyData;
import mirthandmalice.util.TextureLoader;

import java.util.ArrayList;

import static mirthandmalice.character.MirthAndMalice.characterStrings;
import static mirthandmalice.MirthAndMaliceMod.*;
import static mirthandmalice.util.HandleMatchmaking.*;

public class LobbyMenu {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("LobbyPanel"));
    private static final String[] TEXT = uiStrings.TEXT;

    private static final Texture panelBack = TextureLoader.getTexture(assetPath("img/ui/lobbyPanel.png"));
    private static final Texture refreshButton = TextureLoader.getTexture(assetPath("img/ui/refresh.png"));
    private static final Texture createButton = TextureLoader.getTexture(assetPath("img/ui/new.png"));
    private static final Texture arrow = TextureLoader.getTexture(assetPath("img/ui/downArrow.png"));

    private static final float LINE_HEIGHT = FontHelper.getHeight(FontHelper.buttonLabelFont, "X", 1) + 8.0f * Settings.scale;

    private static final int PANEL_WIDTH = 840;
    private static final int PANEL_HEIGHT = 600;

    private static final float PANEL_X = Settings.WIDTH / 2.0f + 75.0f * Settings.scale;
    private static final float PANEL_Y = Settings.HEIGHT / 2.0f - 200.0f * Settings.scale;

    private static final float BUTTON_Y = PANEL_Y + PANEL_HEIGHT * Settings.scale + 10.0f * Settings.scale;
    private static final int BUTTON_SIZE = 56;

    private static final float BUTTON_OFFSET = BUTTON_SIZE / 2.0f;

    private static final float REFRESH_X = PANEL_X + (PANEL_WIDTH * Settings.scale) - 90.0f * Settings.scale;
    private static final float CREATE_X = PANEL_X + (PANEL_WIDTH * Settings.scale) - 156.0f * Settings.scale;

    private static final float PANEL_CENTER_X = PANEL_X + (PANEL_WIDTH / 2.0f) * Settings.scale;
    private static final float PANEL_CENTER_Y = PANEL_Y + (PANEL_HEIGHT / 2.0f) * Settings.scale;

    private static final float OTHER_RENDER_OFFSET = 12.0f * Settings.scale;

    private static final float LABEL_Y = PANEL_Y + 525.0f * Settings.scale;
    private static final float OTHER_RENDER_LABEL_Y = LABEL_Y + OTHER_RENDER_OFFSET;
    private static final float LOBBY_START_Y = LABEL_Y - (LINE_HEIGHT + 7.0f * Settings.scale);

    private static final float LOBBY_WIDTH = 700.0f * Settings.scale;

    //for lobby list
    private static final float NAME_LABEL_X = PANEL_X + 75.0f * Settings.scale;
    private static final float PUBLIC_LABEL_X = PANEL_X + 450.0f * Settings.scale;
    private static final float ASCENSION_SYMBOL_X = PANEL_X + 580.0f * Settings.scale;
    private static final float CHARACTER_LABEL_X = PANEL_X + 660.0f * Settings.scale;

    private static final float ICON_W = 64.0F * Settings.scale;

    private static final float CHARACTER_LABEL_Y = PANEL_Y + 312.0f * Settings.scale; //for lobby creation
    private static final float MOKOU_LABEL_X = NAME_LABEL_X + 80.0f * Settings.scale;
    private static final float KEINE_LABEL_X = MOKOU_LABEL_X + 200.0f * Settings.scale;
    private static final float RANDOM_LABEL_X = KEINE_LABEL_X + 200.0f * Settings.scale;

    private static final float CREATE_TEXT_Y = PANEL_Y + 100.0f * Settings.scale;
    private static final float CREATE_TEXT_WIDTH = FontHelper.getWidth(FontHelper.buttonLabelFont, TEXT[13], 1);

    private static final float PUBLIC_TOGGLE_X = PANEL_X + 130.0f * Settings.scale;
    private static final float PUBLIC_TOGGLE_Y = PANEL_Y + 450.0f * Settings.scale;

    private static final float MIRTH_TOGGLE_X = PUBLIC_TOGGLE_X;
    private static final float MALICE_TOGGLE_X = MIRTH_TOGGLE_X + 200.0f * Settings.scale;
    private static final float RANDOM_TOGGLE_X = MALICE_TOGGLE_X + 200.0f * Settings.scale;
    private static final float CHARACTER_TOGGLE_Y = PANEL_Y + 300.0f * Settings.scale;

    private static final float PUBLIC_INPUT_TEXT_Y = PANEL_Y + 462.0f * Settings.scale;

    private static final float PASSWORD_LABEL_Y = PANEL_Y + 390.0f * Settings.scale;
    private static final float PASSWORD_INPUT_Y = PASSWORD_LABEL_Y - LobbyTextInput.HEIGHT / 2.0f * Settings.scale - 12.0f * Settings.scale;

    private static final float INPUT_X = PANEL_X + 200.0f * Settings.scale;

    private static final float ARROW_X = PANEL_X + 770.0f * Settings.scale;
    private static final float NEXT_ARROW_Y = PANEL_Y + 55.0f * Settings.scale;
    private static final float PREV_ARROW_Y = LOBBY_START_Y + 12.0f * Settings.scale;
    private static final int ARROW_WIDTH = 20;
    private static final int ARROW_HEIGHT = 24;
    private static final float ARROW_OFFSET_X = ARROW_WIDTH / 2.0f;
    private static final float ARROW_OFFSET_Y = ARROW_HEIGHT / 2.0f;

    private static final int LOBBIES_PER_PAGE = 14;

    public boolean visible = false;

    private int mode = 0; //0 is default, 1 is joining a lobby, 2 is creation menu, 3 is post-created

    private ArrayList<LobbyData> lobbies = new ArrayList<>();
    private int page = 0;
    private int maxPage = 0;

    private int hoveredIndex = -1;

    private Color refreshButtonColor = new Color(0.8f, 0.8f, 0.8f, 1.0f);
    private float refreshButtonScale = 1.0f;

    private Color createButtonColor = new Color(0.8f, 0.8f, 0.8f, 1.0f);
    private float createButtonScale = 1.0f;

    private Hitbox refreshButtonHitbox = new Hitbox(REFRESH_X + (BUTTON_SIZE / 2.0f * (1 - Settings.scale)), BUTTON_Y + (BUTTON_SIZE / 2.0f * (1 - Settings.scale)), BUTTON_SIZE * Settings.scale, BUTTON_SIZE * Settings.scale);
    private Hitbox createButtonHitbox = new Hitbox(CREATE_X + (BUTTON_SIZE / 2.0f * (1 - Settings.scale)), BUTTON_Y + (BUTTON_SIZE / 2.0f * (1 - Settings.scale)), BUTTON_SIZE * Settings.scale, BUTTON_SIZE * Settings.scale);
    private Hitbox lobbyAreaHitbox = new Hitbox(NAME_LABEL_X - 5.0f * Settings.scale, PANEL_Y + 50.0f * Settings.scale, LOBBY_WIDTH, 450.0f * Settings.scale);

    private Hitbox prevArrowHitbox = new Hitbox(ARROW_X, PREV_ARROW_Y, ARROW_WIDTH * Settings.scale, ARROW_HEIGHT * Settings.scale);
    private Hitbox nextArrowHitbox = new Hitbox(ARROW_X, NEXT_ARROW_Y, ARROW_WIDTH * Settings.scale, ARROW_HEIGHT * Settings.scale);

    private Hitbox lobbyCreateHitbox = new Hitbox(PANEL_CENTER_X - CREATE_TEXT_WIDTH / 2.0f, CREATE_TEXT_Y - LINE_HEIGHT / 2.0f, CREATE_TEXT_WIDTH, LINE_HEIGHT);

    private ArrayList<Hitbox> lobbyHitboxes = new ArrayList<>();

    //Lobby Creation UI
    private float ASC_LEFT_W;
    private float ASC_RIGHT_W;

    public LobbyTextInput nameInput = new LobbyTextInput(INPUT_X, LOBBY_START_Y);
    public ToggleButton publicRoom = new ToggleButton(PUBLIC_TOGGLE_X, 0, PUBLIC_TOGGLE_Y, ToggleType.PUBLIC_LOBBY, false);
    public LobbyTextInput passwordInput = new LobbyTextInput(INPUT_X, PASSWORD_INPUT_Y);

    private ToggleButton mirthToggle = new ToggleButton(MIRTH_TOGGLE_X, 0, CHARACTER_TOGGLE_Y, ToggleType.PUBLIC_LOBBY, false);
    private ToggleButton maliceToggle = new ToggleButton(MALICE_TOGGLE_X, 0, CHARACTER_TOGGLE_Y, ToggleType.PUBLIC_LOBBY, false);
    private ToggleButton randomToggle = new ToggleButton(RANDOM_TOGGLE_X, 0, CHARACTER_TOGGLE_Y, ToggleType.PUBLIC_LOBBY, false);

    private Hitbox ascensionModeHb;
    private Hitbox ascLeftHb;
    private Hitbox ascRightHb;

    private boolean isAscensionMode;
    private int ascensionLevel;

    public boolean searching = false;
    public boolean receivePassword = false;
    private LobbyData waitingLobby = null;
    private ActiveLobbyData inLobby = null;

    public LobbyMenu()
    {
        float x = NAME_LABEL_X - 5.0f * Settings.scale;
        float y = LOBBY_START_Y;
        for (int i = 0; i < LOBBIES_PER_PAGE; ++i)
        {
            lobbyHitboxes.add(new Hitbox(x, y - OTHER_RENDER_OFFSET, LOBBY_WIDTH, LINE_HEIGHT - 6.0f * Settings.scale));
            y -= LINE_HEIGHT;
        }

        mirthToggle.toggle();
        maliceToggle.toggle();

        FontHelper.cardTitleFont.getData().setScale(1.0F);
        this.ASC_LEFT_W = FontHelper.getSmartWidth(FontHelper.cardTitleFont, TEXT[6], 9999.0F, 0.0F);
        this.ASC_RIGHT_W = FontHelper.getSmartWidth(FontHelper.cardTitleFont, TEXT[7] + "22", 9999.0F, 0.0F);

        this.ascensionModeHb = new Hitbox(ASC_LEFT_W + 100.0F * Settings.scale, 50.0F * Settings.scale);
        this.ascLeftHb = new Hitbox(70.0F * Settings.scale, 70.0F * Settings.scale);
        this.ascRightHb = new Hitbox(70.0F * Settings.scale, 70.0F * Settings.scale);

        this.ascensionModeHb.move(PANEL_CENTER_X - ASC_LEFT_W / 2.0F - 50.0F * Settings.scale, PANEL_Y + 200.0F * Settings.scale);
        this.ascLeftHb.move(PANEL_CENTER_X + 200.0F * Settings.scale - ASC_RIGHT_W * 0.5F, PANEL_Y + 200.0F * Settings.scale);
        this.ascRightHb.move(PANEL_CENTER_X + 200.0F * Settings.scale + ASC_RIGHT_W * 1.5F, PANEL_Y + 200.0F * Settings.scale);
    }

    public void show(boolean searching)
    {
        this.lobbies.clear();
        inLobby = null;
        this.mode = 0;
        this.searching = searching;
        receivePassword = false;
        this.hoveredIndex = -1;
        this.visible = true;
    }
    public void show(ArrayList<SteamID> lobbies)
    {
        setLobbies(lobbies);
        this.mode = 0;
        this.visible = true;
        receivePassword = false;
    }

    public void setLobbies(ArrayList<SteamID> lobbies)
    {
        this.lobbies.clear();
        inLobby = null;
        searching = false;
        receivePassword = false;

        if (!lobbies.isEmpty()) {
            for (SteamID lobby : lobbies) {
                LobbyData data = new LobbyData();

                data.id = lobby;
                data.name = matchmaking.getLobbyData(lobby, HandleMatchmaking.lobbyNameKey);
                data.hostIsMirth = matchmaking.getLobbyData(lobby, HandleMatchmaking.hostIsMirthKey).equals(metadataTrue);
                data.isPublic = matchmaking.getLobbyData(lobby, HandleMatchmaking.lobbyPublicKey).equals(metadataTrue);
                data.ascension = Integer.parseInt(matchmaking.getLobbyData(lobby, HandleMatchmaking.lobbyAscensionKey));

                this.lobbies.add(data);
            }

            this.lobbies.sort(new LobbyData.LobbyDataComparer());
            page = 0;
            hoveredIndex = -1;
            maxPage = this.lobbies.size() / LOBBIES_PER_PAGE;
        }
            //debug code for viewing layout
            /*while (this.lobbies.size() < LOBBIES_PER_PAGE * 2.5)
            {
                LobbyData testData = new LobbyData();
                testData.id = null;
                testData.isPublic = MathUtils.randomBoolean();
                testData.hostIsMokou = MathUtils.randomBoolean();
                testData.name = "Lobby " + (this.lobbies.size() + 1);
                this.lobbies.add(testData);
            }
            this.lobbies.sort(new LobbyData.LobbyDataComparer());
            maxPage = this.lobbies.size() / LOBBIES_PER_PAGE;

            */
    }

    public void receivePassword(String pword)
    {
        if (receivePassword && waitingLobby != null && waitingLobby.id.isValid()) //Of note: This is unsecure.
        {
            //To make it more secure, the password wouldn't be in lobby data, it would be stored locally.
            //Attempted password attempts would send it to the host, who would send back whether or not it was correct.
            //But, I'm too lazy to do that uwu
            //And it's not that important, I think, for a mod like this.

            receivePassword = false;

            if (matchmaking.getLobbyData(waitingLobby.id, HandleMatchmaking.lobbyPasswordKey).equals(pword))
            {
                matchmaking.joinLobby(waitingLobby.id);
                HandleMatchmaking.isHost = false;
                HandleMatchmaking.joinorcreate = true;
            }
            else
            {
                mode = 0;
                chat.receiveMessage(TEXT[16]);
            }
        }
    }

    public void hide()
    {
        this.visible = false;
        mode = 0;
    }

    public void displayLobbyInfo(ActiveLobbyData data)
    {
        mode = 4;

        inLobby = data;
    }

    public void update()
    {
        if (visible)
        {
            switch (mode)
            {
                case 0:
                    refreshButtonHitbox.update();
                    createButtonHitbox.update();
                    lobbyAreaHitbox.update();

                    if (refreshButtonHitbox.hovered) {
                        refreshButtonScale = 1.15f;
                        refreshButtonColor.r = 1.0f;
                        refreshButtonColor.g = 1.0f;
                        refreshButtonColor.b = 1.0f;
                    }
                    else
                    {
                        refreshButtonScale = 1.0f;
                        refreshButtonColor.r = 0.8f;
                        refreshButtonColor.g = 0.8f;
                        refreshButtonColor.b = 0.8f;
                    }

                    if (createButtonHitbox.hovered) {
                        createButtonScale = 1.15f;
                        createButtonColor.r = 1.0f;
                        createButtonColor.g = 1.0f;
                        createButtonColor.b = 1.0f;
                    }
                    else
                    {
                        createButtonScale = 1.0f;
                        createButtonColor.r = 0.8f;
                        createButtonColor.g = 0.8f;
                        createButtonColor.b = 0.8f;
                    }

                    hoveredIndex = -1;
                    if (refreshButtonHitbox.hovered && InputHelper.justClickedLeft)
                    {
                        InputHelper.justClickedLeft = false;
                        HandleMatchmaking.startFindLobby();
                        searching = true;

                        receivePassword = false;
                        if (chat.active)
                        {
                            chat.active = false;
                            mirthandmalice.patch.input.InputHelper.reset();
                        }

                        this.lobbies.clear();
                    }
                    else if (createButtonHitbox.hovered && InputHelper.justClickedLeft)
                    {
                        InputHelper.justClickedLeft = false;

                        if (!publicRoom.enabled)
                            publicRoom.toggle();

                        nameInput.reset();
                        nameInput.setText(CardCrawlGame.playerName + TEXT[10]);
                        passwordInput.reset();

                        receivePassword = false;
                        if (chat.active)
                        {
                            chat.active = false;
                            mirthandmalice.patch.input.InputHelper.reset();
                        }

                        this.isAscensionMode = Settings.gamePref.getBoolean("Ascension Mode Default", false);

                        this.mode = 2;
                        /*
                        HandleMatchmaking.joinorcreate = true;
                        logger.info("Attempting to create a new lobby.");
                        logger.info("Creating public lobby for 2 players.");
                        chat.receiveMessage("Creating new lobby.");
                        matchmaking.createLobby(SteamMatchmaking.LobbyType.Public,2);*/
                    }
                    else if (lobbyAreaHitbox.hovered && !searching && !receivePassword)
                    {
                        int max = Math.min(lobbies.size(), (page * LOBBIES_PER_PAGE + LOBBIES_PER_PAGE));

                        int index = 0;
                        for (int i = page * LOBBIES_PER_PAGE; i < max && index < lobbyHitboxes.size(); ++i)
                        {
                            lobbyHitboxes.get(index).update();

                            if (lobbyHitboxes.get(index).hovered)
                            {
                                hoveredIndex = i;
                                if (InputHelper.justClickedLeft)
                                {
                                    InputHelper.justClickedLeft = false;
                                    if (lobbies.get(i).id != null && lobbies.get(i).id.isValid())
                                    {
                                        mode = 1;
                                        logger.info("Joining...");

                                        if (lobbies.get(i).isPublic)
                                        {
                                            matchmaking.joinLobby(lobbies.get(i).id);
                                            HandleMatchmaking.isHost = false;
                                            HandleMatchmaking.joinorcreate = true;
                                        }
                                        else
                                        {
                                            receivePassword = true;
                                            waitingLobby = lobbies.get(i);
                                            chat.receiveMessage(TEXT[14]);
                                            chat.active = true;
                                        }
                                    }
                                    else
                                    {
                                        lobbies.get(i).invalidate(TEXT[7]);
                                    }
                                }
                            }
                            ++index;
                        }
                    }

                    nextArrowHitbox.update();
                    prevArrowHitbox.update();

                    if (nextArrowHitbox.hovered && InputHelper.justClickedLeft)
                    {
                        InputHelper.justClickedLeft = false;
                        if (page < maxPage)
                            ++page;
                    }
                    else if (prevArrowHitbox.hovered && InputHelper.justClickedLeft)
                    {
                        InputHelper.justClickedLeft = false;
                        if (page > 0)
                            --page;
                    }
                    break;
                case 2: //creating
                    refreshButtonHitbox.update();
                    createButtonHitbox.update();
                    lobbyAreaHitbox.update();

                    if (refreshButtonHitbox.hovered) {
                        refreshButtonScale = 1.15f;
                        refreshButtonColor.r = 1.0f;
                        refreshButtonColor.g = 1.0f;
                        refreshButtonColor.b = 1.0f;
                    }
                    else
                    {
                        refreshButtonScale = 1.0f;
                        refreshButtonColor.r = 0.8f;
                        refreshButtonColor.g = 0.8f;
                        refreshButtonColor.b = 0.8f;
                    }

                    if (createButtonHitbox.hovered) {
                        createButtonScale = 1.15f;
                        createButtonColor.r = 1.0f;
                        createButtonColor.g = 1.0f;
                        createButtonColor.b = 1.0f;
                    }
                    else
                    {
                        createButtonScale = 1.0f;
                        createButtonColor.r = 0.8f;
                        createButtonColor.g = 0.8f;
                        createButtonColor.b = 0.8f;
                    }

                    hoveredIndex = -1;
                    if (refreshButtonHitbox.hovered && InputHelper.justClickedLeft)
                    {
                        InputHelper.justClickedLeft = false;
                        HandleMatchmaking.startFindLobby();
                        searching = true;
                        mode = 0;
                        this.lobbies.clear();
                    }
                    else
                    {
                        nameInput.update();
                        publicRoom.update();
                        lobbyCreateHitbox.update();



                        if (lobbyCreateHitbox.hovered && InputHelper.justClickedLeft)
                        {
                            mode = 3;

                            if (mirthToggle.enabled)
                            {
                                HandleMatchmaking.isMirth = true;
                            }
                            else if (maliceToggle.enabled)
                            {
                                HandleMatchmaking.isMirth = false;
                            }
                            else if (randomToggle.enabled)
                            {
                                HandleMatchmaking.isMirth = MathUtils.randomBoolean();
                            }

                            //Set character
                            for (int i = 0; i < CardCrawlGame.characterManager.getAllCharacters().size(); ++i)
                            {
                                if (CardCrawlGame.characterManager.getAllCharacters().get(i) instanceof MirthAndMalice)
                                {
                                    if (((MirthAndMalice) CardCrawlGame.characterManager.getAllCharacters().get(i)).isMirth ^ HandleMatchmaking.isMirth) //doesn't match
                                    {
                                        ((MirthAndMalice) CardCrawlGame.characterManager.getAllCharacters().get(i)).setMirth(HandleMatchmaking.isMirth);
                                    }
                                }
                            }

                            HandleMatchmaking.joinorcreate = true;
                            logger.info("Attempting to create a new lobby.");
                            chat.receiveMessage("Creating new lobby.");
                            matchmaking.createLobby(SteamMatchmaking.LobbyType.Public, 2);
                            break;
                        }

                        if (!publicRoom.enabled)
                        {
                            passwordInput.update();
                        }

                        if (!mirthToggle.enabled)
                        {
                            mirthToggle.update();
                            if (mirthToggle.enabled)
                            {
                                if (maliceToggle.enabled)
                                    maliceToggle.toggle();
                                if (randomToggle.enabled)
                                    randomToggle.toggle();
                            }
                        }
                        else
                        {
                            mirthToggle.hb.hovered = false;
                        }

                        if (!maliceToggle.enabled)
                        {
                            maliceToggle.update();
                            if (maliceToggle.enabled)
                            {
                                if (mirthToggle.enabled)
                                    mirthToggle.toggle();
                                if (randomToggle.enabled)
                                    randomToggle.toggle();
                            }
                        }
                        else
                        {
                            maliceToggle.hb.hovered = false;
                        }

                        if (!randomToggle.enabled)
                        {
                            randomToggle.update();
                            if (randomToggle.enabled)
                            {
                                if (mirthToggle.enabled)
                                    mirthToggle.toggle();
                                if (maliceToggle.enabled)
                                    maliceToggle.toggle();
                            }
                        }
                        else
                        {
                            randomToggle.hb.hovered = false;
                        }

                        this.ascensionModeHb.update();
                        this.ascRightHb.update();
                        this.ascLeftHb.update();

                        if (InputHelper.justClickedLeft) {
                            if (this.ascensionModeHb.hovered) {
                                this.ascensionModeHb.clickStarted = true;
                            }
                            else if (isAscensionMode) {
                                if (this.ascRightHb.hovered) {
                                    this.ascRightHb.clickStarted = true;
                                } else if (this.ascLeftHb.hovered) {
                                    this.ascLeftHb.clickStarted = true;
                                }
                            }
                        }

                        if (this.ascensionModeHb.clicked || CInputActionSet.proceed.isJustPressed()) {
                            this.ascensionModeHb.clicked = false;
                            this.isAscensionMode = !this.isAscensionMode;
                            Settings.gamePref.putBoolean("Ascension Mode Default", this.isAscensionMode);
                            Settings.gamePref.flush();
                        }

                        if (this.ascLeftHb.clicked || CInputActionSet.pageLeftViewDeck.isJustPressed()) {
                            this.ascLeftHb.clicked = false;// 240

                            for (CharacterOption o : CardCrawlGame.mainMenuScreen.charSelectScreen.options)
                            {
                                if (o.selected) {
                                    o.decrementAscensionLevel(--this.ascensionLevel);
                                    this.ascensionLevel = Math.max(0, this.ascensionLevel);
                                    break;
                                }
                            }
                        }

                        if (this.ascRightHb.clicked || CInputActionSet.pageRightViewExhaust.isJustPressed()) {
                            this.ascRightHb.clicked = false;

                            for (CharacterOption o : CardCrawlGame.mainMenuScreen.charSelectScreen.options)
                            {
                                if (o.selected) {
                                    o.incrementAscensionLevel(++this.ascensionLevel);
                                    this.ascensionLevel = Math.min(this.ascensionLevel, (int)ReflectionHacks.getPrivate(o, CharacterOption.class, "maxAscensionLevel"));
                                    break;
                                }
                            }

                        }

                        AbstractDungeon.isAscensionMode = this.isAscensionMode;
                        if (this.isAscensionMode) {
                            AbstractDungeon.ascensionLevel = this.ascensionLevel;
                        } else {
                            AbstractDungeon.ascensionLevel = 0;
                        }
                    }

                    break;
                case 4: //Lobby view.
                    if (isHost)
                    {
                        lobbyCreateHitbox.update();

                        if (lobbyCreateHitbox.hovered && InputHelper.justClickedLeft)
                        {
                            this.hide();
                            beginGameStartTimer();
                        }
                    }
                    break;
            }
        }
    }

    public void render(SpriteBatch sb)
    {
        if (visible)
        {
            sb.setColor(Color.WHITE);
            sb.draw(panelBack, PANEL_X, PANEL_Y, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, Settings.scale, Settings.scale, 0, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, false, false);

            sb.setColor(refreshButtonColor);
            sb.draw(refreshButton, REFRESH_X, BUTTON_Y, BUTTON_OFFSET, BUTTON_OFFSET, BUTTON_SIZE, BUTTON_SIZE, refreshButtonScale * Settings.scale, refreshButtonScale * Settings.scale, 0, 0, 0, BUTTON_SIZE, BUTTON_SIZE, false, false);

            sb.setColor(createButtonColor);
            sb.draw(createButton, CREATE_X, BUTTON_Y, BUTTON_OFFSET, BUTTON_OFFSET, BUTTON_SIZE, BUTTON_SIZE, createButtonScale * Settings.scale, createButtonScale * Settings.scale, 0, 0, 0, BUTTON_SIZE, BUTTON_SIZE, false, false);

            sb.setColor(Color.WHITE);


            if (receivePassword)
            {
                FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, TEXT[15], PANEL_CENTER_X, PANEL_CENTER_Y, Color.WHITE);
            }
            else if (mode == 1)
            {
                FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, TEXT[8], PANEL_CENTER_X, PANEL_CENTER_Y, Color.WHITE);
            }
            else if (searching)
            {
                FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, TEXT[6], PANEL_CENTER_X, PANEL_CENTER_Y, Color.WHITE);
            }
            else if (mode == 2) //creating
            {
                renderCreationUI(sb);
            }
            else if (lobbies.isEmpty())
            {
                FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, TEXT[3], PANEL_CENTER_X, PANEL_CENTER_Y, Color.WHITE);
            }
            else if (mode == 0)
            {
                renderLobbySelect(sb);
            }
            else if (mode == 3)
            {
                FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, TEXT[22], PANEL_CENTER_X, PANEL_CENTER_Y, Color.WHITE);
            }
            else if (mode == 4) //in lobby.
            {
                renderLobbyInfo(sb);
            }
        }
    }

    private void renderLobbyInfo(SpriteBatch sb)
    {
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.buttonLabelFont, inLobby.name, NAME_LABEL_X, OTHER_RENDER_LABEL_Y, Color.WHITE);
        FontHelper.renderFontCenteredTopAligned(sb, FontHelper.buttonLabelFont, "|", PUBLIC_LABEL_X, LABEL_Y, Color.GOLD);
        FontHelper.renderFontCenteredTopAligned(sb, FontHelper.buttonLabelFont, "Ascension " + (inLobby.ascension != 0 ? inLobby.ascension : "Off"), PUBLIC_LABEL_X + 50.0f * Settings.scale, LABEL_Y, Color.GOLD);


        float y = LOBBY_START_Y;
        float x = NAME_LABEL_X + ICON_W;

        FontHelper.renderFontLeftTopAligned(sb, FontHelper.buttonLabelFont, inLobby.hostName, x, y + OTHER_RENDER_OFFSET, Color.GOLD);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.buttonLabelFont, inLobby.hostIsMirth ? characterStrings.NAMES[1] : characterStrings.NAMES[2], CHARACTER_LABEL_X, y + OTHER_RENDER_OFFSET, Color.GOLD);
        y -= LINE_HEIGHT;

        FontHelper.renderFontLeftTopAligned(sb, FontHelper.buttonLabelFont, inLobby.otherName, x, y + OTHER_RENDER_OFFSET, Color.WHITE);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.buttonLabelFont, inLobby.hostIsMirth ? characterStrings.NAMES[2] : characterStrings.NAMES[1], CHARACTER_LABEL_X, y + OTHER_RENDER_OFFSET, Color.WHITE);

        if (isHost)
            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, TEXT[21], PANEL_CENTER_X, CREATE_TEXT_Y, lobbyCreateHitbox.hovered ? Color.GOLD : Color.WHITE);
    }

    private void renderLobbySelect(SpriteBatch sb)
    {
        if (page < maxPage)
        {
            sb.draw(arrow, ARROW_X, NEXT_ARROW_Y, ARROW_OFFSET_X, ARROW_OFFSET_Y, ARROW_WIDTH, ARROW_HEIGHT, Settings.scale * (nextArrowHitbox.hovered ? 1.1f: 1.0f), Settings.scale * (nextArrowHitbox.hovered ? 1.1f: 1.0f), 0, 0, 0, ARROW_WIDTH, ARROW_HEIGHT, false, false);
        }
        if (page > 0)
        {
            sb.draw(arrow, ARROW_X, PREV_ARROW_Y, ARROW_OFFSET_X, ARROW_OFFSET_Y, ARROW_WIDTH, ARROW_HEIGHT, Settings.scale * (prevArrowHitbox.hovered ? 1.1f: 1.0f), Settings.scale * (prevArrowHitbox.hovered ? 1.1f: 1.0f), 0, 0, 0, ARROW_WIDTH, ARROW_HEIGHT, false, true);
        }

        FontHelper.renderFontLeftTopAligned(sb, FontHelper.buttonLabelFont, TEXT[0], NAME_LABEL_X, OTHER_RENDER_LABEL_Y, Color.GOLD);
        FontHelper.renderFontCenteredTopAligned(sb, FontHelper.buttonLabelFont, TEXT[1], PUBLIC_LABEL_X, LABEL_Y, Color.GOLD);
        sb.draw(ImageMaster.TP_ASCENSION, ASCENSION_SYMBOL_X - ICON_W / 2.0f, LABEL_Y - ICON_W / 2.0f, ICON_W / 2.0f, ICON_W / 2.0f, ICON_W, ICON_W, Settings.scale, Settings.scale, 0, 0, 0, (int) ICON_W, (int) ICON_W, false, false);
        FontHelper.renderFontCenteredTopAligned(sb, FontHelper.buttonLabelFont, TEXT[2], CHARACTER_LABEL_X, LABEL_Y, Color.GOLD);

        int max = Math.min(lobbies.size(), (page * LOBBIES_PER_PAGE + LOBBIES_PER_PAGE));
        float y = LOBBY_START_Y;
        Color textColor;
        for (int i = page * LOBBIES_PER_PAGE; i < max; ++i)
        {
            if (hoveredIndex == i && lobbies.get(i).isValid)
            {
                textColor = Color.GOLD;
            }
            else
            {
                textColor = Color.WHITE;
            }
            FontHelper.renderFontLeftTopAligned(sb, FontHelper.buttonLabelFont, lobbies.get(i).name, NAME_LABEL_X, y + OTHER_RENDER_OFFSET, textColor);
            if (lobbies.get(i).isValid)
            {
                FontHelper.renderFontCenteredTopAligned(sb, FontHelper.buttonLabelFont, lobbies.get(i).isPublic ? TEXT[4] : TEXT[5], PUBLIC_LABEL_X, y, textColor);
                FontHelper.renderFontCenteredTopAligned(sb, FontHelper.buttonLabelFont, String.valueOf(lobbies.get(i).ascension), ASCENSION_SYMBOL_X, y, textColor);
                FontHelper.renderFontCenteredTopAligned(sb, FontHelper.buttonLabelFont, lobbies.get(i).hostIsMirth ? characterStrings.NAMES[1] : characterStrings.NAMES[2], CHARACTER_LABEL_X, y, textColor);
            }
            y -= LINE_HEIGHT;
        }
    }

    private void renderCreationUI(SpriteBatch sb)
    {
        //name
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.buttonLabelFont, TEXT[0], NAME_LABEL_X, LABEL_Y, Color.WHITE);
        nameInput.render(sb);

        //public
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.buttonLabelFont, TEXT[11], MOKOU_LABEL_X, PUBLIC_INPUT_TEXT_Y, Color.WHITE);
        publicRoom.render(sb);

        if (!publicRoom.enabled)
        {
            //password
            FontHelper.renderFontLeftTopAligned(sb, FontHelper.buttonLabelFont, TEXT[9], NAME_LABEL_X, PASSWORD_LABEL_Y, Color.WHITE);
            passwordInput.render(sb);
        }

        //character
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.buttonLabelFont, characterStrings.NAMES[1], MOKOU_LABEL_X, CHARACTER_LABEL_Y, Color.WHITE);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.buttonLabelFont, characterStrings.NAMES[2], KEINE_LABEL_X, CHARACTER_LABEL_Y, Color.WHITE);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.buttonLabelFont, TEXT[12], RANDOM_LABEL_X, CHARACTER_LABEL_Y, Color.WHITE);
        mirthToggle.render(sb);
        maliceToggle.render(sb);
        randomToggle.render(sb);

        //ascension
        renderAscensionOptions(sb);

        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, TEXT[13], PANEL_CENTER_X, CREATE_TEXT_Y, lobbyCreateHitbox.hovered ? Color.GOLD : Color.WHITE);
    }

    private void renderAscensionOptions(SpriteBatch sb)
    {
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.OPTION_TOGGLE, PANEL_CENTER_X - ASC_LEFT_W - 16.0F - 30.0F * Settings.scale, PANEL_Y + 200.0F * Settings.scale - 16.0F, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);// 533
        if (this.ascensionModeHb.hovered) {
            FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, TEXT[17], PANEL_CENTER_X - ASC_LEFT_W / 2.0F, PANEL_Y + 200.0F * Settings.scale, Settings.GREEN_TEXT_COLOR);
            TipHelper.renderGenericTip((float)InputHelper.mX - 140.0F * Settings.scale, (float)InputHelper.mY + 340.0F * Settings.scale, TEXT[19], TEXT[20]);
        } else {
            FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, TEXT[17], PANEL_CENTER_X - ASC_LEFT_W / 2.0F, PANEL_Y + 200.0F * Settings.scale, Settings.GOLD_COLOR);
        }

        FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, TEXT[18] + this.ascensionLevel, PANEL_CENTER_X + ASC_RIGHT_W / 2.0F + 200.0F * Settings.scale, PANEL_Y + 200.0F * Settings.scale, Settings.BLUE_TEXT_COLOR);
        if (this.isAscensionMode) {
            sb.setColor(Color.WHITE);
            sb.draw(ImageMaster.OPTION_TOGGLE_ON, PANEL_CENTER_X - ASC_LEFT_W - 16.0F - 30.0F * Settings.scale, PANEL_Y + 200.0F * Settings.scale - 16.0F, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);


            if (!this.ascLeftHb.hovered && !Settings.isControllerMode) {
                sb.setColor(Color.LIGHT_GRAY);
            } else {
                sb.setColor(Color.WHITE);
            }

            sb.draw(ImageMaster.CF_LEFT_ARROW, this.ascLeftHb.cX - 24.0F, this.ascLeftHb.cY - 24.0F, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 48, 48, false, false);
            if (!this.ascRightHb.hovered && !Settings.isControllerMode) {
                sb.setColor(Color.LIGHT_GRAY);
            } else {
                sb.setColor(Color.WHITE);
            }

            sb.draw(ImageMaster.CF_RIGHT_ARROW, this.ascRightHb.cX - 24.0F, this.ascRightHb.cY - 24.0F, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 48, 48, false, false);
            if (Settings.isControllerMode) {
                sb.draw(CInputActionSet.proceed.getKeyImg(), this.ascensionModeHb.cX - 100.0F * Settings.scale - 32.0F, this.ascensionModeHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
                sb.draw(CInputActionSet.pageLeftViewDeck.getKeyImg(), this.ascLeftHb.cX - 60.0F * Settings.scale - 32.0F, this.ascLeftHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
                sb.draw(CInputActionSet.pageRightViewExhaust.getKeyImg(), this.ascRightHb.cX + 60.0F * Settings.scale - 32.0F, this.ascRightHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
            }
        }

        this.ascensionModeHb.render(sb);
        this.ascLeftHb.render(sb);
        this.ascRightHb.render(sb);
    }
}
