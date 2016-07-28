package yio.tro.achipato;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ivan on 07.10.2014.
 */
public class LevelCreator {
    public static final int ID_BASE = Module.MODULE_INDEX_BASE;
    public static final int ID_LOOKOUT = Module.MODULE_INDEX_LOOKOUT;
    public static final int ID_DEFENSE = Module.MODULE_INDEX_DEFENSE;
    public static final int ID_BARRACKS = Module.MODULE_INDEX_BARRACKS;
    public static final int ID_EXTRACTOR = Module.MODULE_INDEX_EXTRACTOR;
    YioGdxGame yioGdxGame;
    GameController gameController;
    MenuControllerLighty menuControllerLighty;
    Graph greenGraph, redGraph;
    float w, h;
    int appGreen, appRed;
    Random random;

    public LevelCreator(YioGdxGame yioGdxGame, GameController gameController, MenuControllerLighty menuControllerLighty) {
        this.yioGdxGame = yioGdxGame;
        this.gameController = gameController;
        this.menuControllerLighty = menuControllerLighty;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        appGreen = GameController.APPURTENANCE_GREEN;
        appRed = GameController.APPURTENANCE_RED;
        greenGraph = gameController.greenGraph;
        redGraph = gameController.redGraph;
    }

    public void createLevel(int index) {
        random = new Random(index);
        if (index > 30 && index <= 70) {
            randomLevel();
            return;
        }
        switch (index) {
            case 0: // tutorial
                setMoney(5000, 0);
                buildModule(0.05, 0.15, ID_LOOKOUT, appGreen);
                buildModule(0.15, 0.15, ID_BARRACKS, appGreen);
                buildModule(0.05, 0.25, ID_EXTRACTOR, appGreen);
                buildModule(0.1, 0.35, ID_DEFENSE, appGreen);
                buildModule(0.2, 0.25, ID_DEFENSE, appGreen);
                buildModule(0.35, 0.15, ID_DEFENSE, appGreen);
                spawnArmy(0.1, 0.2, 200, appGreen);
                buildModule(0.95, 0.85, ID_BASE, appRed);
                for (int i=0; i<5; i++) {
                    buildModule(0.5 + 0.45 * random.nextDouble(),
                            0.6 + 0.35 * random.nextDouble(),
                            randomID(new int[]{ID_BASE, ID_DEFENSE, ID_EXTRACTOR}), appRed);
                }
                spawnArmy(0.95, 0.85, 50, appRed);
                break;
            case 1:
                setMoney(1000, 0);
                putObstacle(0, 0.9, 0.5);
                buildModule(0.05, 0.15, ID_LOOKOUT, appGreen);
                buildModule(0.95, 0.85, ID_BASE, appRed);
                for (int i=0; i<10; i++) {
                    buildModule(0.1 + 0.8 * random.nextDouble(), 0.15 + 0.2 * random.nextDouble(), ID_BARRACKS, appGreen);
                    buildModule(0.5 + 0.45 * random.nextDouble(), 0.6 + 0.35 * random.nextDouble(), ID_DEFENSE, appRed);
                }
                buildModule(0.3, 0.4, ID_LOOKOUT, appGreen);
                buildModule(0.7, 0.4, ID_LOOKOUT, appGreen);
                spawnArmy(0.3, 0.4, 50, appGreen);
                break;
            case 2:
                setMoney(500, 0);
                buildModule(0.05, 0.15, ID_BASE, appGreen);
                buildModule(0.05, 0.25, ID_BASE, appGreen);
                buildModule(0.05, 0.35, ID_BASE, appGreen);
                buildModule(0.05, 0.45, ID_DEFENSE, appGreen);
                buildModule(0.15, 0.45, ID_DEFENSE, appGreen);
                buildModule(0.1, 0.55, ID_LOOKOUT, appGreen);
                for (int i=0; i<10; i++) buildModule(0.2, 0.2, ID_BARRACKS, appGreen);
                spawnArmy(0.4, 0.4, 50, appGreen);
                buildModule(0.95, 0.85, ID_BASE, appRed);
                for (int i=0; i<15; i++) buildModule(0.8, 0.8, randomID(new int[]{ID_BARRACKS, ID_DEFENSE}), appRed);
                spawnArmy(0.8, 0.8, 50, appRed);
                break;
            case 3:
                setMoney(0, 0);
//                putObstacle(0.3, 0.6, 0.1);
//                putObstacle(0.7, 0.4, 0.1);
                obstacles7();
                commonBase1();
                spawnArmy(0.15, 0.25, 200, appGreen);
                spawnArmy(0.85, 0.75, 100, appRed);
                buildModule(0.3, 0.3, ID_LOOKOUT, appGreen);
                enableMessage();
                break;
            case 4:
                setMoney(1000, 0);
                commonBase1();
                for (int i=0; i<10; i++) buildModule(0.9, 0.8, randomID(new int[]{ID_BARRACKS, ID_DEFENSE, ID_EXTRACTOR}), appRed);
                for (int i=0; i<7; i++) buildModule(0.95, 0.15, ID_EXTRACTOR, appGreen);
                spawnArmy(0.85, 0.75, 30, appRed);
                enableMessage();
                break;
            case 5:
                setMoney(16 * GameController.PRICE_DEFENSE, 0);
                commonBase1();
                for (int i=0; i<7; i++) buildModule(0.05, 0.85, ID_BARRACKS, appRed);
                for (int i=0; i<7; i++) buildModule(0.95, 0.15, ID_EXTRACTOR, appGreen);
                for (int i=0; i<8; i++) buildModule(0.05 + i * 0.11, 0.23, ID_EXTRACTOR, appGreen);
                spawnArmy(0.05, 0.75, 200, appRed);
                enableMessage();
                break;
            case 6:
                setMoney(200, 0);
                obstacles1();
                commonBase1();
                for (int i=0; i<10; i++) buildModule(0.15, 0.15, randomID(new int[]{ID_EXTRACTOR, ID_BARRACKS}), appGreen);
                for (int i=0; i<10; i++) buildModule(0.85, 0.85, randomID(new int[]{ID_EXTRACTOR, ID_BARRACKS, ID_DEFENSE}), appRed);
                buildModule(0.2, 0.5, ID_LOOKOUT, appGreen);
                buildModule(0.7, 0.4, ID_LOOKOUT, appGreen);
                break;
            case 7:
                setMoney(500, 0);
                obstacles2();
                commonBase1();
                for (int i=0; i<7; i++) buildModule(0.05, 0.85, ID_EXTRACTOR, appRed);
                for (int i=0; i<7; i++) buildModule(0.95, 0.15, ID_EXTRACTOR, appGreen);
                for (int i=0; i<8; i++) buildModule(0.05 + i * 0.11, 0.23, ID_DEFENSE, appGreen);
                for (int i=0; i<8; i++) buildModule(0.05 + i * 0.11, 0.77, ID_DEFENSE, appRed);
                buildModule(0.5, 0.4, ID_LOOKOUT, appGreen);
                spawnArmy(0.5, 0.85, 50, appRed);
                spawnArmy(0.5, 0.15, 50, appGreen);
                break;
            case 8:
                setMoney(200, 0);
                putObstacle(0, 0.5, 0.1);
                putObstacle(1, 0.5, 0.1);
                commonBase2();
                for (int i=0; i<7; i++) buildModule(0.5, 0.15, ID_EXTRACTOR, appGreen);
                for (int i=0; i<7; i++) buildModule(0.5, 0.85, randomID(new int[]{ID_BARRACKS, ID_DEFENSE, ID_EXTRACTOR}), appRed);
                buildModule(0.2, 0.4, ID_LOOKOUT, appGreen);
                buildModule(0.8, 0.4, ID_LOOKOUT, appGreen);
                spawnArmy(0.5, 0.25, 30, appGreen);
                break;
            case 9:
                setMoney(300, 200);
                putObstacle(0, 0.5, 0.3);
                commonBase1();
                for (int i=0; i<12; i++) buildModule(0.85, 0.85, randomID(new int[]{ID_BARRACKS, ID_DEFENSE, ID_EXTRACTOR}), appRed);
                for (int i=0; i<12; i++) buildModule(0.15, 0.15, randomID(new int[]{ID_BARRACKS, ID_DEFENSE, ID_EXTRACTOR}), appGreen);
                spawnArmy(0.25, 0.25, 80, appGreen);
                break;
            case 10:
                setMoney(2000, 0);
                commonBase1();
                for (int i=0; i<30; i++) buildModule(0.95, 0.85, randomID(new int[]{ID_BARRACKS, ID_DEFENSE, ID_EXTRACTOR}), appRed);
                spawnArmy(0.15, 0.15, 200, appGreen);
                enableMessage();
                break;
            case 11:
                setMoney(1500, 0);
                commonBase1();
                for (int i=0; i<7; i++) buildModule(0.05, 0.85, ID_BARRACKS, appRed);
                for (int i=0; i<7; i++) buildModule(0.95, 0.15, ID_EXTRACTOR, appGreen);
                for (int i=0; i<8; i++) buildModule(0.05 + i * 0.11, 0.23, ID_EXTRACTOR, appGreen);
                for (int i=0; i<8; i++) buildModule(0.05 + i * 0.11, 0.31, ID_DEFENSE, appGreen);
                for (int i=0; i<8; i++) buildModule(0.05 + i * 0.11, 0.77, ID_BARRACKS, appRed);
                spawnArmy(0.5, 0.85, 200, appRed);
                enableMessage();
                break;
            case 12:
                setMoney(500, 0);
                obstacles3();
                commonBase1();
                for (int i=0; i<3; i++) buildModule(0.05, 0.15, ID_DEFENSE, appGreen);
                for (int i=0; i<17; i++) buildModule(0.95, 0.85, randomID(new int[]{ID_EXTRACTOR, ID_BARRACKS, ID_DEFENSE}), appRed);
                spawnArmy(0.15, 0.15, 200, appGreen);
                spawnArmy(0.85, 0.85, 100, appRed);
                break;
            case 13:
                setMoney(500, 200);
                commonBase2();
                for (int i=0; i<20; i++) buildModule(0.5, 0.15, randomID(new int[]{ID_BARRACKS, ID_DEFENSE, ID_EXTRACTOR}), appGreen);
                for (int i=0; i<20; i++) buildModule(0.5, 0.85, randomID(new int[]{ID_BARRACKS, ID_DEFENSE, ID_EXTRACTOR}), appRed);
                break;
            case 14:
                setMoney(0, 300);
                commonBase1();
                for (int i=0; i<25; i++) buildModule(0.05, 0.15, randomID(new int[]{ID_EXTRACTOR, ID_BARRACKS, ID_DEFENSE}), appGreen);
                spawnArmy(0.85, 0.85, 200, appRed);
                break;
            case 15:
                setMoney(200, 0);
                commonBase1();
                for (int i=0; i<20; i++) buildModule(0.05, 0.15, ID_LOOKOUT, appGreen);
                for (int i=0; i<20; i++) buildModule(0.95, 0.85, randomID(new int[]{ID_DEFENSE, ID_EXTRACTOR}), appRed);
                spawnArmy(0.2, 0.2, 50, appGreen);
                enableMessage();
                break;
            case 16:
                setMoney(500, 500);
                obstacles1();
                commonBase1();
                for (int i=0; i<20; i++) buildModule(0.05, 0.15, randomID(new int[]{ID_DEFENSE, ID_EXTRACTOR, ID_BARRACKS}), appGreen);
                for (int i=0; i<20; i++) buildModule(0.95, 0.85, randomID(new int[]{ID_DEFENSE, ID_EXTRACTOR, ID_BARRACKS}), appRed);
                spawnArmy(0.2, 0.2, 150, appGreen);
                spawnArmy(0.8, 0.8, 50, appRed);
                break;
            case 17:
                setMoney(500, 2000);
                commonBase1();
                for (int i=0; i<30; i++) buildModule(0.05, 0.15, randomID(new int[]{ID_DEFENSE, ID_EXTRACTOR, ID_BARRACKS}), appGreen);
                spawnArmy(0.8, 0.8, 150, appRed);
                break;
            case 18:
                setMoney(300, 0);
                obstacles2();
                commonBase1();
                for (int i=0; i<20; i++) buildModule(0.05, 0.15, ID_BARRACKS, appGreen);
                for (int i=0; i<20; i++) buildModule(0.95, 0.85, ID_BARRACKS, appRed);
                spawnArmy(0.5, 0.4, 150, appGreen);
                spawnArmy(0.5, 0.6, 150, appRed);
                break;
            case 19:
                setMoney(300, 0);
                commonBase1();
                for (int i=0; i<7; i++) buildModule(0.05, 0.15, ID_EXTRACTOR, appGreen);
                for (int i=0; i<8; i++) buildModule(0.05, 0.15, ID_BARRACKS, appGreen);
                for (int i=0; i<16; i++) buildModule(0.05, 0.15, ID_DEFENSE, appGreen);
                for (int i=0; i<20; i++) buildModule(0.95, 0.85, ID_BARRACKS, appRed);
                spawnArmy(0.5, 0.6, 150, appRed);
                break;
            case 20:
                setMoney(1000, 1000);
                obstacles4();
                commonBase1();
                spawnArmy(0.5, 0.3, 50, appGreen);
                spawnArmy(0.5, 0.7, 50, appRed);
                break;
            case 21:
                setMoney(900, 0);
                obstacles2();
                commonBase1();
                for (int i=0; i<5; i++) buildModule(0.05, 0.15, ID_EXTRACTOR, appGreen);
                for (int i=0; i<30; i++) buildModule(0.95, 0.85, ID_DEFENSE, appRed);
                spawnArmy(0.5, 0.3, 50, appGreen);
                break;
            case 22:
                setMoney(900, 900);
                obstacles5();
                commonBase1();
                for (int i=0; i<5; i++) buildModule(0.05, 0.15, ID_EXTRACTOR, appGreen);
                for (int i=0; i<5; i++) buildModule(0.95, 0.85, ID_EXTRACTOR, appRed);
                spawnArmy(0.3, 0.3, 100, appGreen);
                break;
            case 23:
                setMoney(500, 0);
                putObstacle(1, 0.1, 0.4);
                commonBase1();
                for (int i=0; i<10; i++) buildModule(0.05, 0.15, ID_EXTRACTOR, appGreen);
                for (int i=0; i<6; i++) buildModule(0.05, 0.15, ID_DEFENSE, appGreen);
                for (int i=0; i<20; i++) buildModule(0.95, 0.85, ID_BARRACKS, appRed);
                spawnArmy(0.8, 0.8, 75, appRed);
                break;
            case 24:
                setMoney(500, 200);
                obstacles6();
                commonBase1();
                for (int i=0; i<10; i++) buildModule(0.05, 0.15, ID_EXTRACTOR, appGreen);
                for (int i=0; i<8; i++) buildModule(0.95, 0.85, ID_EXTRACTOR, appRed);
                break;
            case 25:
                setMoney(700, 200);
                obstacles5();
                commonBase3();
                for (int i=0; i<20; i++) buildModule(0.05, 0.15, ID_BARRACKS, appGreen);
                for (int i=0; i<20; i++) buildModule(0.95, 0.85, ID_BARRACKS, appRed);
                for (int i=0; i<5; i++) buildModule(0.95, 0.85, ID_DEFENSE, appRed);
                spawnArmy(0.5, 0.3, 200, appGreen);
                spawnArmy(0.5, 0.7, 200, appRed);
                break;
            case 26:
                setMoney(300, 300);
                randomObstacles();
                commonBase1();
                for (int i=0; i<20; i++) buildModule(0.05, 0.15, randomID(new int[]{ID_BARRACKS, ID_EXTRACTOR, ID_DEFENSE}), appGreen);
                for (int i=0; i<20; i++) buildModule(0.95, 0.85, randomID(new int[]{ID_BARRACKS, ID_EXTRACTOR, ID_DEFENSE}), appRed);
                spawnArmy(0.8, 0.8, 100, appRed);
                break;
            case 27:
                setMoney(100, 300);
                obstacles3();
                commonBase1();
                for (int i=0; i<10; i++) buildModule(0.05, 0.15, ID_EXTRACTOR, appGreen);
                for (int i=0; i<10; i++) buildModule(0.05, 0.15, ID_LOOKOUT, appGreen);
                for (int i=0; i<20; i++) buildModule(0.95, 0.85, randomID(new int[]{ID_BARRACKS, ID_EXTRACTOR, ID_DEFENSE}), appRed);
                spawnArmy(0.8, 0.8, 100, appRed);
                break;
            case 28:
                setMoney(0, 400);
                randomObstacles();
                commonBase1();
                for (int i=0; i<5; i++) buildModule(0.05, 0.15, ID_EXTRACTOR, appGreen);
                for (int i=0; i<20; i++) buildModule(0.05, 0.15, ID_DEFENSE, appGreen);
                for (int i=0; i<20; i++) buildModule(0.95, 0.85, ID_EXTRACTOR, appRed);
                spawnArmy(0.8, 0.8, 100, appRed);
                break;
            case 29:
                setMoney(150, 500);
                obstacles7();
                commonBase3();
                for (int i=0; i<20; i++) buildModule(0.05, 0.15, randomID(new int[]{ID_BARRACKS, ID_EXTRACTOR, ID_DEFENSE}), appGreen);
                for (int i=0; i<20; i++) buildModule(0.95, 0.85, randomID(new int[]{ID_BARRACKS, ID_EXTRACTOR, ID_DEFENSE}), appRed);
                break;
            case 30:
                setMoney(150, 500);
                randomObstacles();
                commonBase3();
                for (int i=0; i<20; i++) buildModule(0.05, 0.15, ID_BARRACKS, appGreen);
                for (int i=0; i<20; i++) buildModule(0.95, 0.85, randomID(new int[]{ID_BARRACKS, ID_EXTRACTOR, ID_DEFENSE}), appRed);
                break;
            // 31 - 70 levels generated randomly
            case 71:
                setMoney(0, 0);
                randomObstacles();
                commonBase1();
                for (int i=0; i<20; i++) buildModule(0.05, 0.15, ID_BARRACKS, appGreen);
                for (int i=0; i<20; i++) buildModule(0.95, 0.85, ID_BARRACKS, appRed);
                break;
            case 72:
                setMoney(500, 200);
                obstacles5();
                commonBase2();
                for (int i=0; i<20; i++) buildModule(0.05, 0.15, ID_BARRACKS, appGreen);
                for (int i=0; i<20; i++) buildModule(0.95, 0.85, ID_BARRACKS, appRed);
                break;
            case 73:
                setMoney(200, 700);
                obstacles1();
                commonBase1();
                for (int i=0; i<10; i++) buildModule(0.05, 0.15, ID_EXTRACTOR, appGreen);
                for (int i=0; i<10; i++) buildModule(0.05, 0.15, ID_DEFENSE, appGreen);
                for (int i=0; i<20; i++) buildModule(0.95, 0.85, randomID(new int[]{ID_BARRACKS, ID_EXTRACTOR}), appRed);
                break;
            case 74:
                setMoney(300, 1000);
                obstacles7();
                commonBase1();
                for (int i=0; i<10; i++) buildModule(0.05, 0.15, ID_EXTRACTOR, appGreen);
                for (int i=0; i<7; i++) buildModule(0.05, 0.15, ID_DEFENSE, appGreen);
                for (int i=0; i<20; i++) buildModule(0.95, 0.85, randomID(new int[]{ID_BARRACKS, ID_EXTRACTOR}), appRed);
                break;
            case 75:
                setMoney(0, 0);
                obstacles2();
                commonBase1();
                for (int i=0; i<30; i++) buildModule(0.05, 0.15, ID_BARRACKS, appGreen);
                for (int i=0; i<30; i++) buildModule(0.95, 0.85, ID_BARRACKS, appRed);
                spawnArmy(0.5, 0.25, 150, appGreen);
                spawnArmy(0.5, 0.75, 150, appRed);
                break;
            case 76:
                setMoney(1500, 1500);
                obstacles5();
                commonBase1();
                break;
            case 77:
                setMoney(9000, 9000);
                obstacles3();
                commonBase3();
                spawnArmy(0.2, 0.3, 100, appGreen);
                spawnArmy(0.2, 0.8, 200, appRed);
                break;
            case 78:
                setMoney(0, 5000);
                commonBase1();
                for (int i=0; i<20; i++) buildModule(0.05, 0.15, ID_EXTRACTOR, appGreen);
                for (int i=0; i<20; i++) buildModule(0.05, 0.15, ID_DEFENSE, appGreen);
                spawnArmy(0.5, 0.25, 150, appGreen);
                break;
            case 79:
                setMoney(500, 1500);
                obstacles7();
                commonBase1();
                for (int i=0; i<30; i++) buildModule(0.05, 0.15, ID_EXTRACTOR, appGreen);
                for (int i=0; i<30; i++) buildModule(0.95, 0.85, ID_EXTRACTOR, appRed);
                break;
            case 80:
                setMoney(1000, 5000);
                commonBase1();
                for (int i=0; i<10; i++) buildModule(0.05, 0.15, ID_EXTRACTOR, appGreen);
                for (int i=0; i<20; i++) buildModule(0.95, 0.85, ID_BARRACKS, appRed);
                spawnArmy(0.3, 0.3, 50, appGreen);
                spawnArmy(0.8, 0.8, 150, appRed);
                break;
        }
    }

    void buildModule(double xFactor, double yFactor, int index, int app) {
        float x = (float)xFactor * w;
        float y = (float)yFactor * h;
        Graph graph = null;
        Module module = null;
        switch (app) {
            case GameController.APPURTENANCE_GREEN:
                graph = greenGraph;
                gameController.setGreenPlayerMoney(gameController.greenPlayerMoney + gameController.getModulePriceByIndex(index));
                break;
            case GameController.APPURTENANCE_RED:
                graph = redGraph;
                gameController.setRedPlayerMoney(gameController.redPlayerMoney + gameController.getModulePriceByIndex(index));
                break;

        }
        switch (index) {
            case ID_DEFENSE: module = new ModuleDefense(x, y, app, graph); break;
            case ID_BASE: module = new ModuleBase(x, y, app, graph); break;
            case ID_EXTRACTOR: module = new ModuleExtractor(x, y, app, graph); break;
            case ID_BARRACKS: module = new ModuleBarracks(x, y, app, graph); break;
            case ID_LOOKOUT: module = new ModuleLookout(x, y, app, graph); break;
        }
        buildModule(module);
    }

    void commonBase1() {
        buildModule(0.05, 0.15, ID_BASE, appGreen);
        buildModule(0.95, 0.85, ID_BASE, appRed);
    }

    void commonBase2() {
        buildModule(0.5, 0.15, ID_BASE, appGreen);
        buildModule(0.5, 0.85, ID_BASE, appRed);
    }

    void commonBase3() {
        buildModule(0.05, 0.15, ID_BASE, appGreen);
        buildModule(0.05, 0.85, ID_BASE, appRed);
    }

    void spawnArmy(double xFactor, double yFactor, int number, int app) {
        UnitGroup unitGroup = gameController.findUnitGroupByAppurtenance(app);
        unitGroup.setPosition(xFactor * w, yFactor * h);
        for (int i=0; i<number; i++) {
            spawnUnit((float)xFactor * w, (float)yFactor * h + 1, app); // y+1  -  this is dirty hack, so unit is not exactly in unit group center
        }
    }

    void putObstacle(double xFactor, double yFactor, double rFactor) {
        gameController.addObstacle(xFactor * w, yFactor * h, rFactor * w);
    }

    void obstacles1() {
        putObstacle(0.5, 0.5, 0.2);
    }

    void obstacles2() {
        putObstacle(0, 0.5, 0.2);
        putObstacle(1, 0.5, 0.2);
    }

    void obstacles3() {
        putObstacle(0, 0.5, 0.3);
        putObstacle(1, 0.4, 0.3);
    }

    void obstacles4() {
        putObstacle(0.3, 0.4, 0.1);
        putObstacle(0.7, 0.5, 0.1);
    }

    void obstacles5() {
        putObstacle(0, 0.5, 0.2);
        putObstacle(1, 0.5, 0.2);
        putObstacle(0.5, 0.5, 0.1);
    }

    void obstacles6() {
        putObstacle(0, 0.9, 0.5);
        putObstacle(1, 0.1, 0.5);
    }

    void obstacles7() {
        Bubble b[] = new Bubble[7];
        for (int i=0; i<b.length; i++) {
            b[i] = new Bubble();
            b[i].setPos(0.1f * Gdx.graphics.getWidth() + 0.8f * random.nextFloat() * Gdx.graphics.getWidth(), 0.2f * Gdx.graphics.getHeight() + 0.6f * random.nextFloat() * Gdx.graphics.getHeight());
        }
        double d;
        for (int k=0; k<10; k++) {
            for (int i=0; i<b.length; i++) {
                for (int j=0; j<i; j++) {
                    d = YioGdxGame.distance(b[i].x, b[i].y, b[j].x, b[j].y);
                    if (d < 0.2 * Gdx.graphics.getWidth()) {
                        double a = YioGdxGame.angle(b[i].x, b[i].y, b[j].x, b[j].y);
                        b[i].x -= 0.5 * d * Math.cos(a);
                        b[i].y -= 0.5 * d * Math.sin(a);
                        b[j].x += 0.5 * d * Math.cos(a);
                        b[j].y += 0.5 * d * Math.sin(a);
                    }
                }
            }
        }
        for (int i=0; i<b.length; i++) gameController.addObstacle(b[i].x, b[i].y, 0.05 * Gdx.graphics.getWidth());
    }

    void randomObstacles() {
        switch (random.nextInt(7)) {
            case 0: obstacles1(); break;
            case 1: obstacles2(); break;
            case 2: obstacles3(); break;
            case 3: obstacles4(); break;
            case 4: obstacles5(); break;
            case 5: /* nothing */ break;
            case 6: obstacles7(); break;
        }
    }

    void randomBaseBuild() {
        switch (random.nextInt(6)) {
            case 0:
                for (int i=0; i<10; i++) buildModule(0.5, 0.15, randomID(new int[]{ID_BARRACKS, ID_DEFENSE, ID_EXTRACTOR}), appGreen);
                for (int i=0; i<10; i++) buildModule(0.5, 0.85, randomID(new int[]{ID_BARRACKS, ID_DEFENSE, ID_EXTRACTOR}), appRed);
                break;
            case 1:
                for (int i=0; i<20; i++) buildModule(0.5, 0.15, randomID(new int[]{ID_BARRACKS, ID_BARRACKS, ID_EXTRACTOR}), appGreen);
                for (int i=0; i<15; i++) buildModule(0.5, 0.85, ID_DEFENSE, appRed);
                break;
            case 2:
                for (int i=0; i<15; i++) buildModule(0.5, 0.15, randomID(new int[]{ID_BARRACKS, ID_EXTRACTOR}), appGreen);
                for (int i=0; i<15; i++) buildModule(0.5, 0.85, randomID(new int[]{ID_BARRACKS, ID_DEFENSE, ID_EXTRACTOR}), appRed);
                break;
            case 3:
                for (int i=0; i<15; i++) buildModule(0.5, 0.15, ID_EXTRACTOR, appGreen);
                for (int i=0; i<10; i++) buildModule(0.5, 0.85, ID_BARRACKS, appRed);
                break;
            case 4:
                for (int i=0; i<20; i++) buildModule(0.5, 0.15, ID_BARRACKS, appGreen);
                for (int i=0; i<20; i++) buildModule(0.5, 0.85, ID_BARRACKS, appRed);
                break;
            case 5:
                for (int i=0; i<20; i++) buildModule(0.5, 0.15, randomID(new int[]{ID_BARRACKS, ID_EXTRACTOR}), appGreen);
                for (int i=0; i<15; i++) buildModule(0.5, 0.85, randomID(new int[]{ID_DEFENSE, ID_EXTRACTOR}), appRed);
                break;
        }
    }

    void randomArmies() {
        int army = 15 + random.nextInt(50);
        spawnArmy(0.2 + 0.6 * random.nextDouble(), 0.25, army, appGreen);
        spawnArmy(0.2 + 0.6 * random.nextDouble(), 0.75, army, appRed);
    }

    void randomLevel() {
        int money = 50 * random.nextInt(21);
        setMoney(money, money);
        randomObstacles();
        commonBase1();
        randomBaseBuild();
        randomArmies();
        if (random.nextDouble() > 0.6) buildModule(0.05, 0.15, ID_LOOKOUT, appGreen);
    }

    int randomID(int v[]) {
        return v[random.nextInt(v.length)];
    }

    void setGreenPlayerMoney(int money) {
        gameController.setGreenPlayerMoney(money);
    }

    void setRedPlayerMoney(int money) {
        gameController.setRedPlayerMoney(money);
    }

    void setMoney(int greenMoney, int redMoney) {
        setGreenPlayerMoney(greenMoney);
        setRedPlayerMoney(redMoney);
    }

    void spawnUnit(float x, float y, int appurtenance) {
        gameController.spawnUnit(x, y, appurtenance);
    }

    void addUnitGroup(UnitGroup unitGroup) {
        gameController.addUnitGroup(unitGroup);
    }

    void buildModule(Module module) {
        gameController.buildModule(module);
    }

    void enableMessage() {
        gameController.levelHasSpecialMessage();
    }
}
