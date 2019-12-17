package mirthandmalice.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import mirthandmalice.character.MirthAndMalice;

public class AltOverlayMenu extends OverlayMenu {
    public AbstractPlayer p;

    public OtherEnergyPanel otherPlayerEnergy = new OtherEnergyPanel();
    public OtherDrawPilePanel otherPlayerDrawPile = new OtherDrawPilePanel();
    public OtherDiscardPilePanel otherDiscardPilePanel = new OtherDiscardPilePanel();

    public boolean wasOpen;

    public AltOverlayMenu(AbstractPlayer p)
    {
        super(p);
        this.p = p;
        wasOpen = false;

        this.energyPanel.show_y = Settings.HEIGHT / 2.0f;
        this.energyPanel.hide_y = Settings.HEIGHT / 2.0f;
        this.energyPanel.target_y = Settings.HEIGHT / 2.0f;
        this.energyPanel.current_y = Settings.HEIGHT / 2.0f;

        this.exhaustPanel.show_y += OtherDrawPilePanel.OTHER_DRAW_OFFSET;
    }

    @Override
    public void update() {
        wasOpen = AbstractDungeon.isScreenUp;
        super.update();
    }

    @Override
    public void showCombatPanels() {
        otherPlayerDrawPile.show();
        otherDiscardPilePanel.show();
        otherPlayerEnergy.show();
        super.showCombatPanels();
    }

    @Override
    public void hideCombatPanels() {
        otherPlayerDrawPile.hide();
        otherDiscardPilePanel.hide();
        otherPlayerEnergy.hide();
        super.hideCombatPanels();

        if (p instanceof MirthAndMalice)
        {
            for (AbstractCard c : ((MirthAndMalice) p).otherPlayerHand.group)
            {
                c.target_y = -AbstractCard.IMG_HEIGHT;
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        this.endTurnButton.render(sb);
        this.proceedButton.render(sb);
        this.cancelButton.render(sb);
        if (!Settings.hideLowerElements) {
            this.energyPanel.render(sb);
            this.otherPlayerEnergy.render(sb);
            this.combatDeckPanel.render(sb);
            this.otherPlayerDrawPile.render(sb);
            this.discardPilePanel.render(sb);
            this.otherDiscardPilePanel.render(sb);
            this.exhaustPanel.render(sb);
        }

        this.p.renderHand(sb);
        this.p.hand.renderTip(sb);
    }
}
