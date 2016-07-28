package yio.tro.achipato;

import android.util.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by ivan on 05.08.14.
 */
public class GameController {

    YioGdxGame yioGdxGame;
    public static final int APPURTENANCE_GREEN = 0;
    public static final int APPURTENANCE_RED = 1;
    Graph greenGraph, redGraph;
    int dragType;
    public static final int DRAG_TYPE_NONE = 0;
    public static final int DRAG_TYPE_EXTRACTOR = 1;
    public static final int DRAG_TYPE_BARRACKS = 2;
    public static final int DRAG_TYPE_LOOKOUT = 3;
    public static final int DRAG_TYPE_BASE = 4;
    public static final int DRAG_TYPE_DEFENSE = 5;
    public static final int PRICE_BASE = 5;
    public static final int PRICE_LOOKOUT = 15;
    public static final int PRICE_DEFENSE = 45;
    public static final int PRICE_BARRACKS = 40;
    public static final int PRICE_EXTRACTOR = 35;
    public static final int LASERS_FROM_ONE_MODULE_SIMULTANEOUSLY = 3;
    public static final int NUMBER_OF_BUBBLES = 200;
    ArrayList<UnitGroup> groupList;
    ArrayList<Light> commandsToGo;
    ArrayList<AnimPoint> dollarAnims;
    ArrayList<Unit> cacheMatrixUnits[][];
    ArrayList<Light> lights;
    ArrayList<Unit> queueToDeath;
    ArrayList<ModuleDefense> defenseModuleList;
    ArrayList<Bubble> obstacles;
    ArrayList<Bubble> obstacleCache[][];
    double angles[][];
    int bandHeight, leftSide;
    public int screenX, screenY;
    int w, h;
    int currentBubbleIndex;
    long currentTime, lastTimeMoneySpawnedNaturally, lastTimeAiAnalyzed, lastTimeAiUnitControlled;
    long lastTimeCollisionCheck;
    long timeToUnPressBinButton;
    int maxTouchCount, currentTouchCount, lastTouchCount;
    int maxNumberOfUnitsForOnePlayer;
    int greenPlayerMoney, naturalMoneySpawnDelay, aiAnalyzeDelay, aiUnitControlDelay, redPlayerMoney;
    int collisionCheckDelay;
    TextureRegion dragModuleTexture;
    boolean isBinButtonPressed;
    Bubble bubbles[];
    Random random, predictableRandom;
    float defaultBubbleRadius;
    ArtificialIntelligence artificialIntelligence;
    public static final int MAX_MODULE_NUMBER_FOR_SINGLE_PLAYER = 100;
    float cacheUnitsCellSize;
    Beam beams[];
    int currentBeamIndex;
    Sound soundLaser, soundExplosion, soundHurt, soundBuild, soundHitModule;
    boolean muteBuildSound;
    WaypointGraph waypointGraph;
    LanguagesManager languagesManager;
    boolean tutorial, readyToCreateTutorialTip, playerPressedSomewhere, playerBuiltSomething, playerCancelledSomething;
    int tutorialStepIndex, progress; // progress - index of unlocked level
    long timeToCreateTutorialTip, timeToDisplaySpecialLevelMessage;
    String key;
    LevelCreator levelCreator;
    boolean levelHasMessage;

    public GameController(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        random = new Random();
        predictableRandom = new Random(0);
        greenGraph = new Graph(this, APPURTENANCE_GREEN);
        redGraph = new Graph(this, APPURTENANCE_RED);
        defenseModuleList = new ArrayList<ModuleDefense>();
        bandHeight = (int)(0.1 * Gdx.graphics.getHeight());
        leftSide = (int)(0.5 * (Gdx.graphics.getWidth() - 5 * bandHeight));
        bubbles = new Bubble[NUMBER_OF_BUBBLES];
        for (int i=0; i<NUMBER_OF_BUBBLES; i++) bubbles[i] = new Bubble();
        currentBubbleIndex = 0;
        defaultBubbleRadius = 0.02f * Gdx.graphics.getWidth();
        groupList = new ArrayList<UnitGroup>();
        commandsToGo = new ArrayList<Light>();
        obstacles = new ArrayList<Bubble>();
        maxNumberOfUnitsForOnePlayer = 200;
        naturalMoneySpawnDelay = 1000;
        collisionCheckDelay = 200;
        dollarAnims = new ArrayList<AnimPoint>();
        cacheUnitsCellSize = 0.5f * GameView.moduleSize;
        YioGdxGame.say("cache units cell size = " + cacheUnitsCellSize);
        cacheMatrixUnits = new ArrayList[(int)(Gdx.graphics.getWidth() / cacheUnitsCellSize) + 1][(int)(Gdx.graphics.getHeight() / cacheUnitsCellSize) + 1];
        obstacleCache = new ArrayList[cacheMatrixUnits.length][cacheMatrixUnits[0].length];
        angles = new double[cacheMatrixUnits.length][cacheMatrixUnits[0].length];
        clearObstaclesAndCache();
        lights = new ArrayList<Light>();
        queueToDeath = new ArrayList<Unit>();
        beams = new Beam[30];
        for (int i=0; i<beams.length; i++) beams[i] = new Beam();
        loadSounds();
        waypointGraph = new WaypointGraph();
        languagesManager = yioGdxGame.menuControllerLighty.languagesManager;
        progress = yioGdxGame.selectedLevelIndex;
        levelCreator = new LevelCreator(yioGdxGame, this, yioGdxGame.menuControllerLighty); // this must be called after graphs created
    }

    void loadSounds() {
        muteBuildSound = false;
        soundLaser = Gdx.audio.newSound(Gdx.files.internal("sound/laser.ogg"));
        soundExplosion = Gdx.audio.newSound(Gdx.files.internal("sound/explosion.ogg"));
        soundHurt = Gdx.audio.newSound(Gdx.files.internal("sound/hurt.ogg"));
        soundBuild = Gdx.audio.newSound(Gdx.files.internal("sound/build.ogg"));
        soundHitModule = Gdx.audio.newSound(Gdx.files.internal("sound/hit_module.ogg"));
    }

    void clearCacheUnits() {
        for (int i=0; i < cacheMatrixUnits.length; i++) {
            for (int j=0; j< cacheMatrixUnits[i].length; j++) {
                cacheMatrixUnits[i][j] = new ArrayList<Unit>();
            }
        }
    }

    void clearObstaclesAndCache() {
        // clearing cache and angles
        for (int i=0; i < obstacleCache.length; i++) {
            for (int j=0; j< obstacleCache[i].length; j++) {
                obstacleCache[i][j] = new ArrayList<Bubble>();
                angles[i][j] = -1;
            }
        }
        // clearing array list
        ListIterator iterator = obstacles.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    Beam getNextAvailableBeam() { // can return null
        Beam beam = null;
        int count = 0;
        while (count < beams.length-1) {
            beam = beams[currentBeamIndex];
            currentBeamIndex++;
            if (currentBeamIndex > beams.length-1) currentBeamIndex = 0;
            if (!beam.isVisible()) break;
            count++;
        }
        return beam;
    }

    public void move() {
        Light light;
        for (int i=lights.size()-1; i>=0; i--) {
            light = lights.get(i);
            light.move();
            if (light.factorOfLighting.factor() < 0.1) lights.remove(light);
        }
        for (Bubble bubble : bubbles) {
            if (bubble.isVisible()) bubble.move();
        }
        Light command;
        for (int i=commandsToGo.size()-1; i>=0; i--) {
            command = commandsToGo.get(i);
            command.move();
            if (command.factorOfLighting.factor() < 0.1) removeCommandToGo(command);
        }
        AnimPoint point;
        for (int i=dollarAnims.size()-1; i>=0; i--) {
            point = dollarAnims.get(i);
            point.factorModelLighty.move();
            if (point.factorModelLighty.factor() < 0.1) {
                dollarAnims.remove(i);
            }
        }
        for (int i=0; i<beams.length; i++) beams[i].move();

        greenGraph.move();
        redGraph.move();
        for (UnitGroup unitGroup : groupList) unitGroup.move();
        checkForDeadUnits();
        currentTime = System.currentTimeMillis();
        if (currentTime > lastTimeCollisionCheck + collisionCheckDelay) {
            lastTimeCollisionCheck = currentTime;
            checkForUnitsCollisions(APPURTENANCE_GREEN, APPURTENANCE_RED);
            checkForUnitCollisionsWithGraph(APPURTENANCE_GREEN, redGraph);
            checkForUnitCollisionsWithGraph(APPURTENANCE_RED, greenGraph);
            // check for defense modules
            checkForDefenseCollisions();
        }
        if (isBinButtonPressed && currentTime > timeToUnPressBinButton) isBinButtonPressed = false;
        if (currentTime > lastTimeMoneySpawnedNaturally + naturalMoneySpawnDelay) {
            lastTimeMoneySpawnedNaturally = currentTime;
            greenPlayerMoney++;
            redPlayerMoney++;
        }
        if (currentTime > lastTimeAiAnalyzed + aiAnalyzeDelay) {
            lastTimeAiAnalyzed = currentTime;
            artificialIntelligence.analyzeAndOperate();
            if (currentTouchCount > 0 && !Gdx.input.isTouched()) {
                YioGdxGame.say("current touch count wasn't zero");
                currentTouchCount = 0;
                // небольшой костыль, связанный с мультитачем
            }
        }
        if (currentTime > lastTimeAiUnitControlled + aiUnitControlDelay) {
            lastTimeAiUnitControlled = currentTime;
            artificialIntelligence.unitControl();
        }
        checkForGameEnd();
        if (tutorial) checkForTutorialTips();
        if (levelHasMessage && currentTime > timeToDisplaySpecialLevelMessage) {
            levelHasMessage = false;
            yioGdxGame.menuControllerLighty.createTutorialTip(YioGdxGame.decodeStringToArrayList(languagesManager.getString("level_" + yioGdxGame.selectedLevelIndex), "/"));
        }
    }

    void checkForTutorialTips() {
        if (readyToCreateTutorialTip && System.currentTimeMillis() > timeToCreateTutorialTip) {
            yioGdxGame.menuControllerLighty.createTutorialTip(YioGdxGame.decodeStringToArrayList(languagesManager.getString(key), "/"));
            tutorialStepIndex++;
            readyToCreateTutorialTip = false;
        }
        if (!readyToCreateTutorialTip) {
            switch (tutorialStepIndex) {
                case 0:
                    prepareTutorialTip("tutorial_hello");
                    break;
                case 1:
                    prepareTutorialTip("tutorial_press_somewhere");
                    break;
                case 2:
                    if (playerPressedSomewhere) prepareTutorialTip("tutorial_build_something");
                    break;
                case 3:
                    if (playerBuiltSomething) prepareTutorialTip("tutorial_about_modules");
                    break;
                case 4:
                    prepareTutorialTip("tutorial_about_building_modules");
                    break;
                case 5:
                    prepareTutorialTip("tutorial_about_canceling");
                    break;
                case 6:
                    if (playerCancelledSomething) prepareTutorialTip("tutorial_destroy_your_enemy");
                    break;
            }
        }
    }

    void prepareTutorialTip(String key) {
        this.key = key;
        readyToCreateTutorialTip = true;
        timeToCreateTutorialTip = System.currentTimeMillis() + 1000;
        if (tutorialStepIndex == 0) timeToCreateTutorialTip = System.currentTimeMillis();
    }

    void checkForGameEnd() {
        if (redGraph.modules.size() == 0) playerWon();
        if (greenGraph.modules.size() == 0) computerWon();
    }

    void playerWon() {
        yioGdxGame.setGamePaused(true);
        int ls = yioGdxGame.selectedLevelIndex;
        yioGdxGame.increaseLevelSelection();
        if (yioGdxGame.selectedLevelIndex >= progress) {
            progress = yioGdxGame.selectedLevelIndex;
            if (ls == progress) progress++; // last level completed
            Preferences preferences = Gdx.app.getPreferences("main");
            preferences.putInteger("progress", progress);
            preferences.flush();
            yioGdxGame.menuControllerLighty.updateScrollerLineTexture(progress - 1);
            yioGdxGame.menuControllerLighty.updateScrollerLineTexture(progress);
        }
        yioGdxGame.menuControllerLighty.createAfterGameMenu(true);
        yioGdxGame.setFireworkEnabled(true);
    }

    void computerWon() {
        yioGdxGame.setGamePaused(true);
        yioGdxGame.menuControllerLighty.createAfterGameMenu(false);
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    void checkForDefenseCollisions() {
        int mx, my, mRadius = 6;
        ModuleDefense module;
        Unit unit;
        int shootCount;
        for (int i=defenseModuleList.size()-1; i>=0; i--) {
            module = defenseModuleList.get(i);
            if (!module.isConstructing && currentTime > module.lastTimeAttacked + module.attackDelay) {
                shootCount = 0;
                mx = (int)(module.visualX / cacheUnitsCellSize);
                my = (int)(module.visualY / cacheUnitsCellSize);
                for (int x = Math.max(0, mx - mRadius); x < Math.min(cacheMatrixUnits.length-1, mx + mRadius + 1) && shootCount < LASERS_FROM_ONE_MODULE_SIMULTANEOUSLY; x++) {
                    for (int y = Math.max(0, my - mRadius); y < Math.min(cacheMatrixUnits[0].length-1, my + mRadius + 1) && shootCount < LASERS_FROM_ONE_MODULE_SIMULTANEOUSLY; y++) {
                        if (cacheMatrixUnits[x][y].size() > 0) {
                            unit = cacheMatrixUnits[x][y].get(0);
                            if (YioGdxGame.distance(unit.x, unit.y, module.x, module.y) < 3 * GameView.moduleSize && unit.group.appurtenance != module.appurtenance) {
                                module.lastTimeAttacked = currentTime;
                                yioGdxGame.gameView.createSplat(unit.visualX, unit.visualY);
                                Beam beam = getNextAvailableBeam();
                                if (beam != null) {
                                    beam.set((float)module.visualX, (float)module.visualY, unit.visualX, unit.visualY);
                                    YioGdxGame.playSound(soundLaser);
                                }
                                destroyUnit(unit);
                                shootCount++;
                            }
                        }
                    }
                }

                //now collisions with other modules
                mRadius = 3;
                mx = (int)(module.x / greenGraph.cacheCellSize);
                my = (int)(module.y / greenGraph.cacheCellSize);
                Graph graph = getEnemyGraph(module.appurtenance);
                Module m;
                for (int x = Math.max(0, mx - mRadius); x < Math.min(graph.cacheMatrixModules.length-1, mx + mRadius) && shootCount < LASERS_FROM_ONE_MODULE_SIMULTANEOUSLY; x++) {
                    for (int y = Math.max(0, my - mRadius); y < Math.min(graph.cacheMatrixModules[0].length-1, my + mRadius) && shootCount < LASERS_FROM_ONE_MODULE_SIMULTANEOUSLY; y++) {
                        m = graph.cacheMatrixModules[x][y];
                        if (m != null && m.alive && YioGdxGame.distance(mx, my, x, y) < mRadius && m.inPosition()) {
                            if (m.appurtenance != module.appurtenance) {
                                module.lastTimeAttacked = currentTime;
                                moduleExplosion(m, 0.02f * yioGdxGame.gameView.moduleSize, 5);
                                Beam beam = getNextAvailableBeam();
                                if (beam != null) {
                                    beam.set((float)module.visualX, (float)module.visualY, (float)m.visualX, (float)m.visualY);
                                    YioGdxGame.playSound(soundLaser);
                                }
                                attackModule(m);
                                shootCount++;
                            }
                        }
                    }
                }
            } else continue; //go to next defense module
        }
    }

    Graph getEnemyGraph(int appurtenance) {
        if (appurtenance == APPURTENANCE_GREEN) return redGraph;
        else if (appurtenance == APPURTENANCE_RED) return greenGraph;
        return redGraph;
    }

    void checkForUnitsCollisions(int app1, int app2) { // app - appurtenance
        Unit firstUnit, secondUnit, currentUnit;
        for (int i=0; i < cacheMatrixUnits.length; i++) {
            for (int j=0; j< cacheMatrixUnits[i].length; j++) {
                firstUnit = null;
                secondUnit = null;
                for (int k=cacheMatrixUnits[i][j].size()-1; k>=0; k--) {
                    currentUnit = cacheMatrixUnits[i][j].get(k);
                    if (!currentUnit.alive) continue;
                    if (firstUnit == null && currentUnit.group.appurtenance == app1) firstUnit = currentUnit;
                    if (secondUnit == null && currentUnit.group.appurtenance == app2) secondUnit = currentUnit;
                }
                if (firstUnit != null && secondUnit != null) {
                    killTwoUnits(firstUnit, secondUnit);
                }
            }
        }
    }

    void killTwoUnits(Unit firstUnit, Unit secondUnit) {
        float cx, cy;
        cx = 0.5f * (firstUnit.x + secondUnit.x);
        cy = 0.5f * (firstUnit.y + secondUnit.y);
        yioGdxGame.gameView.createSplat(cx, cy);
        destroyUnit(firstUnit);
        destroyUnit(secondUnit);
        YioGdxGame.playSound(soundHurt);
    }

    void checkForDeadUnits() {
        Unit unit;
        for (int i=queueToDeath.size()-1; i>=0; i--) {
            unit = queueToDeath.get(i);
            if (!unit.inMotion) destroyUnit(unit);
        }
    }

    void checkForUnitCollisionsWithGraph(int unitAppurtenance, Graph graph) {
        if (graph.appurtenance == unitAppurtenance) {
            Log.e("yiotro", "UNIT COLLISION WITH MODULE FAIL : SAME APPURTENANCE");
            return;
        }
        int x, y, radius;
        radius = (int)(GameView.moduleSize / cacheUnitsCellSize) + 1;
        Unit attackingUnit;
        for (Module module : graph.modules) {
            x = (int)(module.visualX / cacheUnitsCellSize);
            y = (int)(module.visualY / cacheUnitsCellSize);
            for (int i=Math.max(0, x - radius); i<=Math.min(cacheMatrixUnits.length-1, x + radius); i++) {
                for (int j=Math.max(0, y - radius); j<=Math.min(cacheMatrixUnits[0].length-1, y + radius); j++) {
                    for (int k=cacheMatrixUnits[i][j].size()-1; k>=0; k--) {
                        attackingUnit = cacheMatrixUnits[i][j].get(k);
                        if (attackingUnit.group.appurtenance != unitAppurtenance) continue;
                        if (module.hp > 0 && YioGdxGame.distance(attackingUnit.visualX, attackingUnit.visualY, module.visualX, module.visualY) < 0.5f * GameView.moduleSize + yioGdxGame.gameView.unitRadius) {
                            yioGdxGame.gameView.createSplat(attackingUnit.visualX, attackingUnit.visualY);
                            Light light = new Light(attackingUnit.visualX, attackingUnit.visualY, yioGdxGame.gameView.unitVisibilityRadius);
                            light.factorOfLighting.factor = 1;
                            light.destroyIllumination();
                            lights.add(light);
                            cacheMatrixUnits[i][j].remove(attackingUnit);
                            YioGdxGame.playSound(soundHitModule);
                            destroyUnit(attackingUnit);
                            attackModule(module);
                        }
                    }
                }
            }
        }
    }

    void attackModule(Module module) {
        module.hp -= 1;
        if (module.isConstructing) module.hp -= 2;
    }

    void destroyUnit(Unit unit) {
        UnitGroup group = unit.group;
        if (group.appurtenance == APPURTENANCE_GREEN) {
            Light light = new Light(unit.visualX, unit.visualY, yioGdxGame.gameView.unitVisibilityRadius);
            light.factorOfLighting.factor = 1;
            light.destroyIllumination();
            lights.add(light);
        }
        cacheMatrixUnits[(int)(unit.visualX / cacheUnitsCellSize)][(int)(unit.visualY / cacheUnitsCellSize)].remove(unit);
        group.unitsInGroup.remove(unit);
        group.updateRadius();
    }

    int getCurrentNumberOfUnits(int appurtenance) {
        int count = 0;
        for (UnitGroup group : groupList)
            if (group.appurtenance == appurtenance)
                count += group.unitsInGroup.size();
        return count;
    }

    void bubbleExplosion(float x, float y, float radius, float power, int howMany) {
        float a, p, r;
        for (int i=0; i<howMany; i++) {
            Bubble bubble = getAvailableBubble();
            a = 2f * (float)Math.PI * random.nextFloat();
            p = random.nextFloat() * power;
            r = random.nextFloat() * radius;
            bubble.setSpeed(p * (float)Math.cos(a), p * (float)Math.sin(a));
            bubble.setRadius(defaultBubbleRadius, -0.02f * defaultBubbleRadius);
            bubble.setPos(x + r * (float)Math.cos(a), y + r * (float)Math.sin(a));
            bubble.setType(0);
        }
    }

    void moduleExplosion(Module module, float power, int howMany) {
        float a, p;
        for (int i=0; i<howMany; i++) {
            Bubble bubble = getAvailableBubble();
            a = 2f * (float)Math.PI * random.nextFloat();
            p = random.nextFloat() * power;
            bubble.setPos((float)module.visualX + 0.25f * yioGdxGame.gameView.moduleSize * (float)Math.cos(a), (float)module.visualY + 0.25f * yioGdxGame.gameView.moduleSize * (float)Math.sin(a));
            bubble.setSpeed(p * (float)Math.cos(a), p * (float)Math.sin(a));
            bubble.setRadius(defaultBubbleRadius, -0.02f * defaultBubbleRadius);
            bubble.setType(0);
        }
    }

    Bubble getAvailableBubble() {
        Bubble bubble = bubbles[currentBubbleIndex];
        currentBubbleIndex++;
        if (currentBubbleIndex >= NUMBER_OF_BUBBLES) currentBubbleIndex = 0;
        if (bubble.isVisible()) return bubbles[0];
        return bubble;
    }

    void prepareForNewGame(int index) {
        predictableRandom = new Random(index);
        yioGdxGame.gameView.createCurrentBackgroundTexture(index);
        yioGdxGame.gameView.loadEnemyTextures(index);
        yioGdxGame.beginBackgroundChange(4);
        yioGdxGame.setFireworkEnabled(false);
        greenGraph.clear();
        redGraph.clear();
        defenseModuleList.clear();
        clearAllUnits();
        clearCacheUnits();
        clearObstaclesAndCache();
        addUnitGroup(new UnitGroup(0.9 * w, 0.85 * h, APPURTENANCE_RED, this));
        addUnitGroup(new UnitGroup(0.5 * w, 0.25 * h, APPURTENANCE_GREEN, this));
        reformUnitsGroups(1, APPURTENANCE_RED);
        waypointGraph.activateAllPoints();
        muteBuildSound = true;
        levelCreator.createLevel(index);
        muteBuildSound = false;
        artificialIntelligence = new AiStrategyUnits(this, APPURTENANCE_RED, APPURTENANCE_GREEN);
        aiAnalyzeDelay = 500;
        aiUnitControlDelay = 100;
        artificialIntelligence.preparationsForNewGame();
        maxTouchCount = 0;
        currentTouchCount = 0;
        playerPressedSomewhere = false;
        playerBuiltSomething = false;
        playerCancelledSomething = false;
        yioGdxGame.gameView.lightUpAllLevel();
    }

    void addUnitGroup(UnitGroup unitGroup) {
        groupList.add(unitGroup);
    }

    void addObstacle(double obsX, double obsY, double obsR) {
        int mx = (int)(obsX / cacheUnitsCellSize);
        int my = (int)(obsY / cacheUnitsCellSize);
        int mr = (int)(obsR / cacheUnitsCellSize);
        Bubble obstacle = new Bubble();
        obstacle.setPos((float)obsX, (float)obsY);
        obstacle.setRadius((float)obsR, 0);
        ListIterator iterator = obstacles.listIterator();
        iterator.add(obstacle);
        for (int x = Math.max(0, mx - mr - 1); x < Math.min(obstacleCache.length-1, mx + mr + 1); x++) {
            for (int y = Math.max(0, my - mr - 1); y < Math.min(obstacleCache[0].length - 1, my + mr + 1); y++) {
                if (YioGdxGame.distance(x, y, mx, my) <= mr + 1) {
                    obstacleCache[x][y].add(obstacle);
                    angles[x][y] = YioGdxGame.angle(mx, my, x, y);
                }
            }
        }
        waypointGraph.deactivateSomePointsByObstacle(obstacle);
    }

    public void setRedPlayerMoney(int redPlayerMoney) {
        this.redPlayerMoney = redPlayerMoney;
    }

    public void setGreenPlayerMoney(int greenPlayerMoney) {
        this.greenPlayerMoney = greenPlayerMoney;
    }

    void activateAllWayPoints() {
        for (int i=waypointGraph.getPoints().size()-1; i>=0; i--)
            waypointGraph.getPoints().get(i).activate();
    }

    void clearAllUnits() {
        for (UnitGroup group : groupList) group.clear();
        ListIterator iterator = groupList.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    int getModulePriceByIndex(int moduleIndex) {
        switch (moduleIndex) {
            case Module.MODULE_INDEX_BARRACKS: return PRICE_BARRACKS;
            case Module.MODULE_INDEX_BASE: return PRICE_BASE;
            case Module.MODULE_INDEX_DEFENSE: return PRICE_DEFENSE;
            case Module.MODULE_INDEX_EXTRACTOR: return PRICE_EXTRACTOR;
            case Module.MODULE_INDEX_LOOKOUT: return PRICE_LOOKOUT;
            default: return 1;
        }
    }

    boolean canBuildModule(int moduleIndex, int appurtenance) {
        switch (appurtenance) {
            case APPURTENANCE_GREEN: return greenPlayerMoney >= getModulePriceByIndex(moduleIndex);
            case APPURTENANCE_RED: return redPlayerMoney >= getModulePriceByIndex(moduleIndex);
            default: return false;
        }
    }

    boolean buildModule(Module module) {
        switch (module.appurtenance) {
            case APPURTENANCE_GREEN:
                if (greenGraph.modules.size() >= MAX_MODULE_NUMBER_FOR_SINGLE_PLAYER) return false;
                if (greenPlayerMoney >= module.getPrice()) {
                    greenPlayerMoney -= module.getPrice();
                } else {
                    bubbleExplosion((float)module.x, (float)module.y, 0.5f * GameView.moduleSize, 0.02f * GameView.moduleSize, 25);
                    return false;
                }
                if (!greenGraph.findBetterPlaceForModule(module)) {
                    greenPlayerMoney += module.getPrice();
                    bubbleExplosion((float)module.x, (float)module.y, 0.5f * GameView.moduleSize, 0.02f * GameView.moduleSize, 25);
                    return false;
                }
                module.checkIfConnectedToSide();
                greenGraph.addModule(module);
                if (!muteBuildSound) YioGdxGame.playSound(soundBuild);
                if (artificialIntelligence != null) artificialIntelligence.alertAboutNewEnemyModule(module);
                waypointGraph.deactivateSomePointsByModule(module);
                waypointGraph.deactivateUnreachablePoints();
                if (tutorial && tutorialStepIndex == 3) playerBuiltSomething = true;
                break;
            case APPURTENANCE_RED:
                if (redGraph.modules.size() >= MAX_MODULE_NUMBER_FOR_SINGLE_PLAYER) return false;
                if (redPlayerMoney >= module.getPrice()) {
                    redPlayerMoney -= module.getPrice();
                } else return false;
                if (!redGraph.findBetterPlaceForModule(module)) {
                    redPlayerMoney += module.getPrice();
                    bubbleExplosion((float)module.x, (float)module.y, 0.5f * GameView.moduleSize, 0.02f * GameView.moduleSize, 25);
                    return false;
                }
                module.checkIfConnectedToSide();
                redGraph.addModule(module);
                break;
        }
        if (module instanceof ModuleDefense) defenseModuleList.add((ModuleDefense)module);
        return true;
    }

    void reportAboutModuleConstructionEnd(Module module) {
        float power = 0.02f * yioGdxGame.gameView.moduleSize;
        if (module instanceof ModuleLookout) power *= 3;
        moduleExplosion(module, power, 25);
    }

    void spawnUnit(float x, float y, int appurtenance) {
        if (getCurrentNumberOfUnits(appurtenance) >= maxNumberOfUnitsForOnePlayer) return;
        Unit unit = new Unit(x, y, this);
        UnitGroup group = getClosestUnitGroup(x, y, appurtenance, false);
        if (group != null) {
            group.addUnit(unit);
            group.sendUnitToDestination(unit);
        }
    }

    void spawnMoneyByExtractor(ModuleExtractor extractor) {
        switch (extractor.appurtenance) {
            case APPURTENANCE_GREEN:
                greenPlayerMoney++;
                break;
            case APPURTENANCE_RED:
                redPlayerMoney++;
                break;
        }
        AnimPoint dollar = new AnimPoint((float)extractor.x, (float)extractor.y, extractor.appurtenance);
        dollar.factorModelLighty.factor = 1;
        dollar.factorModelLighty.beginSlowDestroyProcess();
        dollarAnims.add(dollar);
    }

    UnitGroup findUnitGroupByAppurtenance(int appurtenance) {
        for (UnitGroup group : groupList)
            if (group.appurtenance == appurtenance) return group;
        return null;
    }

    void addCommandToGo(float x, float y) {
        Light command = new Light(x, y, 0.04f * Gdx.graphics.getWidth());
        command.factorOfLighting.factor = 1;
        command.factorOfLighting.beginDestroyProcess();
        ListIterator iterator = commandsToGo.listIterator();
        iterator.add(command);
    }

    void removeCommandToGo(Light command) {
        ListIterator iterator = commandsToGo.listIterator();
        Light temp;
        while (iterator.hasNext()) {
            temp = (Light)iterator.next();
            if (temp == command) {
                iterator.remove();
                return;
            }
        }
    }

    void removeUnitGroup(UnitGroup group) {
        ListIterator iterator = groupList.listIterator();
        UnitGroup temp;
        while (iterator.hasNext()) {
            temp = (UnitGroup)iterator.next();
            if (temp == group) {
                iterator.remove();
                return;
            }
        }
    }

    int howManyUnitsGroupsWithThisAppurtenance(int appurtenance) {
        int count = 0;
        for (UnitGroup group : groupList)
            if (group.appurtenance == appurtenance) count++;
        return count;
    }

    int howManyUnitsWithThisAppurtenance(int appurtenance) {
        int count = 0;
        for (UnitGroup group : groupList)
            if (group.appurtenance == appurtenance) count += group.unitsInGroup.size();
        return count;
    }

    void reformUnitsGroups(int howMany, int appurtenance) {
        if (howMany <= 0) return;
        if (howManyUnitsGroupsWithThisAppurtenance(appurtenance) == howMany) return;
        double x = 0, y = 0;
        // all units in one array list
        ArrayList<Unit> tempListOfUnits = new ArrayList<Unit>();
        UnitGroup group;
        for (int i=groupList.size()-1; i>=0; i--) {
            group = groupList.get(i);
            if (group.appurtenance == appurtenance) {
                for (Unit unit : group.unitsInGroup) tempListOfUnits.add(unit);
                x = group.x;
                y = group.y;
                removeUnitGroup(group);
            }
        }

        //creating new groups at the start of group list
        ListIterator iterator = groupList.listIterator();
        for (int i=0; i<howMany; i++) {
            iterator.add(new UnitGroup(x, y, appurtenance, this));
        }

        //splitting units into this new groups
        for (int i=tempListOfUnits.size()-1; i>=0; i--) {
            groupList.get(random.nextInt(howMany)).addUnit(tempListOfUnits.get(i));
        }

        tempListOfUnits.clear();
    }

    UnitGroup getClosestUnitGroup(double x, double y, int appurtenance, boolean ignoreGroupsWithAssignedPath) {
        UnitGroup closestGroup = null;
        double minDistance = 2 * Gdx.graphics.getHeight(), currentDistance;
        for (UnitGroup group : groupList) {
            if (group.appurtenance != appurtenance) continue;
            if (ignoreGroupsWithAssignedPath && group.assignedPath) continue;
            currentDistance = YioGdxGame.distance(x, y, group.x, group.y);
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                closestGroup = group;
            }
        }
        return closestGroup;
    }

    void demolishLastBuiltModule(int appurtenance) {
        Graph graph = getGraphByAppurtenance(appurtenance);
        if (graph.modules.size() == 0) return;
        Module module = graph.modules.get(0);
        int money;
        if (module.isConstructing) money = getModulePriceByIndex(module.index);
        else money = (int)(0.5 * getModulePriceByIndex(module.index));
        if (appurtenance == APPURTENANCE_GREEN) greenPlayerMoney += money;
        else if (appurtenance == APPURTENANCE_RED) redPlayerMoney += money;
        graph.destroyModule(module);
        moneyExplosion((float)module.x, (float)module.y, (float)module.visualRadius, 0.02f * GameView.moduleSize, 15);
    }

    void moneyExplosion(float x, float y, float radius, float power, int howMany) {
        float a, p, r;
        for (int i=0; i<howMany; i++) {
            Bubble bubble = getAvailableBubble();
            a = 2f * (float)Math.PI * random.nextFloat();
            p = random.nextFloat() * power;
            r = random.nextFloat() * radius;
            bubble.setSpeed(p * (float)Math.cos(a), p * (float)Math.sin(a));
            bubble.setRadius(1.5f * defaultBubbleRadius, -0.02f * defaultBubbleRadius);
            bubble.setPos(x + r * (float)Math.cos(a), y + r * (float)Math.sin(a));
            bubble.setType(1);
        }
    }

    void timeCorrection(long correction) {
        YioGdxGame.say("time correction : " + correction);
        for (Module module : greenGraph.modules) {
            module.timeToEndConstruction += correction;
            module.timeCorrection(correction);
        }
        for (Module module : redGraph.modules) {
            module.timeToEndConstruction += correction;
            module.timeCorrection(correction);
        }
    }

    void touchDown(int screenX, int screenY, int pointer, int button) {
        currentTouchCount++;
        if (currentTouchCount == 1) {
            maxTouchCount = 1;
            for (UnitGroup group : groupList) group.assignedPath = false;
        }
        if (screenX < bandHeight && screenY > Gdx.graphics.getHeight() - bandHeight) {
            isBinButtonPressed = true;
            timeToUnPressBinButton = System.currentTimeMillis() + 500;
            demolishLastBuiltModule(APPURTENANCE_GREEN);
            if (tutorial && tutorialStepIndex == 6) playerCancelledSomething = true;
            return;
        }
        dragType = DRAG_TYPE_NONE;
        if (currentTouchCount > maxTouchCount) maxTouchCount = currentTouchCount;
        lastTouchCount = currentTouchCount;

        // now only lower band stuff
        if (screenY > bandHeight) return;
        if (screenX < leftSide) {
            return;
        } else if (screenX < leftSide + bandHeight) {
            dragType = DRAG_TYPE_BASE;
            dragModuleTexture = yioGdxGame.gameView.textureBaseGreen;
        } else if (screenX < leftSide + 2 * bandHeight) {
            dragType = DRAG_TYPE_LOOKOUT;
            dragModuleTexture = yioGdxGame.gameView.textureLookoutGreen;
        } else if (screenX < leftSide + 3 * bandHeight) {
            dragType = DRAG_TYPE_DEFENSE;
            dragModuleTexture = yioGdxGame.gameView.textureDefenseGreen;
        } else if (screenX < leftSide + 4 * bandHeight) {
            dragType = DRAG_TYPE_BARRACKS;
            dragModuleTexture = yioGdxGame.gameView.textureBarracksGreen;
        } else if (screenX < leftSide + 5 * bandHeight) {
            dragType = DRAG_TYPE_EXTRACTOR;
            dragModuleTexture = yioGdxGame.gameView.textureExtractorGreen;
        }
        this.screenX = screenX;
        this.screenY = screenY;
    }

    void touchUp(int screenX, int screenY, int pointer, int button) {
        currentTouchCount--;
        if (currentTouchCount == maxTouchCount - 1) reformUnitsGroups(maxTouchCount, APPURTENANCE_GREEN);
        lastTouchCount = currentTouchCount;

        if (screenX < bandHeight && screenY > Gdx.graphics.getHeight() - bandHeight) {
            dragType = DRAG_TYPE_NONE;
            dragModuleTexture = null;
            isBinButtonPressed = true;
            timeToUnPressBinButton = System.currentTimeMillis() + 500;
            return;
        }
        if (screenY < bandHeight) { // this is for nice module placement
            screenX = 0;
            screenY = 0;
        }
        switch (dragType) {
            case DRAG_TYPE_NONE:
                if (screenY > bandHeight && screenY < Gdx.graphics.getHeight() - bandHeight) {
                    UnitGroup closestGroup = getClosestUnitGroup(screenX, screenY, APPURTENANCE_GREEN, false);
                    if (closestGroup != null) closestGroup.setPosition(screenX, screenY);
                    else YioGdxGame.say("closest group is NULL");
                    addCommandToGo(screenX, screenY);
                    if (tutorial && tutorialStepIndex == 2) playerPressedSomewhere = true;
                }
                break;
            case DRAG_TYPE_BASE:
                buildModule(new ModuleBase(screenX, screenY, APPURTENANCE_GREEN, greenGraph));
//                playerWon();
                break;
            case DRAG_TYPE_LOOKOUT:
                buildModule(new ModuleLookout(screenX, screenY, APPURTENANCE_GREEN, greenGraph));
                break;
            case DRAG_TYPE_DEFENSE:
                buildModule(new ModuleDefense(screenX, screenY, APPURTENANCE_GREEN, greenGraph));
                break;
            case DRAG_TYPE_BARRACKS:
                buildModule(new ModuleBarracks(screenX, screenY, APPURTENANCE_GREEN, greenGraph));
                break;
            case DRAG_TYPE_EXTRACTOR:
                buildModule(new ModuleExtractor(screenX, screenY, APPURTENANCE_GREEN, greenGraph));
                break;
        }
        dragModuleTexture = null;
    }

    void touchDragged(int screenX, int screenY, int pointer) {
        this.screenX = screenX;
        this.screenY = screenY;
    }

    int getPressedModuleButtonPosition() {
        switch (dragType) {
            case DRAG_TYPE_NONE:
                return 0;
            case DRAG_TYPE_BASE:
                return leftSide;
            case DRAG_TYPE_LOOKOUT:
                return leftSide + bandHeight;
            case DRAG_TYPE_DEFENSE:
                return leftSide + 2 * bandHeight;
            case DRAG_TYPE_BARRACKS:
                return leftSide + 3 * bandHeight;
            case DRAG_TYPE_EXTRACTOR:
                return leftSide + 4 * bandHeight;
            default:
                return 0;
        }
    }

    public void levelHasSpecialMessage() {
        levelHasMessage = true;
        timeToDisplaySpecialLevelMessage = System.currentTimeMillis() + 1000;
    }

    public Graph getGraphByAppurtenance(int appurtenance) {
        switch (appurtenance) {
            case APPURTENANCE_GREEN: return greenGraph;
            case APPURTENANCE_RED: return redGraph;
            default: return null;
        }
    }
}
