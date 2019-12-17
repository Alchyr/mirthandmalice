package mirthandmalice.patch.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.Timer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.ScrollInputProcessor;

import static mirthandmalice.MirthAndMaliceMod.chat;

public class InputHelper {
    public static String text = "";

    static private final char BACKSPACE = 8;
    static private final char ENTER_DESKTOP = '\r';
    static private final char ENTER_ANDROID = '\n';
    static private final char TAB = '\t';
    static private final char DELETE = 127;
    static private final char BULLET = 149;

    private static final int TEXT_CAP = 45;

    private static char lastTyped = '\n';

    @SpirePatch(
            clz = ScrollInputProcessor.class,
            method = "keyTyped"
    )
    public static class receiveTyping
    {
        @SpirePrefixPatch
        public static void readKeyboardInput(ScrollInputProcessor __instance, char character)
        {
            // Disallow "typing" most ASCII control characters, which would show up as a space when onlyFontChars is true.
            lastTyped = '\n';
            switch (character) {
                case ENTER_ANDROID:
                case ENTER_DESKTOP:
                    onPushEnter();
                    return;
                case BACKSPACE:
                    break;
                default:
                    if (character < 32) return;
            }

            if (UIUtils.isMac && Gdx.input.isKeyPressed(Input.Keys.SYM)) return;

            boolean backspace = character == BACKSPACE;
            boolean add = (FontHelper.tipHeaderFont.getData().hasGlyph(character));

            if (backspace && text.length() > 1) {
                lastTyped = BACKSPACE;
                text = text.substring(0, text.length() - 1);
                //scheduleKeyRepeatTask()
                return;
            }
            else if (backspace)
            {
                lastTyped = BACKSPACE;
                text = "";
                return;
            }
            if (add) {
                if (text.length() < TEXT_CAP)
                {
                    lastTyped = character;
                    text = text.concat(String.valueOf(character));
                }
                //scheduleKeyRepeatTask()
            }
        }
    }

    public static void reset()
    {
        text = "";
    }

    public static void onPushEnter()
    {
        chat.onPushEnter();
    }


    class KeyRepeatTask extends Timer.Task {
        int keycode;

        public void run () {
            if (lastTyped != '\n')
            {
                Gdx.input.getInputProcessor().keyTyped(lastTyped);
            }
        }
    }
}