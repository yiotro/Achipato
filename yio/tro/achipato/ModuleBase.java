package yio.tro.achipato;

/**
 * Created by ivan on 09.08.2014.
 */
public class ModuleBase extends Module{

    public ModuleBase(double x, double y, int appurtenance, Graph graph) {
        super(x, y, appurtenance, graph);
        timeToEndConstruction = System.currentTimeMillis() + 2000;
        price = GameController.PRICE_BASE;
        maxHP = hp = 7;
        index = MODULE_INDEX_BASE;
    }

    @Override
    public void timeCorrection(long correction) {
        
    }
}
