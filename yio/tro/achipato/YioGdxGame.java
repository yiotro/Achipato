package yio.tro.achipato;

import android.util.Log;
import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import java.util.StringTokenizer;

public class YioGdxGame extends ApplicationAdapter implements InputProcessor {
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    int w, h;
    MenuControllerLighty menuControllerLighty;
    MenuViewLighty menuViewLighty;
    public static BitmapFont font, gameFont, lowerBandFont, listFont;
    private static GlyphLayout glyphLayout = new GlyphLayout();
    public static final String FONT_CHARACTERS = "йцукенгшщзхъёфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";
    public static int FONT_SIZE;
    public static boolean SOUND = true;
    public static final int INDEX_OF_LAST_LEVEL = 80; // with tutorial
    TextureRegion mainBackground, infoBackground, settingsBackground, pauseBackground;
    TextureRegion currentBackground, lastBackground, hexagonalGrid;
    public static float screenRatio;
    public GameSettings gameSettings;
    public GameController gameController;
    public GameView gameView;
    boolean gamePaused, readyToUnPause;
    long timeToUnPause;
    int frameSkipCount;
    FrameBuffer frameBuffer;
    FactorModelLighty blackoutFactor;
    ArrayList<Splat> splats;
    long timeToSpawnNextSplat;
    float splatSize;
    int currentSplatIndex;
    public static final Random random = new Random();
    long lastTimeButtonPressed;
    boolean alreadyShownErrorMessageOnce, showFpsInfo;
    int fps, currentFrameCount;
    long timeToUpdateFpsInfo;
    int currentBackgroundIndex;
    long timeWhenPauseStarted, timeForFireworkExplosion;
    boolean fireworkEnabled, backAnimation;
    Bubble fireBubbles[];
    int currentBubbleIndex, selectedLevelIndex, splashCount;
    float defaultBubbleRadius;
    double bubbleGravity;
    boolean ignoreNextTimeCorrection, loadedResources, ignoreDrag;
    TextureRegion splash;


    @Override
    public void create() {
        YioGdxGame.say("Application starting...");
        loadedResources = false;
        splashCount = 0;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        splash = GameView.loadTextureRegionByName("splash.png", true);
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        screenRatio = (float) w / (float) h;
        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }


    public static void say(String message) {
        System.out.println(message);
    }


    void loadResourcesAndInitEverything() {
        loadedResources = true;
        gameSettings = new GameSettings(this);
        gameSettings.speed = GameSettings.SPEED_NORMAL;
        gameSettings.difficulty = GameSettings.DIFFICULTY_NORMAL;
        FileHandle fontFile = Gdx.files.internal("font.otf");
        mainBackground = GameView.loadTextureRegionByName("main_menu_background.png", true);
        infoBackground = GameView.loadTextureRegionByName("info_background.png", true);
        settingsBackground = GameView.loadTextureRegionByName("settings_background.png", true);
        hexagonalGrid = GameView.loadTextureRegionByName("hexagonal_grid.png", true);
        pauseBackground = GameView.loadTextureRegionByName("pause_background.png", true);
        blackoutFactor = new FactorModelLighty();
        splats = new ArrayList<Splat>();
        splatSize = 0.15f * Gdx.graphics.getWidth();
        ListIterator iterator = splats.listIterator();
        for (int i = 0; i < 50; i++) {
            iterator.add(new Splat(0, 0, 0));
        }
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FONT_SIZE = (int) (0.031 * Gdx.graphics.getHeight());
        parameter.size = FONT_SIZE;
        parameter.characters = FONT_CHARACTERS;
        parameter.flip = true;
        font = generator.generateFont(parameter);
        parameter.size = 2 * FONT_SIZE;
        listFont = generator.generateFont(parameter);
        listFont.setColor(Color.BLACK);
        parameter.size = FONT_SIZE;
        parameter.flip = false;
        lowerBandFont = generator.generateFont(parameter);
        lowerBandFont.setColor(1, 0.5f, 0, 1);
        parameter.size = 3 * FONT_SIZE;
        gameFont = generator.generateFont(parameter);
        gameFont.setColor(0, 0.2f, 0, 1);
        generator.dispose();
        gamePaused = true;
        alreadyShownErrorMessageOnce = false;
        showFpsInfo = false;
        fireworkEnabled = false;
        fps = 0;
        selectedLevelIndex = 0;
        timeToUpdateFpsInfo = System.currentTimeMillis() + 1000;
//        decorations = new ArrayList<BackgroundMenuDecoration>();
//        initDecorations();

        Preferences preferences = Gdx.app.getPreferences("main");
        selectedLevelIndex = preferences.getInteger("progress", 0); // 0 - default value
        menuControllerLighty = new MenuControllerLighty(this);
        menuViewLighty = new MenuViewLighty(this);
        gameController = new GameController(this); // must be called after menu controller is created. because of languages manager and other stuff
        gameView = new GameView(this);
        gameView.factorModelLighty.beginDestroyProcess();
        currentBackgroundIndex = -1;
        currentBackground = gameView.blackPixel; // call this after game view is created
        beginBackgroundChange(0);
        defaultBubbleRadius = 0.02f * w;
        bubbleGravity = 0.00025 * w;
        fireBubbles = new Bubble[150];
        for (int i = 0; i < fireBubbles.length; i++) { // this must be after game view is created
            fireBubbles[i] = new Bubble();
            fireBubbles[i].setType(random.nextInt(3) + 2);
        }

        Gdx.input.setInputProcessor(this);
        Gdx.gl.glClearColor(0, 0, 0, 1);
    }


    Bubble getAvailableBubble() {
        Bubble bubble = fireBubbles[currentBubbleIndex];
        currentBubbleIndex++;
        if (currentBubbleIndex >= fireBubbles.length) currentBubbleIndex = 0;
        if (bubble.isVisible()) return fireBubbles[0];
        return bubble;
    }


    void fireworkExplosion(float x, float y, float radius, float power, int howMany) {
        float a, p, r;
        for (int i = 0; i < howMany; i++) {
            Bubble bubble = getAvailableBubble();
            a = 2f * (float) Math.PI * random.nextFloat();
            p = random.nextFloat() * power;
            r = random.nextFloat() * radius;
            bubble.setSpeed(p * (float) Math.cos(a), p * (float) Math.sin(a));
            bubble.setRadius(1.5f * defaultBubbleRadius, -0.02f * defaultBubbleRadius);
            bubble.setPos(x + r * (float) Math.cos(a), y + r * (float) Math.sin(a));
        }
    }


    public void setFireworkEnabled(boolean fireworkEnabled) {
        this.fireworkEnabled = fireworkEnabled;
        if (fireworkEnabled) timeForFireworkExplosion = System.currentTimeMillis();
    }


    public void setBackAnimation(boolean backAnimation) {
        this.backAnimation = backAnimation;
    }


    public void setGamePaused(boolean gamePaused) {
        if (gamePaused) {
            this.gamePaused = true;
            timeWhenPauseStarted = System.currentTimeMillis();
        } else {
            unPauseAfterSomeTime();
            beginBackgroundChange(4);
        }
    }


    public void beginBackgroundChange(int index) {
        if (currentBackgroundIndex == index) return;
        currentBackgroundIndex = index;
        lastBackground = currentBackground;
        switch (index) {
            case 0:
                currentBackground = mainBackground;
                break;
            case 1:
                currentBackground = infoBackground;
                break;
            case 2:
                currentBackground = settingsBackground;
                break;
            case 3:
                currentBackground = pauseBackground;
                break;
            case 4:
                currentBackground = gameView.blackPixel;
                break;
        }
        blackoutFactor.setStartConditions(0, 0);
        blackoutFactor.beginFastSpawnProcess();
    }


    void timeCorrection(long correction) {
        if (ignoreNextTimeCorrection) {
            ignoreNextTimeCorrection = false;
            return;
        }
        gameController.timeCorrection(correction);
    }


    void letsIgnoreNextTimeCorrection() {
        ignoreNextTimeCorrection = true;
    }


    public void move() {
        if (!loadedResources) return;
        blackoutFactor.move();
        if (fireworkEnabled) {
            for (Bubble bubble : fireBubbles) {
                if (bubble.isVisible()) {
                    bubble.move();
                    bubble.gravity(bubbleGravity);
                    bubble.limitByWalls(w);
                }
            }
            if (System.currentTimeMillis() > timeForFireworkExplosion) {
                fireworkExplosion(0.1f * w + 0.8f * random.nextFloat() * w, 0.2f * h + 0.8f * random.nextFloat() * h, 0, 0.015f * w, 25);
                timeForFireworkExplosion = System.currentTimeMillis() + 200 + random.nextInt(200);
            }
        }
        if (readyToUnPause && System.currentTimeMillis() > timeToUnPause) {
            gamePaused = false;
            readyToUnPause = false;
            gameController.currentTouchCount = 0;
            timeCorrection(System.currentTimeMillis() - timeWhenPauseStarted);
        }
        gameView.factorModelLighty.move();
        menuControllerLighty.move();
        if (!gamePaused) {
            gameView.move();
            gameController.move();
        }
        if (!gameView.coversAllScreen()) {
            if (System.currentTimeMillis() > timeToSpawnNextSplat) {
                timeToSpawnNextSplat = System.currentTimeMillis() + 1000 + random.nextInt(200);
                float sx, sy;
                sx = random.nextFloat() * Gdx.graphics.getWidth();
                sy = random.nextFloat() * Gdx.graphics.getHeight();
                for (int i = 0; i < 3; i++) {
                    int c = 0, size = splats.size();
                    Splat splat = null;
                    while (c < size) {
                        c++;
                        splat = splats.get(currentSplatIndex);
                        currentSplatIndex++;
                        if (currentSplatIndex >= size) currentSplatIndex = 0;
                        if (!splat.isVisible()) break;
                    }
                    if (splat != null) {
                        float a, p;
                        a = 2f * (float) Math.PI * random.nextFloat();
                        p = 0.01f * splatSize * random.nextFloat();
                        splat.set(sx, sy, 2500);
                        splat.setSpeed(p * (float) Math.cos(a), p * (float) Math.sin(a));
                    }
                }
            }
            for (Splat splat : splats) {
                splat.move();
            }
        }
    }


    public static void turnOffSound() {
        SOUND = false;
    }


    void renderInternals() {
        currentFrameCount++;
        if (showFpsInfo && System.currentTimeMillis() > timeToUpdateFpsInfo) {
            timeToUpdateFpsInfo = System.currentTimeMillis() + 1000;
            fps = currentFrameCount;
            currentFrameCount = 0;
        }
        if (!gameView.coversAllScreen()) {
            Color c = batch.getColor();
            batch.begin();
            if (blackoutFactor.factor() < 0.99) {
                batch.setColor(c.r, c.g, c.b, 1);
                if (backAnimation) {
                    float f = (float) (1 - 0.2 * blackoutFactor.factor());
                    batch.draw(lastBackground, 0.5f * w - 0.5f * w * f, 0.5f * h - 0.5f * h * f, w * f, h * f);
                } else batch.draw(lastBackground, 0, 0, w, h);
            }
            batch.setColor(c.r, c.g, c.b, (float) blackoutFactor.factor());
            if (backAnimation) batch.draw(currentBackground, 0, 0, w, h);
            else {
                float f = (float) (0.8 + 0.2 * blackoutFactor.factor());
                batch.draw(currentBackground, 0.5f * w - 0.5f * w * f, 0.5f * h - 0.5f * h * f, w * f, h * f);
            }
            batch.setColor(c.r, c.g, c.b, 1);
            batch.end();

            maskingBegin();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            float sSize;
            for (Splat splat : splats) {
                sSize = (float) splat.factorModelLighty.factor() * splatSize;
                shapeRenderer.circle(splat.x - 0.5f * sSize, splat.y - 0.5f * sSize, sSize, 8);
            }
            shapeRenderer.end();
            batch.begin();
            maskingContinue();
            batch.draw(hexagonalGrid, 0, 0, w, h);
            batch.end();
            batch.setColor(c.r, c.g, c.b, 1);
            maskingEnd();

            batch.begin();
            if (fireworkEnabled) {
                TextureRegion currTexture = gameView.magentaUnit;
                for (int k = 2; k < 5; k++) {
                    switch (k) {
                        case 2:
                            currTexture = gameView.magentaUnit;
                            break;
                        case 3:
                            currTexture = gameView.redUnit;
                            break;
                        case 4:
                            currTexture = gameView.orangeUnit;
                            break;
                    }
                    for (Bubble bubble : fireBubbles)
                        if (bubble.isVisible() && bubble.type == k)
                            batch.draw(currTexture, bubble.x - bubble.r, bubble.y - bubble.r, bubble.diam, bubble.diam);
                }
            }
            batch.end();
        }
        menuViewLighty.renderScroller();
        gameView.render();
        menuViewLighty.render();
        if (showFpsInfo) {
            batch.begin();
            lowerBandFont.draw(batch, "" + fps, 10, Gdx.graphics.getHeight() - 10);
            batch.end();
        }
    }


    public static final void maskingBegin() {
        Gdx.gl.glClearDepthf(1f);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);
        Gdx.gl.glColorMask(false, false, false, false);
    }


    public static final void maskingContinue() {
        Gdx.gl.glColorMask(true, true, true, true);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
    }


    public static final void maskingEnd() {
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }


    void increaseLevelSelection() {
        menuControllerLighty.scrollerLighty.increaseSelection();
        setSelectedLevelIndex(selectedLevelIndex + 1);
    }


    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!loadedResources) {
            batch.begin();
            batch.draw(splash, 0, 0, w, h);
            batch.end();
            if (splashCount == 2) loadResourcesAndInitEverything();
            splashCount++;
            return;
        }

        try {
            move();
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                menuControllerLighty.createExceptionReport(exception);
            }
        }

        if (gamePaused) {
            renderInternals();
        } else {
            if (Gdx.graphics.getDeltaTime() < 0.025 || frameSkipCount >= 2) {
                frameSkipCount = 0;
                frameBuffer.begin();
                renderInternals();
                frameBuffer.end();
            } else {
                frameSkipCount++;
            }
            batch.begin();
            batch.draw(frameBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, true);
            batch.end();
        }
    }


    void unPauseAfterSomeTime() {
        readyToUnPause = true;
        timeToUnPause = System.currentTimeMillis() + 450; // время анимации - около 420мс
    }


    public void startGame() {
        if (selectedLevelIndex > gameController.progress) return;
        if (selectedLevelIndex < 0 || selectedLevelIndex > INDEX_OF_LAST_LEVEL) return;
        gameController.prepareForNewGame(selectedLevelIndex);
        menuControllerLighty.createGameOverlay();
        gameView.beginSpawnProcess();
        setGamePaused(false);
        letsIgnoreNextTimeCorrection();
        gameController.tutorial = false;
        if (selectedLevelIndex == 0) {
            gameController.tutorial = true;
            gameController.tutorialStepIndex = 0;
        }
    }


    static double angle(double x1, double y1, double x2, double y2) {
        if (x1 == x2) {
            if (y2 > y1) return 0.5 * Math.PI;
            if (y2 < y1) return 1.5 * Math.PI;
            return 0;
        }
        if (x2 >= x1) return Math.atan((y2 - y1) / (x2 - x1));
        else return Math.PI + Math.atan((y2 - y1) / (x2 - x1));
    }


    static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }


    public static ArrayList<String> decodeStringToArrayList(String string, String delimiters) {
        ArrayList<String> res = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(string, delimiters);
        while (tokenizer.hasMoreTokens()) {
            res.add(tokenizer.nextToken());
        }
        return res;
    }


    public void setSelectedLevelIndex(int selectedLevelIndex) {
        if (selectedLevelIndex >= 0 && selectedLevelIndex <= INDEX_OF_LAST_LEVEL)
            this.selectedLevelIndex = selectedLevelIndex;
    }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK) {
            if (!gamePaused) {
                menuControllerLighty.createInGameMenu();
                setGamePaused(true);
            }
        }
        return false;
    }


    @Override
    public boolean keyUp(int keycode) {
        return false;
    }


    @Override
    public boolean keyTyped(char character) {
        return false;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        ignoreDrag = true;
        try {
            if (!gameView.isInMotion() && menuControllerLighty.touchDown(screenX, Gdx.graphics.getHeight() - screenY, pointer, button)) {
                lastTimeButtonPressed = System.currentTimeMillis();
                return false;
            } else {
                ignoreDrag = false;
            }
            if (!gamePaused && gameView.coversAllScreen())
                gameController.touchDown(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                menuControllerLighty.createExceptionReport(exception);
            }
        }
        return false;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        try {
            menuControllerLighty.touchUp(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
            if (!gamePaused && gameView.coversAllScreen() && System.currentTimeMillis() > lastTimeButtonPressed + 300)
                gameController.touchUp(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                menuControllerLighty.createExceptionReport(exception);
            }
        }
        return false;
    }


    public static void playSound(Sound sound) {
        if (!SOUND) return;
        sound.play();
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        menuControllerLighty.touchDragged(screenX, Gdx.graphics.getHeight() - screenY, pointer);
        if (!ignoreDrag && !gamePaused && gameView.coversAllScreen())
            gameController.touchDragged(screenX, Gdx.graphics.getHeight() - screenY, pointer);
        return false;
    }


    static public float getTextWidth(BitmapFont font, String text) {
        glyphLayout.setText(font, text);
        return glyphLayout.width;
    }


    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }


    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
