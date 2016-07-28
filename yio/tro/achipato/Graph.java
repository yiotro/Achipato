package yio.tro.achipato;

import android.util.Log;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by ivan on 09.08.2014.
 */
public class Graph {

    ArrayList<Module> modules;
    ArrayList<Link> links;
    double minDistanceBetweenModules, maxDistanceBetweenModules;
    GameController gameController;
    int appurtenance;
    Module cacheMatrixModules[][];
    float cacheCellSize;

    public Graph(GameController gameController, int appurtenance) {
        this.gameController = gameController;
        this.appurtenance = appurtenance;
        modules = new ArrayList<Module>();
        links = new ArrayList<Link>();
        minDistanceBetweenModules = 0.12 * Gdx.graphics.getWidth();
        maxDistanceBetweenModules = 0.2 * Gdx.graphics.getWidth();
        cacheCellSize = GameView.moduleSize;
        cacheMatrixModules = new Module[(int)(Gdx.graphics.getWidth() / cacheCellSize) + 1][(int)(Gdx.graphics.getHeight() / cacheCellSize) + 1];
        clearCache();
    }

    public void addModule(Module module) {
        if (hasThisModule(module)) return;
        ListIterator iterator = modules.listIterator();
        iterator.add(module);
        double distTemp;
        for (Module temp : modules) {
            distTemp = distanceBetweenModules(temp, module);
            if (distTemp > minDistanceBetweenModules && distTemp < maxDistanceBetweenModules) {
                connectModules(temp, module);
            }
        }
        cacheMatrixModules[(int)(module.x / cacheCellSize)][(int)(module.y / cacheCellSize)] = module;
    }

    void clearCache() {
        for (int i=0; i<cacheMatrixModules.length; i++) {
            for (int j=0; j<cacheMatrixModules[i].length; j++) {
                cacheMatrixModules[i][j] = null;
            }
        }
    }

    public double distanceBetweenModules(Module first, Module second) {
        return YioGdxGame.distance(first.x, first.y, second.x, second.y);
    }

    void connectModules(Module first, Module second) {
        first.connectToModule(second);
        second.connectToModule(first);
        addNewLink(first, second);
    }

    void addNewLink(Module first, Module second) {
        if (hasLinkWithTheseModules(first, second)) return;
        ListIterator iterator = links.listIterator();
        iterator.add(new Link(first, second));
    }

    boolean hasThisModule(Module module) {
        for (Module m : modules)
            if (m == module) return true;
        return false;
    }

    double getMinDistanceToPoint(PointLighty point) {
        double minDist, curDist;
        if (modules.size() < 1) return 0;
        minDist = YioGdxGame.distance(modules.get(0).x, modules.get(0).y, point.x, point.y);
        for (Module m : modules) {
            curDist = YioGdxGame.distance(m.x, m.y, point.x, point.y);
            if (curDist < minDist) minDist = curDist;
        }
        return minDist;
    }

    boolean hasLinkWithTheseModules(Module first, Module second) {
        for (Link link : links) {
            if (link.first == first && link.second == second) return true;
            if (link.second == first && link.first == second) return true;
        }
        return false;
    }

    void move() {
        Module module;
        for (int i=modules.size()-1; i>=0; i--) {
            module = modules.get(i);
            module.move();
            if (module.hp <= 0) destroyModule(module);
        }
        for (Link link : links) {
            link.factorWidth.move();
        }
    }

    void destroyModule(Module module) {
        module.alive = false;
        gameController.bubbleExplosion((float)module.x, (float)module.y, 0.5f * GameView.moduleSize, 0.02f * GameView.moduleSize, 25);
        YioGdxGame.playSound(gameController.soundExplosion);
        if (module.appurtenance == GameController.APPURTENANCE_GREEN) {
            Light light = new Light((float)module.visualX, (float)module.visualY, module.afterConstructionVisibilityRange);
            light.factorOfLighting.factor = 1;
            light.destroyIllumination();
            gameController.lights.add(light);
            gameController.activateAllWayPoints();
            gameController.greenGraph.deactivateWayPointsAgain();
            gameController.waypointGraph.deactivateUnreachablePoints();
            for (Bubble obstacle : gameController.obstacles) gameController.waypointGraph.deactivateSomePointsByObstacle(obstacle);
        }
        if (module instanceof ModuleDefense) gameController.defenseModuleList.remove(module);
        destroyAllLinksWithThisModule(module);

        ListIterator iterator = modules.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next() == module) {
                iterator.remove();
                return;
            }
        }
    }

    void destroyAllLinksWithThisModule(Module module) {
        for (Link link : links) {
            if (link.containsModule(module)) link.factorWidth.beginSlowDestroyProcess();
        }
    }

    int howManyModulesWithThisIndex(int index) {
        int count = 0;
        for (Module m : modules)
        if (m.index == index) count++;
        return count;
    }

    void pushAwayFromModule(Module pusher, Module module) {
        double difference = 1.01 * minDistanceBetweenModules - distanceBetweenModules(pusher, module);
        if (difference > 0) {
            double a = YioGdxGame.angle(pusher.x, pusher.y, module.x, module.y);
            module.x += difference * Math.cos(a);
            module.y += difference * Math.sin(a);
        }
    }

    Module findClosestModuleToPoint(double x, double y) {
        if (modules.size() < 1) return null;
        double minDist, curDist;
        Module m = modules.get(0);
        minDist = YioGdxGame.distance(x, y, m.x, m.y);
        for (Module temp : modules) {
            if (!temp.alive) continue;
            curDist = YioGdxGame.distance(x, y, temp.x, temp.y);
            if (curDist < minDist) {
                minDist = curDist;
                m = temp;
            }
        }
        return m;
    }

    double distanceToClosestDefenseModule(double x, double y) {
        double minDist, curDist;
        minDist = Gdx.graphics.getWidth() + Gdx.graphics.getHeight();
        if (howManyModulesWithThisIndex(Module.MODULE_INDEX_DEFENSE) == 0) return minDist;
        for (Module temp : modules) {
            if (!temp.alive) continue;
            if (temp.index == Module.MODULE_INDEX_DEFENSE) {
                curDist = YioGdxGame.distance(x, y, temp.x, temp.y);
                if (curDist < minDist) {
                    minDist = curDist;
                }
            }
        }
        return minDist;
    }

    Module findNotProtectedModule() {
        if (modules.size() < 1) return null;
        for (Module module : modules) {
            if (module.index != Module.MODULE_INDEX_DEFENSE && distanceToClosestDefenseModule(module.x, module.y) > 5 * gameController.cacheUnitsCellSize) return module;
        }
        return null;
    }



    boolean isModuleTooCloseToOthers(Module module) {
        for (Module m : modules) {
            if (distanceBetweenModules(m , module) < minDistanceBetweenModules) return true;
        }
        return false;
    }

    boolean placeModuleBySquareSocketFromBottom(Module module) {
        double upLim = Gdx.graphics.getHeight() - 1.5 * gameController.bandHeight;
        double rightLim = Gdx.graphics.getWidth() - 0.5 * minDistanceBetweenModules;
        Module currentClosest;
        for (double y = 1.5 * gameController.bandHeight; y < upLim; y += 1.05 * minDistanceBetweenModules) {
            for (double x = 0.5 * minDistanceBetweenModules; x < rightLim; x += 1.02 * minDistanceBetweenModules) {
                currentClosest = findClosestModuleToPoint(x, y);
                if (YioGdxGame.distance(x, y, currentClosest.x, currentClosest.y) > minDistanceBetweenModules
                        && !isPointInObstacle(x, y)) {
                    module.x = x;
                    module.y = y;
                    pullModuleToClosest(module);
                    if (isPointInObstacle(module.x, module.y)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    boolean placeModuleBySquareSocketFromTop(Module module) {
        double downLim = 1.5 * gameController.bandHeight;
        double leftLim = 0.5 * minDistanceBetweenModules;
        Module currentClosest;
        for (double y = Gdx.graphics.getHeight() - 1.5 * gameController.bandHeight; y > downLim; y -= 1.05 * minDistanceBetweenModules) {
            for (double x = Gdx.graphics.getWidth() - 0.5 * minDistanceBetweenModules; x > leftLim; x -= 1.02 * minDistanceBetweenModules) {
                currentClosest = findClosestModuleToPoint(x, y);
                if (YioGdxGame.distance(x, y, currentClosest.x, currentClosest.y) > minDistanceBetweenModules
                        && gameController.obstacleCache[(int)(x / gameController.cacheUnitsCellSize)][(int)(y / gameController.cacheUnitsCellSize)].size() == 0) {
                    module.x = x;
                    module.y = y;
                    return true;
                }
            }
        }
        return false;
    }

    void deactivateWayPointsAgain() {
        for (Module module : modules) {
            if (module.alive) {
                module.deactivateWayPoints();
            }
        }
    }

    void clear() {
        clearCache();
        ListIterator iterator = modules.listIterator();
        while (iterator.hasNext()) {
            Module module = (Module)iterator.next();
            module.connectedModules.clear();
            iterator.remove();
        }
        ListIterator iterator2 = links.listIterator();
        while (iterator2.hasNext()) {
            iterator2.next();
            iterator2.remove();
        }
    }

    boolean tryToFindSuitablePlaceNearby(Module module) {
        double mx = module.x;
        double my = module.y;
        double angle, r;
        for (int i=0; i<15; i++) {
            angle = 2d * Math.PI * gameController.predictableRandom.nextDouble();
            r = (1 + 4d * (double)i / 15d) * gameController.predictableRandom.nextDouble() * GameView.moduleSize;
            module.x = mx + r * Math.cos(angle);
            module.y = my + r * Math.sin(angle);
            limitModule(module);
            if (!isModuleTooCloseToOthers(module)) return true;
        }
        return false;
    }

    void limitModule(Module module) {
        if (module.y < gameController.bandHeight) module.y = 1.5 * gameController.bandHeight;
        if (module.y > Gdx.graphics.getHeight() - gameController.bandHeight) module.y = Gdx.graphics.getHeight() - 1.5 * gameController.bandHeight;
        if (module.x < 0) module.x = gameController.bandHeight;
        if (module.x > Gdx.graphics.getWidth()) module.x = Gdx.graphics.getWidth() - gameController.bandHeight;
    }

    boolean isPointInObstacle(double x, double y) {
        ArrayList<Bubble> list = gameController.obstacleCache[(int)(x / gameController.cacheUnitsCellSize)][(int)(y / gameController.cacheUnitsCellSize)];
        if (list.size() == 0) return false;
        Bubble obs = list.get(0);
        if (YioGdxGame.distance(x, y, obs.x, obs.y) < obs.r + 0.5 * GameView.moduleSize) return true;
        return false;
    }

    void pullModuleToClosest(Module module) {
        Module closestModule = findClosestModuleToPoint(module.x, module.y);
        if (closestModule == null) return;
        double a = YioGdxGame.angle(module.x, module.y, closestModule.x, closestModule.y);
        double difference = distanceBetweenModules(module, closestModule) - 1.01 * minDistanceBetweenModules;
        module.x += difference * Math.cos(a);
        module.y += difference * Math.sin(a);
    }

    public boolean findBetterPlaceForModule(Module module) {
        for (Module m : modules) pushAwayFromModule(m, module);
        limitModule(module);
        if (isModuleTooCloseToOthers(module)) {
            if (!tryToFindSuitablePlaceNearby(module) || isPointInObstacle(module.x, module.y)) {
                switch (module.appurtenance) {
                    case GameController.APPURTENANCE_GREEN:
                        if (!placeModuleBySquareSocketFromBottom(module)) return false;
                        break;
                    case GameController.APPURTENANCE_RED:
                        if (!placeModuleBySquareSocketFromTop(module)) return false;
                        break;
                }
            }
        } else {
            pullModuleToClosest(module);
            if (isPointInObstacle(module.x, module.y)) return false;
        }
        return true;
    }
}
