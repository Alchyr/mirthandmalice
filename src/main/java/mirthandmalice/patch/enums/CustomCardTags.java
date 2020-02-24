package mirthandmalice.patch.enums;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class CustomCardTags {
    @SpireEnum
    public static AbstractCard.CardTags MK_ECHO_ATTACK;
    @SpireEnum
    public static AbstractCard.CardTags MK_ECHO_SKILL;
    @SpireEnum
    public static AbstractCard.CardTags MK_ECHO_POWER;
    @SpireEnum
    public static AbstractCard.CardTags MM_BURST; //if enemy lost hp this turn
    @SpireEnum
    public static AbstractCard.CardTags MK_FRAGMENT;

    @SpireEnum
    public static AbstractCard.CardTags MM_CLAIM; //for non-attack cards that claim, so that block is calculated correctly
}
