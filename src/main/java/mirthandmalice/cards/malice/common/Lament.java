package mirthandmalice.cards.malice.common;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.green.PiercingWail;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import mirthandmalice.abstracts.MaliceCard;
import mirthandmalice.actions.general.AllEnemyLoseHPAction;
import mirthandmalice.util.CardInfo;

import static mirthandmalice.MirthAndMaliceMod.makeID;

public class Lament extends MaliceCard {
    private final static CardInfo cardInfo = new CardInfo(
            "Lament",
            1,
            CardType.SKILL,
            CardTarget.ALL_ENEMY,
            CardRarity.COMMON);
    // skill

    public static final String ID = makeID(cardInfo.cardName);


    private static final int MAGIC = 6;
    private static final int UPG_MAGIC = 3;

    public Lament() {
        super(cardInfo, false);

        setMagic(MAGIC, UPG_MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new SFXAction("ATTACK_PIERCING_WAIL", -0.4f, true));
        addToBot(new VFXAction(new ShockWaveEffect(p.hb.cX, p.hb.cY, Color.BLACK, ShockWaveEffect.ShockWaveType.CHAOTIC), 0.0f));
        addToBot(new VFXAction(new ShockWaveEffect(p.hb.cX, p.hb.cY, Color.DARK_GRAY, ShockWaveEffect.ShockWaveType.CHAOTIC), 0.0f));
        addToBot(new VFXAction(new ShockWaveEffect(p.hb.cX, p.hb.cY, Color.GRAY, ShockWaveEffect.ShockWaveType.CHAOTIC), 0.0f));
        addToBot(new AllEnemyLoseHPAction(p, this.magicNumber, AbstractGameAction.AttackEffect.NONE));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Lament();
    }
}