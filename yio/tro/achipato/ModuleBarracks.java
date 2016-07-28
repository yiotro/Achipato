package yio.tro.achipato;

/**
 * Created by ivan on 09.08.2014.
 */
public class ModuleBarracks extends Module{

    int unitSpawnDelay;
    long lastTimeUnitSpawned, currentTime;

    public ModuleBarracks(double x, double y, int appurtenance, Graph graph) {
        super(x, y, appurtenance, graph);
        timeToEndConstruction = System.currentTimeMillis() + 4000;
        unitSpawnDelay = 3500;
        price = GameController.PRICE_BARRACKS;
        maxHP = hp = 10;
        index = MODULE_INDEX_BARRACKS;
    }

    @Override
    void move() {
        super.move();
        currentTime = System.currentTimeMillis();
        if (!isConstructing && currentTime > lastTimeUnitSpawned + unitSpawnDelay) {
            lastTimeUnitSpawned = currentTime;
            spawnUnit();
        }
    }

    void spawnUnit() {
        graph.gameController.spawnUnit((float)visualX, (float)visualY, appurtenance);
    }

    @Override
    public void timeCorrection(long correction) {
        lastTimeUnitSpawned += correction;
    }
}
