package yio.tro.achipato;

/**
 * Created by ivan on 09.08.2014.
 */
public class ModuleLookout extends Module{

    public ModuleLookout(double x, double y, int appurtenance, Graph graph) {
        super(x, y, appurtenance, graph);
        afterConstructionVisibilityRange = 20 * (float) visualRadius;
        timeToEndConstruction = System.currentTimeMillis() + 4000;
        price = GameController.PRICE_LOOKOUT;
        maxHP = hp = 15;
        index = MODULE_INDEX_LOOKOUT;
    }

    @Override
    public void timeCorrection(long correction) {
        
    }
}
