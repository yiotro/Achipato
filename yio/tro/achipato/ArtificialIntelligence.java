package yio.tro.achipato;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ivan on 13.08.2014.
 */
public abstract class ArtificialIntelligence {

    GameController gameController;
    Graph graph;
    int appurtenance, enemyAppurtenance;
    ArrayList<Integer> buildOrder;
    Random random;
    public static final int BEHAVIOR_SCOUT = 0;
    public static final int BEHAVIOR_ATTACK = 1;
    public static final int BEHAVIOR_DEFEND = 2;
    int behaviorState;

    protected ArtificialIntelligence(GameController gameController, int appurtenance, int enemyAppurtenance) {
        this.gameController = gameController;
        this.appurtenance = appurtenance;
        this.enemyAppurtenance = enemyAppurtenance;
        graph = gameController.getGraphByAppurtenance(appurtenance);
        buildOrder = new ArrayList<Integer>();
        random = new Random();
    }

    abstract void analyzeAndOperate();

    abstract void preparationsForNewGame();

    abstract void alertAboutNewEnemyModule(Module module);

    abstract void alertAboutDestroyedEnemyModule(Module module);

    boolean canBuildNextModuleFromBuildOrder() {
        if (buildOrder.size() < 1) return false;
        return gameController.canBuildModule(buildOrder.get(0), appurtenance);
    }

    Module createModuleByIndex(int moduleIndex, double x, double y) {
        switch (moduleIndex) {
            case Module.MODULE_INDEX_LOOKOUT: return new ModuleLookout(x, y, appurtenance, graph);
            case Module.MODULE_INDEX_EXTRACTOR: return new ModuleExtractor(x, y, appurtenance, graph);
            case Module.MODULE_INDEX_DEFENSE: return new ModuleDefense(x, y, appurtenance, graph);
            case Module.MODULE_INDEX_BARRACKS: return new ModuleBarracks(x, y, appurtenance, graph);
            case Module.MODULE_INDEX_BASE: return new ModuleBase(x, y, appurtenance, graph);
            default: return new ModuleBase(x, y, appurtenance, graph);
        }
    }

    abstract void unitControl();
}
