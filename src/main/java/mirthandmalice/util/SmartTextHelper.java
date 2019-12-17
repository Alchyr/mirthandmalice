package mirthandmalice.util;

public class SmartTextHelper {
    public static String clearSmartText(String msg)
    {
        msg = msg.replaceAll("#[rgbpy]", ""); //no colors.
        msg = msg.replaceAll("~\\S+~", ""); //no waveys
        msg = msg.replaceAll("@\\S+@", ""); //no mega-waveys

        return msg;
    }
}
