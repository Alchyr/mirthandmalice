package mirthandmalice.util;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;

public class MessageHelper {
    public static String cardInfoString(AbstractCard c)
    {
        //Format:
        //cardID upgraded(1/0) timesUpgraded prefix suffix cost basedamage baseblock basemagic
        StringBuilder sb = new StringBuilder(c.cardID).append("|||");

        sb.append(c.upgraded ? 1 : 0).append("|||");
        sb.append(c.timesUpgraded).append("|||");

        if (c.name.equals(c.originalName))
        {
            sb.append("!!!|||!!!|||");
        }
        else if (c.name.contains(c.originalName))
        {
            String[] parts = c.name.split(c.originalName);
            if (parts.length == 2)
            {
                if (parts[0].isEmpty())
                    sb.append("!!!|||");
                else
                    sb.append(parts[0]).append("|||");

                if (parts[1].isEmpty())
                    sb.append("!!!|||");
                else
                    sb.append(parts[1]).append("|||");
            }
            else //it contains original name multiple times??? or somehow, not at all? No idea how to handle that, so just use the original name.
            {
                sb.append("!!!|||!!!|||");
            }
        }
        else
        {
            sb.append("!!!|||!!!|||");
        }

        sb.append(c.cost).append("|||");
        sb.append(c.costForTurn).append("|||");
        sb.append(c.baseDamage).append("|||");
        sb.append(c.damage).append("|||");
        sb.append(c.baseBlock).append("|||");
        sb.append(c.block).append("|||");
        sb.append(c.baseMagicNumber).append("|||");
        sb.append(c.magicNumber);

        return sb.toString();
    }

    public static AbstractCard cardFromInfo(String cardInfo)
    {
        String[] args = cardInfo.split("\\|\\|\\|");
        AbstractCard c = CardLibrary.getCopy(args[0]);

        if (args[1].equals("1"))
        {
            for (int i = 0; i < Integer.valueOf(args[2]); ++i)
            {
                c.upgrade();
            }
        }

        String name = c.originalName;
        if (!args[3].equals("!!!"))
            name = args[3] + name;
        if (!args[4].equals("!!!"))
            name += args[4];

        c.name = name;
        c.cost = Integer.valueOf(args[5]);
        c.costForTurn = Integer.valueOf(args[6]);
        if (c.cost != c.costForTurn)
            c.isCostModifiedForTurn = true;
        c.baseDamage = Integer.valueOf(args[7]);
        c.damage = Integer.valueOf(args[8]);
        c.isDamageModified = c.baseDamage != c.damage;
        c.baseBlock = Integer.valueOf(args[9]);
        c.block = Integer.valueOf(args[10]);
        c.isBlockModified = c.baseBlock != c.block;
        c.baseMagicNumber = Integer.valueOf(args[11]);
        c.magicNumber = Integer.valueOf(args[12]);
        c.isMagicNumberModified = c.baseMagicNumber != c.magicNumber;

        return c;
    }
}
