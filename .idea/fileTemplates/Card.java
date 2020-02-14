
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import mirthandmalice.abstracts.#if ( ${PACKAGE_NAME.contains( "neutral" )})NeutralCard#elseif ( ${PACKAGE_NAME.contains( "cards.mirth" )})MirthCard#else MaliceCard#end;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class ${NAME} extends #if ( ${PACKAGE_NAME.contains( "neutral" )})NeutralCard#elseif ( ${PACKAGE_NAME.contains( "cards.mirth" )})MirthCard#else MaliceCard#end {
    private final static CardInfo cardInfo = new CardInfo(
            "${NAME}",
            ${COST},
            CardType.#if ( ${TYPE.startsWith( "p" )} )POWER#elseif ( ${TYPE.startsWith( "s" )} )SKILL#else ATTACK#end,
            CardTarget.NONE,
            CardRarity.#if ( ${PACKAGE_NAME.contains( "basic" )})BASIC#elseif ( ${PACKAGE_NAME.contains( "uncommon" )})UNCOMMON#elseif ( ${PACKAGE_NAME.contains( "common" )})COMMON#elseif ( ${PACKAGE_NAME.contains( "rare" )})RARE#else SPECIAL#end
    );
    // ${TYPE}
    
    public static final String ID = makeID(cardInfo.cardName);
    
    #if (${UPGRADE_COST} != "")    private static final int UPG_COST = ${UPGRADE_COST};
#end

    #if (${DAMAGE} != "")    private static final int DAMAGE = ${DAMAGE};
#end#if (${UPGRADE_DAMAGE} != "")    private static final int UPG_DAMAGE = ${UPGRADE_DAMAGE};

#end#if (${BLOCK} != "")    private static final int BLOCK = ${BLOCK};
#end#if (${UPGRADE_BLOCK} != "")    private static final int UPG_BLOCK = ${UPGRADE_BLOCK};

#end#if (${MAGIC} != "")    private static final int MAGIC = ${MAGIC};
#end#if (${UPGRADE_MAGIC} != "")    private static final int UPG_MAGIC = ${UPGRADE_MAGIC};
#end

    public ${NAME}() {
        super(cardInfo, #if (${UPGRADE_DESCRIPTION} != "") true#else false#end);
        
#if (${UPGRADE_COST} != "")        setCostUpgrade(UPG_COST);
#end
#if (${DAMAGE} != "")        setDamage(DAMAGE#end#if (${UPGRADE_DAMAGE} != ""), UPG_DAMAGE);#elseif (${DAMAGE} != "") );#end
#if (${BLOCK} != "")        setBlock(BLOCK#end#if (${UPGRADE_BLOCK} != ""), UPG_BLOCK);#elseif (${BLOCK} != "") );#end
#if (${MAGIC} != "")        setMagic(MAGIC#end#if (${UPGRADE_MAGIC} != ""), UPG_MAGIC);#elseif (${MAGIC} != "") );#end
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // :)
    }
    
    @Override
    public AbstractCard makeCopy() {
        return new ${NAME}();
    }
}