package yio.tro.achipato;

/**
 * Created by ivan on 09.08.2014.
 */
public class ModuleExtractor extends Module {

    int moneySpawnDelay;
    long currentTime, lastTimeMoneySpawned;

    public ModuleExtractor(double x, double y, int appurtenance, Graph graph) {
        super(x, y, appurtenance, graph);
        timeToEndConstruction = System.currentTimeMillis() + 5000;
        moneySpawnDelay = 2500;
        price = GameController.PRICE_EXTRACTOR;
        maxHP = hp = 10;
        index = MODULE_INDEX_EXTRACTOR;
    }

    @Override
    void move() {
        super.move();
        currentTime = System.currentTimeMillis();
        if (!isConstructing && currentTime > lastTimeMoneySpawned + moneySpawnDelay) {
            lastTimeMoneySpawned = currentTime;
            graph.gameController.spawnMoneyByExtractor(this);
        }
    }

    @Override
    public void timeCorrection(long correction) {
        lastTimeMoneySpawned += correction;
    }
}
