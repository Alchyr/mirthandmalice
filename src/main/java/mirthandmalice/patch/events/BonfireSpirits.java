package mirthandmalice.patch.events;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.Bonfire;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.SpiritPoop;
import mirthandmalice.patch.enums.CharacterEnums;
import mirthandmalice.util.MultiplayerHelper;

import static mirthandmalice.MirthAndMaliceMod.chat;
import static mirthandmalice.MirthAndMaliceMod.makeID;

@SpirePatch(
        clz = Bonfire.class,
        method = "setReward"
)
public class BonfireSpirits {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("Bonfire"));
    private static final String[] TEXT = uiStrings.TEXT;

    @SpirePrefixPatch
    public static void reportReward(Bonfire __instance, AbstractCard.CardRarity rarity)
    {
        if (AbstractDungeon.player.chosenClass == CharacterEnums.MIRTHMALICE && MultiplayerHelper.active)
        {
            switch (rarity)
            {
                case CURSE:
                    MultiplayerHelper.sendP2PString("bonfirecurse");
                    break;
                case BASIC:
                    MultiplayerHelper.sendP2PString("bonfirebasic");
                    break;
                case COMMON:
                case SPECIAL:
                    MultiplayerHelper.sendP2PString("bonfirecommon");
                    break;
                case UNCOMMON:
                    MultiplayerHelper.sendP2PString("bonfireuncommon");
                    break;
                case RARE:
                    MultiplayerHelper.sendP2PString("bonfirerare");
                    break;
            }
        }
    }

    public static void receiveReward(String msg)
    {
        if (chat != null)
        {
            switch (msg)
            {
                case "curse":
                    chat.receiveMessage(MultiplayerHelper.partnerName + TEXT[0]);
                    if (!AbstractDungeon.player.hasRelic(SpiritPoop.ID)) {
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, RelicLibrary.getRelic(SpiritPoop.ID).makeCopy());
                    }
                    break;
                case "basic":
                    chat.receiveMessage(MultiplayerHelper.partnerName + TEXT[1]);
                    break;
                case "common":
                    chat.receiveMessage(MultiplayerHelper.partnerName + TEXT[2]);
                    AbstractDungeon.player.heal(5);
                    break;
                case "uncommon":
                    chat.receiveMessage(MultiplayerHelper.partnerName + TEXT[4]);
                    AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth);
                    break;
                case "rare":
                    chat.receiveMessage(MultiplayerHelper.partnerName + TEXT[3]);
                    AbstractDungeon.player.increaseMaxHp(10, false);
                    AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth);
                    break;
            }
        }
    }
}
