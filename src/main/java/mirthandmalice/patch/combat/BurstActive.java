package mirthandmalice.patch.combat;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

@SpirePatch(
        clz = AbstractMonster.class,
        method = SpirePatch.CLASS
)
public class BurstActive {
    //check patch in hooks: OnEnemyAttacked and PreMonsterTurn, which calls relevant method in main class
    public static SpireField<Boolean> active = new SpireField<>(()->false);
}
