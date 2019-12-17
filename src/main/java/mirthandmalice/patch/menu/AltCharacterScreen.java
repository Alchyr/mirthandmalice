package mirthandmalice.patch.menu;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(
        clz = BaseMod.class,
        method = "generateCharacterOptions"
)
public class AltCharacterScreen {
    //Current unnecessary. (and also causes a crash.)
    /*@SpirePostfixPatch
    public static ArrayList<CharacterOption> useAltScreen(ArrayList<CharacterOption> __result)
    {
        int index = 0;
        for (; index < __result.size(); index++)
        {
            if (__result.get(index).c instanceof MokouKeine)
            {
                //Replace with alternate select.
                __result.set(index, new MokouKeineCharacterOption(__result.get(index).name, __result.get(index).c));
                break;
            }
        }

        return __result;
    }*/
}
