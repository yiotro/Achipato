package yio.tro.achipato;

import android.util.Log;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;

/**
 * Created by ivan on 26.08.2014.
 */
public class AiStrategyUnits extends ArtificialIntelligence{

    Module moduleUnderAttack;
    boolean lockBehavior;
    long timeToUnlockBehavior;
    WaypointGraph waypointGraph;
    ArrayList<WayPoint> wayPoints;
    boolean letsCancelTwice;
    long timeToCancelTwice, lastTimeBehaviorChanged;
    boolean lastDefenseModuleOnTheLeft;
    ArrayList<ModuleDefense> defenseModulesInLeftUpperSector;
    int howManyDefensesEnemyHas;
    double defX, defY;
    UnitGroup unitGroup;

    public AiStrategyUnits(GameController gameController, int appurtenance, int enemyAppurtenance) {
        super(gameController, appurtenance, enemyAppurtenance);
        setBehavior(BEHAVIOR_SCOUT);
        defenseModulesInLeftUpperSector = new ArrayList<ModuleDefense>();
    }

    void createRandomBuildOrder() {
        for (int i=0; i<16; i++) {
            if (random.nextDouble() > 0.5) buildOrder.add(Integer.valueOf(Module.MODULE_INDEX_EXTRACTOR));
            else buildOrder.add(Integer.valueOf(Module.MODULE_INDEX_BARRACKS));
        }
        for (int i=0; i<24; i++) {
            if (random.nextDouble() > 0.8) buildOrder.add(Integer.valueOf(Module.MODULE_INDEX_EXTRACTOR));
            else buildOrder.add(Integer.valueOf(Module.MODULE_INDEX_BARRACKS));
        }
        for (int i=0; i<8; i++) {
            buildOrder.add(Integer.valueOf(Module.MODULE_INDEX_DEFENSE));
        }
    }

    void createEarlyRushBuildOrder() {
        for (int i=0; i<8; i++) {
            buildOrder.add(Integer.valueOf(Module.MODULE_INDEX_BARRACKS));
        }
        createRandomBuildOrder();
    }

    void createEconomyBuildOrder() {
        for (int i=0; i<16; i++) {
            if (random.nextDouble() > 0.2) buildOrder.add(Integer.valueOf(Module.MODULE_INDEX_EXTRACTOR));
            else buildOrder.add(Integer.valueOf(Module.MODULE_INDEX_BARRACKS));
        }
        for (int i=0; i<24; i++) {
            if (random.nextDouble() > 0.8) buildOrder.add(Module.MODULE_INDEX_EXTRACTOR);
            else buildOrder.add(Integer.valueOf(Module.MODULE_INDEX_BARRACKS));
        }
        for (int i=0; i<8; i++) {
            buildOrder.add(Integer.valueOf(Module.MODULE_INDEX_DEFENSE));
        }
    }

    PointLighty getBestSuitedPointForScouting() {
        PointLighty point = new PointLighty();
        Graph enemyGraph = gameController.getGraphByAppurtenance(enemyAppurtenance);
        for (int i=0; i<10; i++) {
            point.x = random.nextFloat() * Gdx.graphics.getWidth();
            point.y = random.nextFloat() * 0.5f * Gdx.graphics.getHeight() + 0.2f * Gdx.graphics.getHeight();
            if (enemyGraph.distanceToClosestDefenseModule(point.x, point.y) > 5 * gameController.cacheUnitsCellSize) return point;
        }
        return point;
    }

    void lockBehaviorForSomeTime(int time) {
        lockBehavior = true;
        timeToUnlockBehavior = System.currentTimeMillis() + time;
    }

    void sendUnitsToPosition(PointLighty pointLighty) {
        sendUnitsToPosition(pointLighty.x, pointLighty.y);
    }

    void sendUnitsToPosition(double x, double y) {
        if (unitGroup == null) return;
        wayPoints = waypointGraph.findWayModern(new WayPoint(null, unitGroup.approximateX, unitGroup.approximateY), new WayPoint(null, x, y));
        for (int i=0; i<wayPoints.size(); i++)
            if (wayPoints.get(i) == null) {
                YioGdxGame.say("way point is null : " + i);
                wayPoints.clear();
                return;
            }
    }

    @Override
    void unitControl() {
        if (wayPoints != null && wayPoints.size() > 0) {
            if (unitGroup == null) return;
            WayPoint firstPoint = wayPoints.get(0);
            if (firstPoint != null) {
                unitGroup.setPosition(firstPoint.getX(), firstPoint.getY());
                if (YioGdxGame.distance(unitGroup.approximateX, unitGroup.approximateY, firstPoint.getX(), firstPoint.getY()) < 5 * gameController.yioGdxGame.gameView.unitRadius) {
                    wayPoints.remove(0);
                }
            }
        }

        switch (behaviorState) {
            case BEHAVIOR_SCOUT:
                if (wayPoints == null || wayPoints.size() == 0) {
                    sendUnitsToPosition(getBestSuitedPointForScouting());
                }
                break;
            case BEHAVIOR_ATTACK:
                checkToFindNewModuleForAttack();
                if (moduleUnderAttack == null) break;
                if (isStanding()) {
                    sendUnitsToPosition(moduleUnderAttack.x, moduleUnderAttack.y);
                }
                break;
            case BEHAVIOR_DEFEND:
                if (isStanding()) {
                    if (unitGroup == null) return;
                    if (unitGroup.approximateY < 0.7 * Gdx.graphics.getHeight() || random.nextDouble() < 0.01) {
                        double x = random.nextDouble() * Gdx.graphics.getWidth();
                        double y = 0.7 * Gdx.graphics.getHeight() - 0.2 * random.nextDouble();
                        sendUnitsToPosition(x, y);
                    }
                }
                break;
        }
    }

    boolean isStanding() {
        if (wayPoints == null) return true;
        if (unitGroup == null) return false; // do not touch it if null
        return wayPoints.size() == 0 ;
    }

    void checkToFindNewModuleForAttack() {
        if (unitGroup == null) return;
        if (moduleUnderAttack == null || !moduleUnderAttack.alive) {
            Module unProtectedModule = gameController.getGraphByAppurtenance(enemyAppurtenance).findNotProtectedModule();
            Module closestModule = gameController.getGraphByAppurtenance(enemyAppurtenance).findClosestModuleToPoint(unitGroup.x, unitGroup.y);
            if (unProtectedModule != null) {
                moduleUnderAttack = unProtectedModule;
                if (    YioGdxGame.distance(unitGroup.approximateX, unitGroup.approximateY, unProtectedModule.x, unProtectedModule.y) > 2.5 *
                        YioGdxGame.distance(unitGroup.approximateX, unitGroup.approximateY, closestModule.x, closestModule.y))
                            moduleUnderAttack = closestModule;
            } else {
                moduleUnderAttack = closestModule;
            }
        }
    }

    void updateBehavior() {
        if (lockBehavior) return;
        int enemyArmy = gameController.howManyUnitsWithThisAppurtenance(enemyAppurtenance) + 1;
        int myArmy = gameController.howManyUnitsWithThisAppurtenance(appurtenance) + 1;
        float k = (float)myArmy / (float)enemyArmy;

        switch (behaviorState) {
            case BEHAVIOR_SCOUT:
                if (myArmy > 25 || enemyArmy > 15) {
                    setBehavior(BEHAVIOR_DEFEND);
                }
                break;
            case BEHAVIOR_ATTACK:
                if (myArmy < 25) setBehavior(BEHAVIOR_DEFEND);
                if (unitGroup != null && moduleUnderAttack != null && unitGroup.numberOfUnitsNearApproximatePoint() < moduleUnderAttack.hp && System.currentTimeMillis() > lastTimeBehaviorChanged + 5000) setBehavior(BEHAVIOR_DEFEND);
//                if (myArmy < 75 && gameController.getGraphByAppurtenance(enemyAppurtenance).howManyModulesWithThisIndex(Module.MODULE_INDEX_DEFENSE) > 1) behaviorState = BEHAVIOR_DEFEND;
                break;
            case BEHAVIOR_DEFEND:
                if (unitGroup != null && unitGroup.numberOfUnitsNearApproximatePoint() > 25) {
                    setBehavior(BEHAVIOR_ATTACK);
                }
                break;
        }
    }

    void setBehavior(int behavior) {
        behaviorState = behavior;
        if (wayPoints != null) wayPoints.clear();
        if (behavior == BEHAVIOR_DEFEND) moduleUnderAttack = null;
        YioGdxGame.say(getBehaviorString());
        lastTimeBehaviorChanged = System.currentTimeMillis();
    }

    String getBehaviorString() {
        switch (behaviorState) {
            case BEHAVIOR_DEFEND: return "behavior : DEFEND";
            case BEHAVIOR_ATTACK: return "behavior : ATTACK";
            case BEHAVIOR_SCOUT: return "behavior : SCOUT";
            default: return "ERROR behavior";
        }
    }

    @Override
    void alertAboutNewEnemyModule(Module module) {
        if (module.y > 0.5 * Gdx.graphics.getHeight() && unitGroup != null && unitGroup.numberOfUnitsNearApproximatePoint() > module.maxHP && behaviorState == BEHAVIOR_DEFEND) {
            moduleUnderAttack = module;
            setBehavior(BEHAVIOR_ATTACK);
        }
        if (module.index == Module.MODULE_INDEX_DEFENSE) {
            howManyDefensesEnemyHas++;
        }
        if (buildOrder.size() < 1) return;
        Graph enemyGraph = gameController.getGraphByAppurtenance(enemyAppurtenance);
        if (graph.howManyModulesWithThisIndex(Module.MODULE_INDEX_BARRACKS) < enemyGraph.howManyModulesWithThisIndex(Module.MODULE_INDEX_BARRACKS) + 2
                && buildOrder.get(0) != Module.MODULE_INDEX_BARRACKS
                && buildOrder.get(0) != Module.MODULE_INDEX_DEFENSE) {
            buildOrder.add(0, Integer.valueOf(Module.MODULE_INDEX_BARRACKS));
        }
    }

    @Override
    void alertAboutDestroyedEnemyModule(Module module) {
        if (module.index == Module.MODULE_INDEX_DEFENSE) howManyDefensesEnemyHas--;
        if (howManyDefensesEnemyHas < 0) YioGdxGame.say("somehow player has <0 defense modules. check AI units");
    }

    void checkToDefend() {
        int enemyArmy = gameController.howManyUnitsWithThisAppurtenance(enemyAppurtenance) + 1;
        int myArmy = gameController.howManyUnitsWithThisAppurtenance(appurtenance) + 1;
        float k = (float)myArmy / (float)enemyArmy;
        if (k < 0.5 && enemyArmy > 50) {
            int co = 0;
            for (int i=0; i<buildOrder.size(); i++) {
                if (buildOrder.get(i) == Module.MODULE_INDEX_DEFENSE) co++;
                else break;
            }
            int n = 3 - graph.howManyModulesWithThisIndex(Module.MODULE_INDEX_DEFENSE) - co;
            for (int i=0; i<n; i++) {
                buildOrder.add(Integer.valueOf(Module.MODULE_INDEX_DEFENSE));
            }
        }
        UnitGroup enemyGroup = gameController.getClosestUnitGroup(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), GameController.APPURTENANCE_GREEN, false);
        if (enemyGroup != null && enemyArmy > 30 && enemyGroup.approximateY > 0.5f * Gdx.graphics.getHeight()) {
            Module module = graph.findClosestModuleToPoint(enemyGroup.approximateX, enemyGroup.approximateY);
            if (module == null) return;
            defX = 0.5 * (enemyGroup.approximateX + module.x);
            defY = 0.5 * (enemyGroup.approximateY + module.y);
            if (buildOrder.get(0) != Module.MODULE_INDEX_DEFENSE) buildOrder.add(Integer.valueOf(Module.MODULE_INDEX_DEFENSE));
        }
    }

    int howManyAliveModulesInArrayList(ArrayList<ModuleDefense> moduleArrayList) {
        int c = 0;
        for (int i=moduleArrayList.size()-1; i>=0; i--) {
            if (moduleArrayList.get(i).alive) c++;
        }
        return c;
    }

    @Override
    void analyzeAndOperate() {
        if (!letsCancelTwice && canBuildNextModuleFromBuildOrder()) {
            double x = 0, y = 0;
            if (howManyAliveModulesInArrayList(defenseModulesInLeftUpperSector) > 0) {
                buildOrder.set(0, Integer.valueOf(Module.MODULE_INDEX_DEFENSE));
                YioGdxGame.say("reacting to fact that player has defense in left upper sector");
            }
            if (buildOrder.get(0) != Module.MODULE_INDEX_DEFENSE) {
                x = Gdx.graphics.getWidth();
                y = Gdx.graphics.getHeight();
            } else {
                x = (0.1 + 0.8 * YioGdxGame.random.nextDouble()) * Gdx.graphics.getWidth();
                y = 0.4 * Gdx.graphics.getHeight();
            }
            Module module = createModuleByIndex(buildOrder.get(0), x, y);
            module.graph.findBetterPlaceForModule(module);
            gameController.buildModule(module);
            buildOrder.remove(0);
            int s = graph.modules.size();
            if (s == 20 || s == 21 || s == 25) buildOrder.add(0, Integer.valueOf(Module.MODULE_INDEX_DEFENSE));
        }
        if (lockBehavior && System.currentTimeMillis() > timeToUnlockBehavior) lockBehavior = false;

        checkToDefend();
        updateBehavior();
        unitControl();
        if (letsCancelTwice && System.currentTimeMillis() > timeToCancelTwice) {
            letsCancelTwice = false;
            gameController.demolishLastBuiltModule(appurtenance);
            gameController.demolishLastBuiltModule(appurtenance);
        }
    }

    @Override
    void preparationsForNewGame() {
        waypointGraph = gameController.waypointGraph;
        for (int i=0; i<4; i++) buildOrder.add(Integer.valueOf(Module.MODULE_INDEX_BARRACKS));
        if (random.nextDouble() < 0.8) {
            createRandomBuildOrder();
        } else if (random.nextDouble() < 0.5) {
            createEconomyBuildOrder();
        } else {
            createEarlyRushBuildOrder();
        }
//        letsCancelTwice = true;
//        timeToCancelTwice = System.currentTimeMillis() + 2000;
        lastDefenseModuleOnTheLeft = false;
        defenseModulesInLeftUpperSector.clear();
        howManyDefensesEnemyHas = 0;
        unitGroup = gameController.findUnitGroupByAppurtenance(appurtenance);
        if (unitGroup == null) YioGdxGame.say("unit group is null");
    }
}
