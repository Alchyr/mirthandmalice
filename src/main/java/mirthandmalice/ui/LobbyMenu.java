package mirthandmalice.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.options.ToggleButton;
import mirthandmalice.character.MirthAndMalice;
import mirthandmalice.patch.enums.ToggleType;
import mirthandmalice.util.HandleMatchmaking;
import mirthandmalice.util.LobbyData;
import mirthandmalice.util.TextureLoader;

import java.util.ArrayList;

import static mirthandmalice.character.MirthAndMalice.characterStrings;
import static mirthandmalice.util.HandleMatchmaking.matchmaking;
import static mirthandmalice.util.HandleMatchmaking.metadataTrue;
import static mirthandmalice.MirthAndMaliceMod.*;

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
    private static final float CHARACTER_LABEL_X = PANEL_X + 660.0f * Settings.scale;

    private static final float CHARACTER_LABEL_Y = PANEL_Y + 312.0f * Settings.scale; //for lobby creation
    private static final float MOKOU_LABEL_X = NAME_LABEL_X + 80.0f * Settings.scale;
    private static final float KEINE_LABEL_X = MOKOU_LABEL_X + 200.0f * Settings.scale;
    private static final float RANDOM_LABEL_X = KEINE_LABEL_X + 200.0f * Settings.scale;

    private static final float CREATE_TEXT_Y = PANEL_Y + 100.0f * Settings.scale;
    private static final float CREATE_TEXT_WIDTH = FontHelper.getWidth(FontHelper.buttonLabelFont, TEXT[13], 1);

    private static final float PUBLIC_TOGGLE_X = PANEL_X + 130.0f * Settings.scale;
    private static final float PUBLIC_TOGGLE_Y = PANEL_Y + 450.0f * Settings.scale;

    private static final float MOKOU_TOGGLE_X = PUBLIC_TOGGLE_X;
    private static final float KEINE_TOGGLE_X = MOKOU_TOGGLE_X + 200.0f * Settings.scale;
    private static final float RANDOM_TOGGLE_X = KEINE_TOGGLE_X + 200.0f * Settings.scale;
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

    public LobbyTextInput nameInput = new LobbyTextInput(INPUT_X, LOBBY_START_Y);
    public ToggleButton publicRoom = new ToggleButton(PUBLIC_TOGGLE_X, 0, PUBLIC_TOGGLE_Y, ToggleType.PUBLIC_LOBBY, false);
    public LobbyTextInput passwordInput = new LobbyTextInput(INPUT_X, PASSWORD_INPUT_Y);

    private ToggleButton mokouToggle = new ToggleButton(MOKOU_TOGGLE_X, 0, CHARACTER_TOGGLE_Y, ToggleType.PUBLIC_LOBBY, false);
    private ToggleButton keineToggle = new ToggleButton(KEINE_TOGGLE_X, 0, CHARACTER_TOGGLE_Y, ToggleType.PUBLIC_LOBBY, false);
    private ToggleButton randomToggle = new ToggleButton(RANDOM_TOGGLE_X, 0, CHARACTER_TOGGLE_Y, ToggleType.PUBLIC_LOBBY, false);

    public boolean searching = false;
    public boolean receivePassword = false;
    private LobbyData waitingLobby = null;

    public LobbyMenu()
    {
        float x = NAME_LABEL_X - 5.0f * Settings.scale;
        float y = LOBBY_START_Y;
        for (int i = 0; i < LOBBIES_PER_PAGE; ++i)
        {
            lobbyHitboxes.add(new Hitbox(x, y - OTHER_RENDER_OFFSET, LOBBY_WIDTH, LINE_HEIGHT - 6.0f * Settings.scale));
            y -= LINE_HEIGHT;
        }

        mokouToggle.toggle();
        keineToggle.toggle();
    }

    public void show(boolean searching)
    {
        this.lobbies.clear();
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
        searching = false;
        receivePassword = false;

        if (!lobbies.isEmpty()) {
            for (SteamID lobby : lobbies) {
                LobbyData data = new LobbyData();

                data.id = lobby;
                data.name = matchmaking.getLobbyData(lobby, HandleMatchmaking.lobbyNameKey);
                data.hostIsMokou = matchmaking.getLobbyData(lobby, HandleMatchmaking.hostIsMokouKey).equals(metadataTrue);
                data.isPublic = matchmaking.getLobbyData(lobby, HandleMatchmaking.lobbyPublicKey).equals(metadataTrue);

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
        if (receivePassword && waitingLobby != null && waitingLobby.id.isValid())
        {
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

    public void update()
    {
        if (visible)
        {
            refreshButtonHitbox.update();
            createButtonHitbox.update();
            lobbyAreaHitbox.update();

            switch (mode)
            {
                case 0:
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

                            if (mokouToggle.enabled)
                            {
                                HandleMatchmaking.isMokou = true;
                            }
                            else if (keineToggle.enabled)
                            {
                                HandleMatchmaking.isMokou = false;
                            }
                            else if (randomToggle.enabled)
                            {
                                HandleMatchmaking.isMokou = MathUtils.randomBoolean();
                            }

                            //Set character
                            for (int i = 0; i < CardCrawlGame.characterManager.getAllCharacters().size(); ++i)
                            {
                                if (CardCrawlGame.characterManager.getAllCharacters().get(i) instanceof MirthAndMalice)
                                {
                                    if (((MirthAndMalice) CardCrawlGame.characterManager.getAllCharacters().get(i)).isMirth ^ HandleMatchmaking.isMokou) //doesn't match
                                    {
                                        ((MirthAndMalice) CardCrawlGame.characterManager.getAllCharacters().get(i)).setMirth(HandleMatchmaking.isMokou);
                                    }
                                }
                            }

                            HandleMatchmaking.joinorcreate = true;
                            logger.info("Attempting to create a new lobby.");
                            chat.receiveMessage("Creating new lobby.");
                            matchmaking.createLobby(SteamMatchmaking.LobbyType.Public, 2);
                            this.hide();
                            break;
                        }

                        if (!publicRoom.enabled)
                        {
                            passwordInput.update();
                        }

                        if (!mokouToggle.enabled)
                        {
                            mokouToggle.update();
                            if (mokouToggle.enabled)
                            {
                                if (keineToggle.enabled)
                                    keineToggle.toggle();
                                if (randomToggle.enabled)
                                    randomToggle.toggle();
                            }
                        }
                        else
                        {
                            mokouToggle.hb.hovered = false;
                        }

                        if (!keineToggle.enabled)
                        {
                            keineToggle.update();
                            if (keineToggle.enabled)
                            {
                                if (mokouToggle.enabled)
                                    mokouToggle.toggle();
                                if (randomToggle.enabled)
                                    randomToggle.toggle();
                            }
                        }
                        else
                        {
                            keineToggle.hb.hovered = false;
                        }

                        if (!randomToggle.enabled)
                        {
                            randomToggle.update();
                            if (randomToggle.enabled)
                            {
                                if (mokouToggle.enabled)
                                    mokouToggle.toggle();
                                if (keineToggle.enabled)
                                    keineToggle.toggle();
                            }
                        }
                        else
                        {
                            randomToggle.hb.hovered = false;
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
                mokouToggle.render(sb);
                keineToggle.render(sb);
                randomToggle.render(sb);

                FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, TEXT[13], PANEL_CENTER_X, CREATE_TEXT_Y, lobbyCreateHitbox.hovered ? Color.GOLD : Color.WHITE);
            }
            else if (lobbies.isEmpty())
            {
                FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, TEXT[3], PANEL_CENTER_X, PANEL_CENTER_Y, Color.WHITE);
            }
            else if (mode == 0)
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
                        FontHelper.renderFontCenteredTopAligned(sb, FontHelper.buttonLabelFont, lobbies.get(i).hostIsMokou ? characterStrings.NAMES[1] : characterStrings.NAMES[2], CHARACTER_LABEL_X, y, textColor);
                    }
                    y -= LINE_HEIGHT;
                }
            }
        }
    }
}
