package mirthandmalice.character;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomPlayer;
import basemod.animations.SpriterAnimation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.cards.curses.Necronomicurse;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.CorruptionPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.BurningBlood;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbInterface;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.CardDisappearEffect;
import mirthandmalice.actions.character.OtherPlayerDeckShuffleAction;
import mirthandmalice.cards.malice.basic.Caution;
import mirthandmalice.cards.malice.basic.MaliceDefend;
import mirthandmalice.cards.malice.basic.MaliceStrike;
import mirthandmalice.cards.malice.basic.Wilt;
import mirthandmalice.cards.malice.uncommon.Originate;
import mirthandmalice.cards.malice.uncommon.Forget;
import mirthandmalice.cards.mirth.basic.ImitatedInnocence;
import mirthandmalice.cards.mirth.basic.Indulgence;
import mirthandmalice.cards.mirth.basic.MirthDefend;
import mirthandmalice.cards.mirth.basic.MirthStrike;
import mirthandmalice.cards.mirth.common.Scorch;
import mirthandmalice.effects.MaliceParticleEffect;
import mirthandmalice.effects.MirthMaliceAuraEffect;
import mirthandmalice.effects.MirthParticleEffect;
import mirthandmalice.patch.energy_division.SetEnergyGain;
import mirthandmalice.patch.energy_division.TrackCardSource;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.patch.fortune_misfortune.FortuneMisfortune;
import mirthandmalice.patch.game_initialize.EnergyFontGen;
import mirthandmalice.patch.manifestation.ManifestField;
import mirthandmalice.ui.AstrologerOrb;
import mirthandmalice.ui.MokouOrb;
import mirthandmalice.ui.OtherEnergyPanel;
import mirthandmalice.util.*;

import java.lang.reflect.Method;
import java.util.*;

import static mirthandmalice.ui.OtherDrawPilePanel.OTHER_DRAW_OFFSET;
import static mirthandmalice.MirthAndMaliceMod.*;

public class MirthAndMalice extends CustomPlayer {
    public static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString(makeID("MirthAndMalice"));

    private static final Color ARROW_COLOR = new Color(1.0F, 0.2F, 0.3F, 1.0F);

    // Base stats
    private static final int ENERGY_PER_TURN = 3; //Total of 6 energy and 8 cards base. As such, cards are relatively weaker.
    private static final int STARTING_HP = 60;
    private static final int MAX_HP = 60;
    private static final int STARTING_GOLD = 77; //Both players have individual gold, so this is actually 77x2.
    private static final int CARD_DRAW = 8; //automatically split between two groups
    private static final int ORB_SLOTS = 0;

    private static final String SpritePath = assetPath("img/character/spriter/Character.scml");

    public boolean isMirth;
    private float manifestParticleTimer = 0;
    private float manifestSmallParticleTimer = 0;

    private AbstractCard.CardColor cardColor;
    private BitmapFont energyFont;

    private Color cardRenderColor;// = Color.WHITE.cpy();
    private Color cardTrailColor;// = Color.GOLD.cpy();
    private Color slashAttackColor;// = Color.GOLDENROD.cpy();

    private boolean mirthDraw;

    protected EnergyOrbInterface otherPlayerOrb;

    public CardGroup otherPlayerMasterDeck;
    public CardGroup otherPlayerHand;
    public CardGroup otherPlayerDraw;
    public CardGroup otherPlayerDiscard;
    public CardGroup fakeLimbo;

    private static Method hoverReticleMethod;

    static {
        try
        {
            hoverReticleMethod = AbstractPlayer.class.
                    getDeclaredMethod("renderHoverReticle", SpriteBatch.class);

            hoverReticleMethod.setAccessible(true);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
    }

    //Just to track what potions they have, for ensuring potions aren't played more than they should be.
    //Other player should send a list of potions ids whenever they obtain or use a potion.

    private Vector2[] points;

    public MirthAndMalice(boolean mirth) {
        super(mirth ? characterStrings.NAMES[1] : characterStrings.NAMES[2], CharacterEnums.MIRTHMALICE, mirth ? new MokouOrb() : new AstrologerOrb(),
                new SpriterAnimation(SpritePath));



        this.points = (Vector2[])ReflectionHacks.getPrivate(this, AbstractPlayer.class, "points");

        otherPlayerOrb = mirth ? new AstrologerOrb() : new MokouOrb();

        this.hand = new AltHandCardgroup(true);
        otherPlayerHand = new AltHandCardgroup(false);
        otherPlayerDraw = new CardGroup(CardGroup.CardGroupType.DRAW_PILE);
        otherPlayerDiscard = new CardGroup(CardGroup.CardGroupType.DISCARD_PILE);
        otherPlayerMasterDeck = new CardGroup(CardGroup.CardGroupType.MASTER_DECK);
        fakeLimbo = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

        this.isMirth = mirth;
        this.mirthDraw = true;

        this.title = getTitle(this.chosenClass);

        this.energyFont = EnergyFontGen.yinYangEnergyFont;

        if (mirth)
        {
            this.cardColor = CharacterEnums.MIRTHMALICE_MIRTH;
            this.cardTrailColor = Color.WHITE.cpy();
            this.cardRenderColor = MIRTH_COLOR.cpy();
            this.slashAttackColor = MIRTH_COLOR.cpy();
        }
        else
        {
            this.cardColor = CharacterEnums.MIRTHMALICE_MALICE;
            this.cardTrailColor = Color.BLACK.cpy();
            this.cardRenderColor = MALICE_COLOR.cpy();
            this.slashAttackColor = MALICE_COLOR.cpy();
        }

        // =============== TEXTURES, ENERGY, LOADOUT =================

        initializeClass(null, // required call to load textures and setup energy/loadout
                assetPath("img/character/shoulder.png"), // campfire pose
                assetPath("img/character/shoulder2.png"), // another campfire pose
                assetPath("img/character/corpse.png"), // dead corpse
                getLoadout(), 20.0F, -10.0F, 220.0F, 290.0F, new AltEnergyManager(ENERGY_PER_TURN)); // energy manager

        // =============== TEXT BUBBLE LOCATION =================

        this.dialogX = (this.drawX + 0.0F * Settings.scale); // set location for text bubbles
        this.dialogY = (this.drawY + 120.0F * Settings.scale);
    }

    public void setMirth(boolean isMirth)
    {
        this.isMirth = isMirth;

        this.title = getTitle(this.chosenClass);

        if (isMirth)
        {
            this.name = characterStrings.NAMES[1];
            this.energyOrb = new MokouOrb();
            this.otherPlayerOrb = new AstrologerOrb();
            this.cardColor = CharacterEnums.MIRTHMALICE_MIRTH;
            this.cardTrailColor = Color.RED.cpy();
            this.cardRenderColor = MIRTH_COLOR.cpy();
            this.slashAttackColor = MIRTH_COLOR.cpy();
        }
        else
        {
            this.name = characterStrings.NAMES[2];
            this.energyOrb = new AstrologerOrb();
            this.otherPlayerOrb = new MokouOrb();
            this.cardColor = CharacterEnums.MIRTHMALICE_MALICE;
            this.cardTrailColor = Color.BLUE.cpy();
            this.cardRenderColor = MALICE_COLOR.cpy();
            this.slashAttackColor = MALICE_COLOR.cpy();
        }

        if (AbstractDungeon.topPanel != null)
        {
            ReflectionHacks.setPrivate(AbstractDungeon.topPanel, TopPanel.class, "title", this.title);
        }

        logger.info("Set character to " + this.name);
    }

    @Override
    public void preBattlePrep() {
        this.otherPlayerHand.clear();
        this.otherPlayerDiscard.clear();
        this.mirthDraw = true;

        super.preBattlePrep();
    }

    @Override
    public void combatUpdate() {
        super.combatUpdate();

        manifestParticleTimer -= Gdx.graphics.getDeltaTime();
        manifestSmallParticleTimer -= Gdx.graphics.getDeltaTime();

        while (manifestSmallParticleTimer <= 0.0f)
        {
            if (ManifestField.mirthManifested.get(this))
            {
                manifestSmallParticleTimer += MathUtils.random(0.05f, 0.02f);
                AbstractDungeon.effectsQueue.add(new MirthParticleEffect());
            }
            else
            {
                manifestSmallParticleTimer += MathUtils.random(0.1f, 0.15f);
                AbstractDungeon.effectsQueue.add(new MaliceParticleEffect());
            }
        }

        if (manifestParticleTimer <= 0.0f)
        {
            manifestParticleTimer = MathUtils.random(0.3F, 0.4F);
            AbstractDungeon.effectList.add(new MirthMaliceAuraEffect(ManifestField.mirthManifested.get(this)));
        }
    }

    @Override
    public void draw() {
        if (otherPlayerHand.size() >= BaseMod.MAX_HAND_SIZE && this.hand.size() >= BaseMod.MAX_HAND_SIZE)
        {
            this.createHandIsFullDialog();
        }
        else
        {
            CardCrawlGame.sound.playAV("CARD_DRAW_8", -0.12F, 0.25F);
            this.draw(1);
            this.onCardDrawOrDiscard();
        }
    }

    @Override
    public Texture getEnergyImage() {
        if (isMirth) {
            return TextureLoader.getTexture(assetPath("img/Blank.png"));
        }
        else {
            return TextureLoader.getTexture(assetPath("img/Character/orb/vfx.png"));
        }
    }
    public Texture getOtherEnergyImage() {
        if (!isMirth) {
            return TextureLoader.getTexture(assetPath("img/Blank.png"));
        }
        else {
            return TextureLoader.getTexture(assetPath("img/Character/orb/vfx.png"));
        }
    }

    @Override
    public void gainEnergy(int e) {
        if (SetEnergyGain.myGain == SetEnergyGain.otherPlayerGain)
        {
            super.gainEnergy(e);
            OtherEnergyPanel.addEnergy(e);
        }
        else if (SetEnergyGain.myGain)
        {
            super.gainEnergy(e);
        }
        else {
            OtherEnergyPanel.addEnergy(e);
        }
    }

    public boolean forceTryDraw(int force)
    {
        if (force == 0)
        {
            return tryDraw();
        }
        else
        {
            if (!forceDrawPileValid(force) || forceIsHandFull(force))
            {
                return false;
            }

            CardCrawlGame.sound.playAV("CARD_DRAW_8", -0.12F, 0.25F);
            this.forceDraw(force,1);
            this.onCardDrawOrDiscard();
            return true;
        }
    }

    public boolean tryDraw()
    {
        if (otherPlayerHand.size() >= BaseMod.MAX_HAND_SIZE && this.hand.size() >= BaseMod.MAX_HAND_SIZE)
        {
            this.createHandIsFullDialog();
            return false;
        }
        else
        {
            if (!drawPileValid() || isHandFull())
            {
                if (!TrackCardSource.useOtherEnergy && !TrackCardSource.useMyEnergy)
                    mirthDraw = !mirthDraw;
                return false;
            }
            CardCrawlGame.sound.playAV("CARD_DRAW_8", -0.12F, 0.25F);
            this.draw(1);
            this.onCardDrawOrDiscard();
            return true;
        }
    }
    public boolean tryOtherDraw()
    {
        if (otherPlayerHand.size() >= BaseMod.MAX_HAND_SIZE && this.hand.size() >= BaseMod.MAX_HAND_SIZE)
        {
            this.createHandIsFullDialog();
            return false;
        }
        else
        {
            if (!otherDrawPileValid() || isOtherHandFull())
            {
                if (!TrackCardSource.useOtherEnergy && !TrackCardSource.useMyEnergy)
                    mirthDraw = !mirthDraw;
                return false;
            }
            CardCrawlGame.sound.playAV("CARD_DRAW_8", -0.12F, 0.25F);
            this.drawOther(1);
            this.onCardDrawOrDiscard();
            return true;
        }
    }

    public boolean forceDrawPileValid(int force)
    {
        if (force == 0)
        {
            return drawPileValid();
        }
        else if (force < 0)
        {
            return !otherPlayerDraw.isEmpty();
        }
        else
        {
            return drawPile.isEmpty();
        }
    }
    public boolean drawPileValid()
    {
        if (TrackCardSource.useOtherEnergy) {
            return !otherPlayerDraw.isEmpty();
        }
        else if (TrackCardSource.useMyEnergy) {
            return !drawPile.isEmpty();
        }

        if (mirthDraw ^ isMirth)
        {
            return !otherPlayerDraw.isEmpty();
        }
        else
        {
            return !drawPile.isEmpty();
        }
    }
    public boolean otherDrawPileValid()
    {
        if (TrackCardSource.useOtherEnergy) {
            return !drawPile.isEmpty();
        }
        else if (TrackCardSource.useMyEnergy) {
            return !otherPlayerDraw.isEmpty();
        }

        if (mirthDraw ^ isMirth)
        {
            return !drawPile.isEmpty();
        }
        else
        {
            return !otherPlayerDraw.isEmpty();
        }
    }

    public boolean forceIsHandFull(int force)
    {
        if (force == 0)
        {
            return isHandFull();
        }
        else if (force < 0)
        {
            return otherPlayerHand.size() >= BaseMod.MAX_HAND_SIZE;
        }
        else
        {
            return hand.size() >= BaseMod.MAX_HAND_SIZE;
        }
    }
    public boolean isHandFull()
    {
        if (TrackCardSource.useOtherEnergy)
            return otherPlayerHand.size() >= BaseMod.MAX_HAND_SIZE;

        if (TrackCardSource.useMyEnergy)
            return hand.size() >= BaseMod.MAX_HAND_SIZE;

        if (mirthDraw ^ isMirth)
        {
            return otherPlayerHand.size() >= BaseMod.MAX_HAND_SIZE;
        }
        else
        {
            return hand.size() >= BaseMod.MAX_HAND_SIZE;
        }
    }
    public boolean isOtherHandFull()
    {
        if (TrackCardSource.useOtherEnergy)
            return hand.size() >= BaseMod.MAX_HAND_SIZE;

        if (TrackCardSource.useMyEnergy)
            return otherPlayerHand.size() >= BaseMod.MAX_HAND_SIZE;

        if (mirthDraw ^ isMirth)
        {
            return hand.size() >= BaseMod.MAX_HAND_SIZE;
        }
        else
        {
            return otherPlayerHand.size() >= BaseMod.MAX_HAND_SIZE;
        }
    }

    public boolean forceDiscardPileEmpty(int force)
    {
        if (force == 0)
        {
            return discardPileEmpty();
        }
        else if (force < 0)
        {
            return otherPlayerDiscard.isEmpty();
        }
        else
        {
            return discardPile.isEmpty();
        }
    }
    public boolean discardPileEmpty()
    {
        if (TrackCardSource.useOtherEnergy)
            return otherPlayerDiscard.isEmpty();

        if (TrackCardSource.useMyEnergy)
            return discardPile.isEmpty();

        if (mirthDraw ^ isMirth)
        {
            return otherPlayerDiscard.isEmpty();
        }
        else
        {
            return discardPile.isEmpty();
        }
    }
    public boolean otherDiscardPileEmpty()
    {
        if (TrackCardSource.useOtherEnergy)
            return discardPile.isEmpty();

        if (TrackCardSource.useMyEnergy)
            return otherPlayerDiscard.isEmpty();

        if (mirthDraw ^ isMirth) {
            return discardPile.isEmpty();
        }
        else {
            return otherPlayerDiscard.isEmpty();
        }
    }

    public AbstractGameAction forceGetShuffleAction(int force)
    {
        if (force == 0) {
            return getShuffleAction();
        }
        else if (force < 0) {
            return new OtherPlayerDeckShuffleAction();
        }
        else {
            return new EmptyDeckShuffleAction();
        }
    }
    public AbstractGameAction getShuffleAction()
    {
        if (TrackCardSource.useMyEnergy)
            return new EmptyDeckShuffleAction();

        if (mirthDraw ^ isMirth || TrackCardSource.useOtherEnergy)
        {
            return new OtherPlayerDeckShuffleAction();
        }
        else
        {
            return new EmptyDeckShuffleAction();
        }
    }
    public AbstractGameAction getOtherShuffleAction()
    {
        if (TrackCardSource.useMyEnergy)
            return new OtherPlayerDeckShuffleAction();

        if (mirthDraw ^ isMirth || TrackCardSource.useOtherEnergy)
        {
            return new EmptyDeckShuffleAction();
        }
        else
        {
            return new OtherPlayerDeckShuffleAction();
        }
    }
    public String getOtherPlayerName()
    {
        return isMirth ? characterStrings.NAMES[2] : characterStrings.NAMES[1];
    }

    public void forceDraw(int force, int numCards)
    {
        for(int i = 0; i < numCards; ++i) {
            if (forceIsHandFull(force) || !forceDrawPileValid(force))
            {
                break;
            }
            else if (force < 0)
            {
                logger.info(getOtherPlayerName() + " draw");
                if (!this.otherPlayerDraw.isEmpty()) {
                    AbstractCard c = this.otherPlayerDraw.getTopCard();
                    c.current_x = CardGroup.DRAW_PILE_X;
                    c.current_y = CardGroup.DRAW_PILE_Y + OTHER_DRAW_OFFSET;
                    c.setAngle(0.0F, true);
                    c.lighten(false);
                    c.drawScale = 0.12F;
                    c.targetDrawScale = 0.75F;
                    
                    c.triggerWhenDrawn();
                    this.otherPlayerHand.addToHand(c);
                    this.otherPlayerDraw.removeTopCard();

                    for (AbstractPower p : this.powers)
                    {
                        p.onCardDraw(c);
                    }
                    for (AbstractRelic r : this.relics)
                    {
                        r.onCardDraw(c);
                    }
                    this.otherPlayerHand.refreshHandLayout();
                } else {
                    logger.info("ERROR: How did this happen? No cards in other player's draw pile?? Player.java");
                }
            }
            else
            {
                logger.info(name + " draw");
                if (!this.drawPile.isEmpty()) {
                    AbstractCard c = this.drawPile.getTopCard();
                    c.current_x = CardGroup.DRAW_PILE_X;
                    c.current_y = CardGroup.DRAW_PILE_Y;
                    c.setAngle(0.0F, true);
                    c.lighten(false);
                    c.drawScale = 0.12F;
                    c.targetDrawScale = 0.75F;
                    
                    c.triggerWhenDrawn();
                    this.hand.addToHand(c);
                    this.drawPile.removeTopCard();

                    for (AbstractPower p : this.powers)
                    {
                        p.onCardDraw(c);
                    }
                    for (AbstractRelic r : this.relics)
                    {
                        r.onCardDraw(c);
                    }
                }
                else {
                    logger.info("ERROR: How did this happen? No cards in draw pile?? Player.java");
                }
            }
        }
    }

    @Override
    public void draw(int numCards) {
        boolean failedDraw = false;
        for(int i = 0; i < numCards; ++i) {
            if (isHandFull() || !drawPileValid())
            {
                i--; //this draw does nothing, decrement counter.
                //If draw is failed twice in a row (Both characters hands are full) or drawing from specific pile break draw loop
                if (failedDraw || TrackCardSource.useMyEnergy || TrackCardSource.useOtherEnergy)
                    break;
                failedDraw = true;
            }
            else if (TrackCardSource.useOtherEnergy)
            {
                logger.info(getOtherPlayerName() + " draw, based on card source");
                if (!this.otherPlayerDraw.isEmpty()) {
                    AbstractCard c = this.otherPlayerDraw.getTopCard();
                    c.current_x = CardGroup.DRAW_PILE_X;
                    c.current_y = CardGroup.DRAW_PILE_Y + OTHER_DRAW_OFFSET;
                    c.setAngle(0.0F, true);
                    c.lighten(false);
                    c.drawScale = 0.12F;
                    c.targetDrawScale = 0.75F;
                    
                    c.triggerWhenDrawn();
                    this.otherPlayerHand.addToHand(c);
                    this.otherPlayerDraw.removeTopCard();

                    for (AbstractPower p : this.powers)
                    {
                        p.onCardDraw(c);
                    }
                    for (AbstractRelic r : this.relics)
                    {
                        r.onCardDraw(c);
                    }
                    this.otherPlayerHand.refreshHandLayout();
                    failedDraw = false;
                } else {
                    logger.info("ERROR: How did this happen? No cards in other player's draw pile?? Player.java");
                }
            }
            else if (TrackCardSource.useMyEnergy)
            {
                logger.info(name + " draw, based on card source");
                if (!this.drawPile.isEmpty()) {
                    AbstractCard c = this.drawPile.getTopCard();
                    c.current_x = CardGroup.DRAW_PILE_X;
                    c.current_y = CardGroup.DRAW_PILE_Y;
                    c.setAngle(0.0F, true);
                    c.lighten(false);
                    c.drawScale = 0.12F;
                    c.targetDrawScale = 0.75F;
                    
                    c.triggerWhenDrawn();
                    this.hand.addToHand(c);
                    this.drawPile.removeTopCard();

                    for (AbstractPower p : this.powers)
                    {
                        p.onCardDraw(c);
                    }
                    for (AbstractRelic r : this.relics)
                    {
                        r.onCardDraw(c);
                    }
                    failedDraw = false;
                }
                else {
                    logger.info("ERROR: How did this happen? No cards in draw pile?? Player.java");
                }
            }
            else if (mirthDraw ^ isMirth) //One is true, one is false. So, either mokou is drawing and this isn't mokou, or this is mokou and it's keine's draw.
            {
                logger.info(getOtherPlayerName() + " draw, based on standard swap");
                if (!this.otherPlayerDraw.isEmpty()) {
                    AbstractCard c = this.otherPlayerDraw.getTopCard();
                    c.current_x = CardGroup.DRAW_PILE_X;
                    c.current_y = CardGroup.DRAW_PILE_Y + OTHER_DRAW_OFFSET;
                    c.setAngle(0.0F, true);
                    c.lighten(false);
                    c.drawScale = 0.12F;
                    c.targetDrawScale = 0.75F;
                    
                    c.triggerWhenDrawn();
                    this.otherPlayerHand.addToHand(c);
                    this.otherPlayerDraw.removeTopCard();

                    for (AbstractPower p : this.powers)
                    {
                        p.onCardDraw(c);
                    }
                    for (AbstractRelic r : this.relics)
                    {
                        r.onCardDraw(c);
                    }
                    this.otherPlayerHand.refreshHandLayout();
                    failedDraw = false;
                    mirthDraw = !mirthDraw;
                } else {
                    logger.info("ERROR: How did this happen? No cards in other player's draw pile?? Player.java");
                }
            }
            else //Both are true or both are false.
            {
                logger.info(name + " draw, based on standard swap");
                if (!this.drawPile.isEmpty()) {
                    AbstractCard c = this.drawPile.getTopCard();
                    c.current_x = CardGroup.DRAW_PILE_X;
                    c.current_y = CardGroup.DRAW_PILE_Y;
                    c.setAngle(0.0F, true);
                    c.lighten(false);
                    c.drawScale = 0.12F;
                    c.targetDrawScale = 0.75F;
                    
                    c.triggerWhenDrawn();
                    this.hand.addToHand(c);
                    this.drawPile.removeTopCard();

                    for (AbstractPower p : this.powers)
                    {
                        p.onCardDraw(c);
                    }
                    for (AbstractRelic r : this.relics)
                    {
                        r.onCardDraw(c);
                    }
                    failedDraw = false;
                    mirthDraw = !mirthDraw;
                } else {
                    logger.info("ERROR: How did this happen? No cards in draw pile?? Player.java");
                }
            }
        }
    }

    public void drawOther(int numCards) {
        boolean failedDraw = false;
        for(int i = 0; i < numCards; ++i) {
            if (isOtherHandFull() || !otherDrawPileValid())
            {
                i--; //this draw does nothing, decrement counter.
                //If draw is failed twice in a row (Both characters hands are full) or drawing from specific pile break draw loop
                if (failedDraw || TrackCardSource.useMyEnergy || TrackCardSource.useOtherEnergy)
                    break;
                failedDraw = true;
            }
            else if (TrackCardSource.useMyEnergy)
            {
                logger.info(getOtherPlayerName() + " draw, based on card source (drawOther)");
                if (!this.otherPlayerDraw.isEmpty()) {
                    AbstractCard c = this.otherPlayerDraw.getTopCard();
                    c.current_x = CardGroup.DRAW_PILE_X;
                    c.current_y = CardGroup.DRAW_PILE_Y + OTHER_DRAW_OFFSET;
                    c.setAngle(0.0F, true);
                    c.lighten(false);
                    c.drawScale = 0.12F;
                    c.targetDrawScale = 0.75F;
                    
                    c.triggerWhenDrawn();
                    this.otherPlayerHand.addToHand(c);
                    this.otherPlayerDraw.removeTopCard();

                    for (AbstractPower p : this.powers)
                    {
                        p.onCardDraw(c);
                    }
                    for (AbstractRelic r : this.relics)
                    {
                        r.onCardDraw(c);
                    }
                    this.otherPlayerHand.refreshHandLayout();
                    failedDraw = false;
                } else {
                    logger.info("ERROR: How did this happen? No cards in other player's draw pile?? Player.java");
                }
            }
            else if (TrackCardSource.useOtherEnergy)
            {
                logger.info(name + " draw, based on card source (drawOther)");
                if (!this.drawPile.isEmpty()) {
                    AbstractCard c = this.drawPile.getTopCard();
                    c.current_x = CardGroup.DRAW_PILE_X;
                    c.current_y = CardGroup.DRAW_PILE_Y;
                    c.setAngle(0.0F, true);
                    c.lighten(false);
                    c.drawScale = 0.12F;
                    c.targetDrawScale = 0.75F;
                    
                    c.triggerWhenDrawn();
                    this.hand.addToHand(c);
                    this.drawPile.removeTopCard();

                    for (AbstractPower p : this.powers)
                    {
                        p.onCardDraw(c);
                    }
                    for (AbstractRelic r : this.relics)
                    {
                        r.onCardDraw(c);
                    }
                    failedDraw = false;
                }
                else {
                    logger.info("ERROR: How did this happen? No cards in draw pile?? Player.java");
                }
            }
            else if (mirthDraw ^ isMirth) //One is true, one is false. So, either mokou is drawing and this isn't mokou, or this is mokou and it's keine's draw.
            {
                logger.info(name + " draw, based on standard swap (drawOther)");
                if (!this.drawPile.isEmpty()) {
                    AbstractCard c = this.drawPile.getTopCard();
                    c.current_x = CardGroup.DRAW_PILE_X;
                    c.current_y = CardGroup.DRAW_PILE_Y;
                    c.setAngle(0.0F, true);
                    c.lighten(false);
                    c.drawScale = 0.12F;
                    c.targetDrawScale = 0.75F;
                    
                    c.triggerWhenDrawn();
                    this.hand.addToHand(c);
                    this.drawPile.removeTopCard();

                    for (AbstractPower p : this.powers)
                    {
                        p.onCardDraw(c);
                    }
                    for (AbstractRelic r : this.relics)
                    {
                        r.onCardDraw(c);
                    }
                    failedDraw = false;
                } else {
                    logger.info("ERROR: How did this happen? No cards in draw pile?? Player.java");
                }
                mirthDraw = !mirthDraw;
            }
            else //Both are true or both are false.
            {
                logger.info(getOtherPlayerName() + " draw, based on standard swap (drawOther)");
                if (!this.otherPlayerDraw.isEmpty()) {
                    AbstractCard c = this.otherPlayerDraw.getTopCard();
                    c.current_x = CardGroup.DRAW_PILE_X;
                    c.current_y = CardGroup.DRAW_PILE_Y + OTHER_DRAW_OFFSET;
                    c.setAngle(0.0F, true);
                    c.lighten(false);
                    c.drawScale = 0.12F;
                    c.targetDrawScale = 0.75F;
                    
                    c.triggerWhenDrawn();
                    this.otherPlayerHand.addToHand(c);
                    this.otherPlayerDraw.removeTopCard();

                    for (AbstractPower p : this.powers)
                    {
                        p.onCardDraw(c);
                    }
                    for (AbstractRelic r : this.relics)
                    {
                        r.onCardDraw(c);
                    }
                    this.otherPlayerHand.refreshHandLayout();
                    failedDraw = false;
                } else {
                    logger.info("ERROR: How did this happen? No cards in draw pile?? Player.java");
                }
                mirthDraw = !mirthDraw;
            }
        }
    }

    public void updateOtherOrb(int amt)
    {
        otherPlayerOrb.updateOrb(amt);
    }


    public void updateCardsOnDiscard() {
        if (isMirth) //keine is always updated first.
        {
            for (AbstractCard c : otherPlayerHand.group)
            {
                c.didDiscard();
            }
            for (AbstractCard c : otherPlayerDiscard.group)
            {
                c.didDiscard();
            }
            for (AbstractCard c : otherPlayerDraw.group)
            {
                c.didDiscard();
            }
        }
        super.updateCardsOnDiscard();
        if (!isMirth)
        {
            for (AbstractCard c : otherPlayerHand.group)
            {
                c.didDiscard();
            }
            for (AbstractCard c : otherPlayerDiscard.group)
            {
                c.didDiscard();
            }
            for (AbstractCard c : otherPlayerDraw.group)
            {
                c.didDiscard();
            }
        }
    }

    @Override
    public void onCardDrawOrDiscard() {
        for (AbstractPower p : this.powers)
        {
            p.onDrawOrDiscard();
        }
        for (AbstractRelic r : this.relics)
        {
            r.onDrawOrDiscard();
        }

        if (isMirth)
        {
            if (this.hasPower(CorruptionPower.POWER_ID)) {
                for (AbstractCard c : this.otherPlayerHand.group)
                {
                    if (c.type == AbstractCard.CardType.SKILL && c.costForTurn != 0) {
                        c.modifyCostForCombat(-99);
                    }
                }
            }
            this.otherPlayerHand.applyPowers();
        }

        if (this.hasPower(CorruptionPower.POWER_ID)) {
            for (AbstractCard c : this.hand.group)
            {
                if (c.type == AbstractCard.CardType.SKILL && c.costForTurn != 0) {
                    c.modifyCostForCombat(-99);
                }
            }
        }
        this.hand.applyPowers();
        this.hand.glowCheck();

        if (!isMirth)
        {
            if (this.hasPower(CorruptionPower.POWER_ID)) {
                for (AbstractCard c : this.otherPlayerHand.group)
                {
                    if (c.type == AbstractCard.CardType.SKILL && c.costForTurn != 0) {
                        c.modifyCostForCombat(-99);
                    }
                }
            }
            this.otherPlayerHand.applyPowers();
        }

        //EXTREME DEBUG CODE: COMMENT OUT IF NOT NEEDED
        if (HandleMatchmaking.isHost)
        {

        }
        else //send data to host to display
        {

        }
    }

    @Override
    public void applyStartOfTurnCards() {
        if (isMirth)
        {
            for (AbstractCard c : otherPlayerDraw.group)
            {
                if (c != null)
                    c.atTurnStart();
            }
            for (AbstractCard c : otherPlayerHand.group)
            {
                if (c != null)
                    c.atTurnStart();
            }
            for (AbstractCard c : otherPlayerDiscard.group)
            {
                if (c != null)
                    c.atTurnStart();
            }
        }
        super.applyStartOfTurnCards();
        if (!isMirth)
        {
            for (AbstractCard c : otherPlayerDraw.group)
            {
                if (c != null)
                    c.atTurnStart();
            }
            for (AbstractCard c : otherPlayerHand.group)
            {
                if (c != null)
                    c.atTurnStart();
            }
            for (AbstractCard c : otherPlayerDiscard.group)
            {
                if (c != null)
                    c.atTurnStart();
            }
        }
    }

    public void renderOtherOrb(SpriteBatch sb, boolean enabled, float current_x, float current_y) {
        this.otherPlayerOrb.renderOrb(sb, enabled, current_x, current_y);
    }

    public BitmapFont getOtherEnergyNumFont() {
        return isMirth ? FontHelper.energyNumFontBlue : FontHelper.energyNumFontRed;
    }

    @Override
    public void renderHand(SpriteBatch sb) {
        AbstractMonster hoveredMonster = (AbstractMonster)ReflectionHacks.getPrivate(this, AbstractPlayer.class, "hoveredMonster");

        if (Settings.SHOW_CARD_HOTKEYS) {
            int index = 0;

            for(Iterator var3 = this.hand.group.iterator(); var3.hasNext(); ++index) {
                AbstractCard card = (AbstractCard)var3.next();
                if (index < InputActionSet.selectCardActions.length) {
                    float width = AbstractCard.IMG_WIDTH * card.drawScale / 2.0F;
                    float height = AbstractCard.IMG_HEIGHT * card.drawScale / 2.0F;
                    float topOfCard = card.current_y + height;
                    float textSpacing = 50.0F * Settings.scale;
                    float textY = topOfCard + textSpacing;
                    float sin = (float)Math.sin((double)(card.angle / 180.0F) * 3.141592653589793D);
                    float xOffset = sin * width;
                    FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, InputActionSet.selectCardActions[index].getKeyString(), card.current_x - xOffset, textY, Settings.CREAM_COLOR);
                }
            }
        }

        if (this.inspectMode && this.inspectHb != null) {
            this.renderReticle(sb, this.inspectHb);
        }

        if (this.hoveredCard != null) {
            int aliveMonsters = 0;
            this.hand.renderHand(sb, this.hoveredCard);
            this.hoveredCard.renderHoverShadow(sb);
            if ((this.isDraggingCard || this.inSingleTargetMode) && this.isHoveringDropZone) {
                if (this.isDraggingCard && !this.inSingleTargetMode) {
                    AbstractMonster theMonster = null;
                    Iterator var4 = AbstractDungeon.getMonsters().monsters.iterator();

                    while(var4.hasNext()) {
                        AbstractMonster m = (AbstractMonster)var4.next();
                        if (!m.isDying && m.currentHealth > 0) {
                            ++aliveMonsters;
                            theMonster = m;
                        }
                    }

                    if (aliveMonsters == 1 && hoveredMonster == null) {
                        this.hoveredCard.calculateCardDamage(theMonster);
                        this.hoveredCard.render(sb);
                        this.hoveredCard.applyPowers();
                    } else {
                        this.hoveredCard.render(sb);
                    }
                }

                if (!AbstractDungeon.getCurrRoom().isBattleEnding()) {
                    try {
                        hoverReticleMethod.invoke(this, sb);
                    }
                    catch (Exception e) {
                        logger.error("Failed to invoke hoverReticleMethod.");
                        logger.error(e.getMessage());
                    }
                }
            }

            if (hoveredMonster != null) {
                this.hoveredCard.calculateCardDamage(hoveredMonster);
                this.hoveredCard.render(sb);
                this.hoveredCard.applyPowers();
            } else if (aliveMonsters != 1) {
                this.hoveredCard.render(sb);
            }
            this.otherPlayerHand.renderHand(sb, this.cardInUse);
        } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.HAND_SELECT) {
            this.hand.render(sb);
        } else {
            this.hand.renderHand(sb, this.cardInUse);
            this.otherPlayerHand.renderHand(sb, this.cardInUse);
        }

        if (this.cardInUse != null && AbstractDungeon.screen != AbstractDungeon.CurrentScreen.HAND_SELECT) {
            this.cardInUse.render(sb);
            if (AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) {
                AbstractDungeon.effectList.add(new CardDisappearEffect(this.cardInUse.makeCopy(), this.cardInUse.current_x, this.cardInUse.current_y));
                this.cardInUse = null;
            }
        }

        this.limbo.render(sb);

        if (this.inSingleTargetMode && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.getCurrRoom().isBattleEnding()) {
            float arrowX = (float) ReflectionHacks.getPrivate(this, AbstractPlayer.class, "arrowX");
            float arrowY = (float) ReflectionHacks.getPrivate(this, AbstractPlayer.class, "arrowY");
            float arrowScale = Settings.scale;

            arrowX = MathHelper.mouseLerpSnap(arrowX, (float)InputHelper.mX);
            arrowY = MathHelper.mouseLerpSnap(arrowY, (float)InputHelper.mY);
            ReflectionHacks.setPrivate(this, AbstractPlayer.class, "arrowX", arrowX);
            ReflectionHacks.setPrivate(this, AbstractPlayer.class, "arrowY", arrowY);
            Vector2 controlPoint = new Vector2(this.hoveredCard.current_x - (arrowX - this.hoveredCard.current_x) / 4.0F, arrowY + (arrowY - this.hoveredCard.current_y) / 2.0F);
            if (hoveredMonster == null) {
                ReflectionHacks.setPrivate(this, AbstractPlayer.class, "arrowScale", Settings.scale);
                ReflectionHacks.setPrivate(this, AbstractPlayer.class, "arrowScaleTimer", 0.0F);
                sb.setColor(Color.WHITE);
            } else {
                float timer = (float)ReflectionHacks.getPrivate(this, AbstractPlayer.class, "arrowScaleTimer") + Gdx.graphics.getDeltaTime();
                if (timer > 1.0F)
                    timer = 1.0F;
                ReflectionHacks.setPrivate(this, AbstractPlayer.class, "arrowScaleTimer", timer);

                arrowScale = Interpolation.elasticOut.apply(Settings.scale, Settings.scale * 1.2F, timer);
                ReflectionHacks.setPrivate(this, AbstractPlayer.class, "arrowScale", arrowScale);
                sb.setColor(ARROW_COLOR);
            }

            Vector2 tmp = new Vector2(controlPoint.x - arrowX, controlPoint.y - arrowY);
            tmp.nor();
            drawCurvedLine(sb, controlPoint, new Vector2(this.hoveredCard.current_x, this.hoveredCard.current_y), new Vector2(arrowX, arrowY), controlPoint);
            sb.draw(ImageMaster.TARGET_UI_ARROW, arrowX - 128.0F, arrowY - 128.0F, 128.0F, 128.0F, 256.0F, 256.0F, arrowScale, arrowScale, tmp.angle() + 90.0F, 0, 0, 256, 256, false, false);
        }
    }

    // Starting description and loadout
    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(characterStrings.NAMES[0], characterStrings.TEXT[0],
                STARTING_HP, MAX_HP, ORB_SLOTS, STARTING_GOLD, CARD_DRAW, this, getStartingRelics(),
                getStartingDeck(), false);
    }

    // Card Pool
    @Override
    public ArrayList<AbstractCard> getCardPool(ArrayList<AbstractCard> tmpPool) {
        super.getCardPool(tmpPool);

        for (Map.Entry<String, AbstractCard> c : CardLibrary.cards.entrySet())
        {
            AbstractCard card = c.getValue();
            if (card.color.equals(CharacterEnums.MIRTHMALICE_NEUTRAL) &&
                    !card.rarity.equals(AbstractCard.CardRarity.BASIC) &&
                    !card.rarity.equals(AbstractCard.CardRarity.SPECIAL) &&
                    (!UnlockTracker.isCardLocked(c.getKey()) || Settings.isDailyRun))
            {
                tmpPool.add(card);
            }
        }

        return tmpPool;
    }

    // Starting Deck
    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> startDeck = new ArrayList<>();

        otherPlayerMasterDeck.clear();

        if (isMirth)
        {
            startDeck.add(MirthStrike.ID);
            startDeck.add(MirthStrike.ID);
            startDeck.add(MirthStrike.ID);
            startDeck.add(MirthDefend.ID);
            startDeck.add(MirthDefend.ID);
            startDeck.add(MirthDefend.ID);
            startDeck.add(Indulgence.ID);
            startDeck.add(ImitatedInnocence.ID);

            otherPlayerMasterDeck.group.add(new MaliceStrike());
            otherPlayerMasterDeck.group.add(new MaliceStrike());
            otherPlayerMasterDeck.group.add(new MaliceStrike());
            otherPlayerMasterDeck.group.add(new MaliceDefend());
            otherPlayerMasterDeck.group.add(new MaliceDefend());
            otherPlayerMasterDeck.group.add(new MaliceDefend());
            otherPlayerMasterDeck.group.add(new Caution());
            otherPlayerMasterDeck.group.add(new Wilt());
        }
        else
        {
            startDeck.add(MaliceStrike.ID);
            startDeck.add(MaliceStrike.ID);
            startDeck.add(MaliceStrike.ID);
            startDeck.add(MaliceDefend.ID);
            startDeck.add(MaliceDefend.ID);
            startDeck.add(MaliceDefend.ID);
            startDeck.add(Caution.ID);
            startDeck.add(Wilt.ID);

            otherPlayerMasterDeck.group.add(new MirthStrike());
            otherPlayerMasterDeck.group.add(new MirthStrike());
            otherPlayerMasterDeck.group.add(new MirthStrike());
            otherPlayerMasterDeck.group.add(new MirthDefend());
            otherPlayerMasterDeck.group.add(new MirthDefend());
            otherPlayerMasterDeck.group.add(new MirthDefend());
            otherPlayerMasterDeck.group.add(new Indulgence());
            otherPlayerMasterDeck.group.add(new ImitatedInnocence());
        }

        return startDeck;
    }

    // Starting Relics
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> startingRelics = new ArrayList<>();

        startingRelics.add(BurningBlood.ID);

        return startingRelics;
    }

    // Character select screen effect
    @Override
    public void doCharSelectScreenSelectEffect() {
        if (MathUtils.randomBoolean()) {
            CardCrawlGame.sound.play("CARD_BURN", 0.1f);
            CardCrawlGame.sound.playA("ATTACK_FLAME_BARRIER", -0.3f);
        }
        else {
            CardCrawlGame.sound.play("ATTACK_MAGIC_FAST_1", 0.1f);
        }
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT,
                false); // Screen Effect
    }

    // Character select on-button-press sound effect
    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return MathUtils.randomBoolean() ? "ATTACK_MAGIC_FAST_1" : "ATTACK_FLAME_BARRIER";
    }

    @Override
    public int getAscensionMaxHPLoss() {
        return 10;
    } //a bit much, perhaps? But, this character will be quite overtuned at low ascension.

    @Override
    public AbstractCard.CardColor getCardColor() {
        return this.cardColor;
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        return this.energyFont;
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        return isMirth ? new Scorch() : new Forget();
    }
    @Override
    public String getLocalizedCharacterName() {
        return characterStrings.NAMES[0];
    }
    @Override
    public String getTitle(PlayerClass playerClass) {
        return (isMirth ? characterStrings.NAMES[1] : characterStrings.NAMES[2]);
    }


    @Override
    public AbstractPlayer newInstance() {
        return new MirthAndMalice(isMirth);
    }

    @Override
    public Color getCardTrailColor() {
        return cardTrailColor;
    }
    @Override
    public Color getCardRenderColor() {
        return cardRenderColor;
    }
    @Override
    public Color getSlashAttackColor() {
        return slashAttackColor;
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[] {
                AbstractGameAction.AttackEffect.POISON,
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL,
                AbstractGameAction.AttackEffect.POISON,
                AbstractGameAction.AttackEffect.POISON,
                AbstractGameAction.AttackEffect.SLASH_HORIZONTAL,
                AbstractGameAction.AttackEffect.SLASH_VERTICAL,
                AbstractGameAction.AttackEffect.POISON,
                AbstractGameAction.AttackEffect.SLASH_HEAVY
        };
    }

    @Override
    public String getSpireHeartText() {
        return characterStrings.TEXT[1];
    }
    @Override
    public String getVampireText() {
        return characterStrings.TEXT[2];
    }

    @Override
    public boolean isCursed() {
        for (AbstractCard c : this.masterDeck.group)
        {
            if ((c.type == AbstractCard.CardType.CURSE || c.color == AbstractCard.CardColor.CURSE) && !c.cardID.equals(Necronomicurse.ID) && !c.cardID.equals(AscendersBane.ID))
                return true;
        }
        for (AbstractCard c : this.otherPlayerMasterDeck.group)
        {
            if ((c.type == AbstractCard.CardType.CURSE || c.color == AbstractCard.CardColor.CURSE) && !c.cardID.equals(Necronomicurse.ID) && !c.cardID.equals(AscendersBane.ID))
                return true;
        }

        return false;
    }

    public boolean randomOtherPlayerPotion = false;
    @Override
    public AbstractPotion getRandomPotion() {
        ArrayList<AbstractPotion> list = new ArrayList<>();

        if (!isMirth)
        {
            for (String potionID : MultiplayerHelper.otherPlayerPotions)
            {
                list.add(PotionHelper.getPotion(potionID));
            }
        }

        for (AbstractPotion p : this.potions)
        {
            if (!(p instanceof PotionSlot)) {
                list.add(p);
            }
        }

        if (isMirth)
        {
            for (String potionID : MultiplayerHelper.otherPlayerPotions)
            {
                list.add(PotionHelper.getPotion(potionID));
            }
        }

        if (list.isEmpty()) {
            randomOtherPlayerPotion = false;
            return null;
        } else {
            randomOtherPlayerPotion = false;
            Collections.shuffle(list, new Random(AbstractDungeon.miscRng.randomLong()));
            if (!potions.contains(list.get(0)))
                randomOtherPlayerPotion = true;

            return list.get(0);
        }
    }

    private void drawCurvedLine(SpriteBatch sb, Vector2 controlPoint, Vector2 start, Vector2 end, Vector2 control) {
        float radius = 7.0F * Settings.scale;

        for(int i = 0; i < this.points.length - 1; ++i) {
            this.points[i] = Bezier.quadratic(this.points[i], (float)i / 20.0F, start, control, end, new Vector2());
            radius += 0.4F * Settings.scale;
            Vector2 tmp;
            float angle;
            if (i != 0) {
                tmp = new Vector2(this.points[i - 1].x - this.points[i].x, this.points[i - 1].y - this.points[i].y);
                angle = tmp.nor().angle() + 90.0F;
            } else {
                tmp = new Vector2(controlPoint.x - this.points[i].x, controlPoint.y - this.points[i].y);
                angle = tmp.nor().angle() + 270.0F;
            }

            sb.draw(ImageMaster.TARGET_UI_CIRCLE, this.points[i].x - 64.0F, this.points[i].y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, radius / 18.0F, radius / 18.0F, angle, 0, 0, 128, 128, false, false);
        }
    }
}
