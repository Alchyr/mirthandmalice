package mirthandmalice.patch.game_initialize;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import mirthandmalice.character.MirthAndMalice;

import java.util.ArrayList;

@SpirePatch(
        clz = BaseMod.class,
        method = "generateCharacterOptions"
)
public class AscensionMax {
    public static int getMaxAscension()
    {
        return 20;
    }
    @SpirePostfixPatch
    public static ArrayList<CharacterOption> useAltScreen(ArrayList<CharacterOption> __result)
    {
        int index = 0;
        for (; index < __result.size(); index++)
        {
            if (__result.get(index).c instanceof MirthAndMalice)
            {
                Prefs pref = __result.get(index).c.getPrefs();

                //set ascension
                if (pref != null) {
                    ReflectionHacks.setPrivate(__result.get(index), CharacterOption.class, "maxAscensionLevel", getMaxAscension());
                    pref.putInteger("ASCENSION_LEVEL", getMaxAscension());

                    pref.flush();
                }
                break;
            }
        }

        return __result;
    }
}
