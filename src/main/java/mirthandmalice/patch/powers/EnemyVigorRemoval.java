package mirthandmalice.patch.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;
import javassist.*;

@SpirePatch(
        clz = VigorPower.class,
        method = SpirePatch.CONSTRUCTOR
)
public class EnemyVigorRemoval {
    public static void Raw(CtBehavior ctBehavior) throws CannotCompileException {
        CtClass vigorClass = ctBehavior.getDeclaringClass();

        CtMethod removeAtEndOfTurn = CtNewMethod.make(
                "public void atEndOfTurn(boolean isPlayer) { if (!isPlayer) $0.addToBot(new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction($0.owner, $0.owner, $0)); }",
                vigorClass);
        vigorClass.addMethod(removeAtEndOfTurn);
    }
}
