package mirthandmalice.ui;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;

public class MokouKeineCharacterOption extends CharacterOption {
    public MokouKeineCharacterOption(String localizedName, AbstractPlayer character)
    {
        super(character.getLocalizedCharacterName(), CardCrawlGame.characterManager.recreateCharacter(character.chosenClass), BaseMod.playerSelectButtonMap.get(character.chosenClass), BaseMod.playerPortraitMap.get(character.chosenClass));
    }

    @Override
    public void update() {
        super.update();
        if (this.selected)
        {
            //Add option to play single player
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.selected)
        {
            //render option to play single player
        }
    }
}
