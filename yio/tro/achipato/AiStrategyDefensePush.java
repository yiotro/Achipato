package yio.tro.achipato;

import com.badlogic.gdx.Gdx;

/**
 * Created by ivan on 29.08.2014.
 */
public class AiStrategyDefensePush extends ArtificialIntelligence{

    public AiStrategyDefensePush(GameController gameController, int appurtenance, int enemyAppurtenance) {
        super(gameController, appurtenance, enemyAppurtenance);
    }

    void createEconomyBuildOrder() {
        for (int i=0; i<40; i++) {
            buildOrder.add(new Integer(Module.MODULE_INDEX_EXTRACTOR));
        }
        for (int i=0; i<8; i++) {
            buildOrder.add(new Integer(Module.MODULE_INDEX_DEFENSE));
        }
    }

    @Override
    void analyzeAndOperate() {
        if (canBuildNextModuleFromBuildOrder()) {
            if (buildOrder.get(0) != Module.MODULE_INDEX_DEFENSE)
                gameController.buildModule(createModuleByIndex(buildOrder.get(0), Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
            else gameController.buildModule(createModuleByIndex(buildOrder.get(0), 0.5f * Gdx.graphics.getWidth(), 0.5f * Gdx.graphics.getHeight()));
            buildOrder.remove(0);
        }
    }

    @Override
    void unitControl() {

    }

    @Override
    void preparationsForNewGame() {
        createEconomyBuildOrder();
        for (UnitGroup group : gameController.groupList) {
            if (group.appurtenance != appurtenance) continue;
            group.setPosition(0.7 * Gdx.graphics.getWidth(), 0.2 * Gdx.graphics.getHeight());
        }
    }

    @Override
    void alertAboutNewEnemyModule(Module module) {
        if (module.index == Module.MODULE_INDEX_BARRACKS) {
            int enemyBarracks = gameController.getGraphByAppurtenance(enemyAppurtenance).howManyModulesWithThisIndex(Module.MODULE_INDEX_BARRACKS);
            int myDefense = gameController.getGraphByAppurtenance(appurtenance).howManyModulesWithThisIndex(Module.MODULE_INDEX_DEFENSE);
            if (myDefense < enemyBarracks) buildOrder.add(0, new Integer(Module.MODULE_INDEX_DEFENSE));
        }
    }

    @Override
    void alertAboutDestroyedEnemyModule(Module module) {

    }
}
