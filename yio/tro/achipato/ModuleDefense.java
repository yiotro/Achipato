package yio.tro.achipato;

/**
 * Created by ivan on 09.08.2014.
 */
public class ModuleDefense extends Module{

    long lastTimeAttacked;
    int attackDelay;

    public ModuleDefense(double x, double y, int appurtenance, Graph graph) {
        super(x, y, appurtenance, graph);
        afterConstructionVisibilityRange = 12 * (float) visualRadius;
        timeToEndConstruction = System.currentTimeMillis() + 1500;
        price = GameController.PRICE_DEFENSE;
        maxHP = hp = 15;
        attackDelay = 900;
        index = MODULE_INDEX_DEFENSE;
    }

    boolean canAttack() {
        return System.currentTimeMillis() > lastTimeAttacked + attackDelay;
    }

    @Override
    public void timeCorrection(long correction) {
        lastTimeAttacked += correction;
    }
}
