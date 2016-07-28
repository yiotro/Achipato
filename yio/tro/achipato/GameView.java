package yio.tro.achipato;

import android.util.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by ivan on 05.08.14.
 */
public class GameView {

    YioGdxGame yioGdxGame;
    GameController gameController;
    TextureRegion backgroundRegion;
    public FactorModelLighty factorModelLighty;
    FrameBuffer frameBuffer;
    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    float cx, cy, dw, dh, w, h;
    TextureRegion textureExtractorGreen, textureBarracksGreen, textureDefenseGreen, textureLookoutGreen, textureBaseGreen;
    TextureRegion textureExtractorEnemy, textureBarracksEnemy, textureDefenseEnemy, textureLookoutEnemy, textureBaseEnemy;
    TextureRegion lowerBand, upperBand, redUnit, magentaUnit, orangeUnit;
    TextureRegion binTexture, constructionTexture, commandToGoInternalTexture, commandToGoExternalTexture, dollarTexture;
    TextureRegion greenUnitTexture, enemyUnitTexture, fissureTexture, beamTexture, blackCircleTexture;
    TextureRegion grayCircle, bloodSplats[];
    int bandHeight, leftSide;
    TextureRegion animationTextureRegion;
    float linkLineThickness;
    public static final float moduleSize = 0.1f * Gdx.graphics.getWidth();
    public static final float unitRadius = 0.012f * Gdx.graphics.getWidth();
    TextureRegion blackPixel, currentBackground;
    Light centralLight;
    float unitVisibilityRadius;
    Color greenUnitColor, redUnitColor;
    ArrayList<Splat> splats;
    float splatSize;
    int currentSplatIndex;
    int segments;
    float beamThickness;


    public GameView(YioGdxGame yioGdxGame) { //must be called after creation of GameController and MenuView
        this.yioGdxGame = yioGdxGame;
        gameController = yioGdxGame.gameController;
        factorModelLighty = new FactorModelLighty();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        spriteBatch = yioGdxGame.batch;
        shapeRenderer = yioGdxGame.menuViewLighty.shapeRenderer;
        cx = yioGdxGame.w / 2;
        cy = yioGdxGame.h / 2;
        w = yioGdxGame.w;
        h = yioGdxGame.h;
        linkLineThickness = 0.01f * Gdx.graphics.getWidth();
        unitVisibilityRadius = 0.08f * Gdx.graphics.getWidth();
        beamThickness = 0.05f * Gdx.graphics.getWidth();
        centralLight = new Light(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Gdx.graphics.getHeight());
        greenUnitColor = new Color(0, 0.4f, 0, 1);
        redUnitColor = new Color(0.4f, 0, 0, 1);
        segments = Gdx.graphics.getWidth() / 75;
        if (segments < 12) segments = 12;
        if (segments > 24) segments = 24;
        loadTextures();
        // create splats after textures are loaded
        splats = new ArrayList<Splat>();
        ListIterator iterator = splats.listIterator();
        for (int i = 0; i < 50; i++) {
            iterator.add(new Splat(YioGdxGame.random.nextInt(3), 0, 0));
        }
        splatSize = 0.08f * Gdx.graphics.getWidth();
    }


    void loadTextures() {
        backgroundRegion = loadTextureRegionByName("game_background.png", true);
        textureExtractorGreen = loadTextureRegionByName("green/module_extractor.png", true);
        textureBarracksGreen = loadTextureRegionByName("green/module_barracks.png", true);
        textureDefenseGreen = loadTextureRegionByName("green/module_defense.png", true);
        textureLookoutGreen = loadTextureRegionByName("green/module_lookout.png", true);
        textureBaseGreen = loadTextureRegionByName("green/module_base.png", true);
        loadEnemyTextures(0);
        binTexture = loadTextureRegionByName("cancel_icon.png", true);
        constructionTexture = loadTextureRegionByName("construction.png", false);
        commandToGoInternalTexture = loadTextureRegionByName("command_to_go1.png", false);
        commandToGoExternalTexture = loadTextureRegionByName("command_to_go2.png", false);
        dollarTexture = loadTextureRegionByName("dollar.png", false);
        greenUnitTexture = loadTextureRegionByName("green/unit.png", false);
        redUnit = loadTextureRegionByName("red/unit.png", false);
        magentaUnit = loadTextureRegionByName("magenta/unit.png", false);
        orangeUnit = loadTextureRegionByName("orange/unit.png", false);
        fissureTexture = loadTextureRegionByName("fissure.png", false);
        beamTexture = loadTextureRegionByName("beam.png", false);
        blackCircleTexture = loadTextureRegionByName("black_circle.png", false);
        bloodSplats = new TextureRegion[3];
        bloodSplats[0] = loadTextureRegionByName("blood1.png", false);
        bloodSplats[1] = loadTextureRegionByName("blood2.png", false);
        bloodSplats[2] = loadTextureRegionByName("blood3.png", false);
        grayCircle = loadTextureRegionByName("gray_circle.png", true);
        initLowerBand();
        initUpperBand();
        blackPixel = yioGdxGame.menuViewLighty.blackPixel;
        createCurrentBackgroundTexture(0);
    }


    void loadEnemyTextures(int index) {
        switch ((new Random(index)).nextInt(3)) {
            case 0:
                textureExtractorEnemy = loadTextureRegionByName("red/module_extractor.png", true);
                textureBarracksEnemy = loadTextureRegionByName("red/module_barracks.png", true);
                textureDefenseEnemy = loadTextureRegionByName("red/module_defense.png", true);
                textureLookoutEnemy = loadTextureRegionByName("red/module_lookout.png", true);
                textureBaseEnemy = loadTextureRegionByName("red/module_base.png", true);
                enemyUnitTexture = redUnit;
                break;
            case 1:
                textureExtractorEnemy = loadTextureRegionByName("magenta/module_extractor.png", true);
                textureBarracksEnemy = loadTextureRegionByName("magenta/module_barracks.png", true);
                textureDefenseEnemy = loadTextureRegionByName("magenta/module_defense.png", true);
                textureLookoutEnemy = loadTextureRegionByName("magenta/module_lookout.png", true);
                textureBaseEnemy = loadTextureRegionByName("magenta/module_base.png", true);
                enemyUnitTexture = magentaUnit;
                break;
            case 2:
                textureExtractorEnemy = loadTextureRegionByName("orange/module_extractor.png", true);
                textureBarracksEnemy = loadTextureRegionByName("orange/module_barracks.png", true);
                textureDefenseEnemy = loadTextureRegionByName("orange/module_defense.png", true);
                textureLookoutEnemy = loadTextureRegionByName("orange/module_lookout.png", true);
                textureBaseEnemy = loadTextureRegionByName("orange/module_base.png", true);
                enemyUnitTexture = orangeUnit;
                break;
        }
    }


    void createCurrentBackgroundTexture(int index) {
        FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, 1, 1, false);
        frameBuffer.begin();
        switch ((new Random(index)).nextInt(5)) {
            case 0:
                Gdx.gl.glClearColor(0, 0.5f, 0.5f, 1);
                break;
            case 1:
                Gdx.gl.glClearColor(0, 1, 0, 1);
                break;
            case 2:
                Gdx.gl.glClearColor(0, 0, 1, 1);
                break;
            case 3:
                Gdx.gl.glClearColor(0.5f, 0.5f, 0, 1);
                break;
            case 4:
                Gdx.gl.glClearColor(0.5f, 0, 0.5f, 1);
                break;
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Texture texture = frameBuffer.getColorBufferTexture();
        TextureRegion coverPixel = new TextureRegion(texture, 1, 1);
        frameBuffer.end();

        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, yioGdxGame.w, yioGdxGame.h, false);
        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(backgroundRegion, 0, 0, yioGdxGame.w, yioGdxGame.h);
        Color c = spriteBatch.getColor();
        spriteBatch.setColor(c.r, c.g, c.b, 0.05f);
        spriteBatch.draw(coverPixel, 0, 0, yioGdxGame.w, yioGdxGame.h);
        spriteBatch.setColor(c.r, c.g, c.b, 1);
        spriteBatch.end();
        texture = frameBuffer.getColorBufferTexture();
        currentBackground = new TextureRegion(texture, yioGdxGame.w, yioGdxGame.h);
        frameBuffer.end();
    }


    void initLowerBand() {
        bandHeight = (int) (0.1 * Gdx.graphics.getHeight());
        int width = Gdx.graphics.getWidth();
        leftSide = (int) (0.5 * (width - 5 * bandHeight));
        FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        frameBuffer.begin();
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1); // was 0.58f, 0.58f, 0.58f, 1
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        spriteBatch.draw(textureExtractorGreen, leftSide + 4.15f * bandHeight, 0.3f * bandHeight, 0.7f * bandHeight, 0.7f * bandHeight);
        spriteBatch.draw(textureBarracksGreen, leftSide + 3.15f * bandHeight, 0.3f * bandHeight, 0.7f * bandHeight, 0.7f * bandHeight);
        spriteBatch.draw(textureDefenseGreen, leftSide + 2.15f * bandHeight, 0.3f * bandHeight, 0.7f * bandHeight, 0.7f * bandHeight);
        spriteBatch.draw(textureLookoutGreen, leftSide + 1.15f * bandHeight, 0.3f * bandHeight, 0.7f * bandHeight, 0.7f * bandHeight);
        spriteBatch.draw(textureBaseGreen, leftSide + 0.15f * bandHeight, 0.3f * bandHeight, 0.7f * bandHeight, 0.7f * bandHeight);
        YioGdxGame.lowerBandFont.draw(spriteBatch, "$" + GameController.PRICE_BASE, leftSide + 0.3f * bandHeight, 0.3f * bandHeight);
        YioGdxGame.lowerBandFont.draw(spriteBatch, "$" + GameController.PRICE_LOOKOUT, leftSide + 1.2f * bandHeight, 0.3f * bandHeight);
        YioGdxGame.lowerBandFont.draw(spriteBatch, "$" + GameController.PRICE_DEFENSE, leftSide + 2.2f * bandHeight, 0.3f * bandHeight);
        YioGdxGame.lowerBandFont.draw(spriteBatch, "$" + GameController.PRICE_BARRACKS, leftSide + 3.2f * bandHeight, 0.3f * bandHeight);
        YioGdxGame.lowerBandFont.draw(spriteBatch, "$" + GameController.PRICE_EXTRACTOR, leftSide + 4.2f * bandHeight, 0.3f * bandHeight);
        spriteBatch.end();

        Texture texture = frameBuffer.getColorBufferTexture();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        lowerBand = new TextureRegion(texture, width, bandHeight);
        lowerBand.flip(false, true);

        frameBuffer.end();
    }


    void initUpperBand() { //must be called after initLowerBand()
        FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        frameBuffer.begin();
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(binTexture, 0, 0, bandHeight, bandHeight);
        spriteBatch.end();
        Texture texture = frameBuffer.getColorBufferTexture();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        upperBand = new TextureRegion(texture, Gdx.graphics.getWidth(), bandHeight);
        upperBand.flip(false, true);
        frameBuffer.end();
    }


    public static TextureRegion loadTextureRegionByName(String name, boolean antialias) {
        Texture texture = new Texture(Gdx.files.internal(name));
        if (antialias) texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);
        return region;
    }


    void renderInternals() {
        spriteBatch.begin();
        spriteBatch.draw(blackPixel, 0, 0, yioGdxGame.w, yioGdxGame.h);
        spriteBatch.end();
        fogOfWar();
        spriteBatch.begin();
        spriteBatch.draw(currentBackground, 0, 0, yioGdxGame.w, yioGdxGame.h);
        renderSplats();
        renderBubbles();
        renderUnits();
        renderDollarAnims();
        renderBeams();
        renderGraph(gameController.greenGraph);
        renderGraph(gameController.redGraph);
        renderObstacles();
        spriteBatch.end();
//        renderWayGraph();
//        renderWayPoints();
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        spriteBatch.begin();
        renderDraggedModule();
        renderCommandsToGo();
        spriteBatch.draw(upperBand, 0, Gdx.graphics.getHeight() - bandHeight, Gdx.graphics.getWidth(), bandHeight);
        spriteBatch.draw(lowerBand, 0, 0, Gdx.graphics.getWidth(), bandHeight);
        renderPressedBandButtons();
        renderCurrentMoney();
        spriteBatch.end();
    }


    void renderObstacles() {
        for (Bubble obs : gameController.obstacles) {
            spriteBatch.draw(grayCircle, obs.x - obs.r, obs.y - obs.r, obs.diam, obs.diam);
        }
    }


    void renderBeams() {
        Beam beam;
        float thickness;
        for (int i = 0; i < gameController.beams.length; i++) {
            beam = gameController.beams[i];
            if (beam.isVisible()) {
                thickness = (float) beam.factorModelLighty.factor() * beamThickness;
                spriteBatch.draw(beamTexture, beam.x, beam.y - thickness * 0.5f, 0f, thickness * 0.5f, beam.length, thickness, 1f, 1f, (float) (180 / Math.PI * beam.a));
            }
        }
    }


    void renderSplats() {
        float sSize;
        TextureRegion currSplat;
        for (int i = 0; i < 3; i++) {
            currSplat = bloodSplats[i];
            for (Splat splat : splats) {
                if (splat.splatType == i) {
                    sSize = (float) splat.factorModelLighty.factor() * splatSize;
                    spriteBatch.draw(currSplat, splat.x - 0.5f * sSize, splat.y - 0.5f * sSize, sSize, sSize);
                }
            }
        }
    }


    void renderDollarAnims() {
        AnimPoint point;
        float f;
        for (int i = gameController.dollarAnims.size() - 1; i >= 0; i--) {
            point = gameController.dollarAnims.get(i);
            f = (float) point.factorModelLighty.factor();
            spriteBatch.draw(dollarTexture, point.x - 0.5f * f * moduleSize, point.y + 0.5f * (1f - f) * moduleSize, f * moduleSize, 0.5f * moduleSize);
        }
    }


    void renderCurrentMoney() {
        YioGdxGame.gameFont.draw(spriteBatch, "$" + gameController.greenPlayerMoney, 0.18f * Gdx.graphics.getWidth(), 0.985f * Gdx.graphics.getHeight());
    }


    void renderCommandsToGo() {
        float r, R;
        for (Light command : gameController.commandsToGo) {
            r = (float) command.factorOfLighting.factor() * command.r;
            R = (float) (Math.pow(command.factorOfLighting.factor(), 0.3)) * command.r;
            spriteBatch.draw(commandToGoInternalTexture, command.x - r, command.y - r, 2 * r, 2 * r);
            spriteBatch.draw(commandToGoExternalTexture, command.x - R, command.y - R, 2 * R, 2 * R);
        }
    }


    private void renderGreenUnits() {
        TextureRegion region = null;
        for (UnitGroup unitGroup : gameController.groupList) {
            if (unitGroup.appurtenance != GameController.APPURTENANCE_GREEN) continue;
            for (Unit unit : unitGroup.unitsInGroup) {
                spriteBatch.draw(greenUnitTexture, unit.visualX - unitRadius, unit.visualY - unitRadius, 2 * unitRadius, 2 * unitRadius);
            }
//            spriteBatch.draw(blackCircleTexture, (float)unitGroup.approximateX, (float)unitGroup.approximateY, 2 * unitRadius, 2 * unitRadius);
        }
    }


    private void renderRedUnits() {
        TextureRegion region = null;
        for (UnitGroup unitGroup : gameController.groupList) {
            if (unitGroup.appurtenance != GameController.APPURTENANCE_RED) continue;
            for (Unit unit : unitGroup.unitsInGroup) {
                spriteBatch.draw(enemyUnitTexture, unit.visualX - unitRadius, unit.visualY - unitRadius, 2 * unitRadius, 2 * unitRadius);
            }
//            spriteBatch.draw(blackCircleTexture, (float)unitGroup.approximateX, (float)unitGroup.approximateY, 2 * unitRadius, 2 * unitRadius);
        }
    }


    void renderUnits() {
        renderRedUnits();
        renderGreenUnits();
    }


    void renderBubbles() {
        TextureRegion textureRegion = blackCircleTexture;
        for (int k = 0; k < 2; k++) {
            switch (k) {
                case 0:
                    textureRegion = blackCircleTexture;
                    break;
                case 1:
                    textureRegion = dollarTexture;
                    break;
            }
            for (Bubble bubble : gameController.bubbles) {
                if (bubble.isVisible() && bubble.type == k) {
                    spriteBatch.draw(textureRegion, bubble.x - bubble.r, bubble.y - bubble.r, bubble.diam, bubble.diam);
                }
            }
        }
    }


    void renderPressedBandButtons() {
        Color c = spriteBatch.getColor();
        spriteBatch.setColor(c.r, c.g, c.b, 0.5f);
        if (gameController.isBinButtonPressed) {
            spriteBatch.draw(blackPixel, 0, Gdx.graphics.getHeight() - bandHeight, bandHeight, bandHeight);
        }
        if (gameController.dragModuleTexture != null) {
            spriteBatch.draw(blackPixel, gameController.getPressedModuleButtonPosition(), 0, bandHeight, bandHeight);
        }
        spriteBatch.setColor(c.r, c.g, c.b, 1);

    }


    void fogOfWar() {
        Gdx.gl.glClearDepthf(1f);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);
        Gdx.gl.glColorMask(false, false, false, false);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(centralLight.x, centralLight.y, centralLight.currentRadius());
        renderGraphLight(gameController.greenGraph);
        renderUnitsLight();
        Light light;
        for (int i = gameController.lights.size() - 1; i >= 0; i--) {
            light = gameController.lights.get(i);
            shapeRenderer.circle(light.x, light.y, light.currentRadius(), segments);
        }
        shapeRenderer.end();
        Gdx.gl.glColorMask(true, true, true, true);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
    }

//    void renderStoppedUnitLights() {
//        Unit unit;
//        for (ArrayList line[] : gameController.cacheMatrixUnits) {
//            for (ArrayList list : line) {
//                if (list != null && list.size() > 0) {
//                    unit = (Unit)list.get(0);
//                    if (!unit.inMotion && unit.group.isGreen())
//                        shapeRenderer.circle(unit.visualX, unit.visualY, unitVisibilityRadius);
//                }
//            }
//        }
//    }


    void renderUnitsLight() {
        for (UnitGroup unitGroup : gameController.groupList) {
            if (unitGroup.hasUnits() && unitGroup.isGreen()) {
                for (Unit unit : unitGroup.unitsInGroup) {
                    if (unit.lightFactor.factor() > 0.1)
                        shapeRenderer.circle(unit.visualX, unit.visualY, (float) unit.lightFactor.factor() * unitVisibilityRadius, segments);
                }
            }
        }
    }


    void renderGraphLight(Graph graph) {
        Light light;
        for (Module module : graph.modules) {
            light = module.light;
            if (module instanceof ModuleLookout) {
                shapeRenderer.circle(light.x, light.y, light.currentRadius(), 2 * segments);
                continue;
            }
            shapeRenderer.circle(light.x, light.y, light.currentRadius(), segments);
        }
    }


    void renderDraggedModule() {
        if (gameController.dragModuleTexture != null) {
            spriteBatch.draw(gameController.dragModuleTexture, gameController.screenX - 0.5f * moduleSize, gameController.screenY - 0.5f * moduleSize, moduleSize, moduleSize);
        }
    }


    void renderGraph(Graph graph) {
        for (Link link : graph.links) {
            drawLine(link.first.visualX, link.first.visualY, link.second.visualX, link.second.visualY, link.factorWidth.factor() * linkLineThickness, spriteBatch, blackPixel);
        }

        //render
        for (Module module : graph.modules) {
            if (graph.appurtenance == GameController.APPURTENANCE_GREEN && module.connectedToBottom)
                drawLine(module.visualX, module.visualY, module.x, bandHeight, module.light.factorOfLighting.factor() * linkLineThickness, spriteBatch, blackPixel);
            if (graph.appurtenance == GameController.APPURTENANCE_RED && module.connectedToTop)
                drawLine(module.visualX, module.visualY, module.x, Gdx.graphics.getHeight() - bandHeight, module.light.factorOfLighting.factor() * linkLineThickness, spriteBatch, blackPixel);
        }

        renderConstructingModules(graph);
        for (int i = 0; i < 5; i++) renderModulesByIndexAndAppurtenance(i, graph.appurtenance, graph);
        renderFissures(graph);
    }


    private void renderConstructingModules(Graph graph) {
        for (Module module : graph.modules) {
            if (module.isConstructing) {
                spriteBatch.draw(constructionTexture,
                        (float) module.visualX - 0.5f * moduleSize,
                        (float) module.visualY - 0.5f * moduleSize,
                        0.5f * moduleSize,
                        0.5f * moduleSize,
                        moduleSize,
                        moduleSize,
                        1f, 1f, (float) module.rotationAngle);
            }
        }
    }


    private void renderModulesByIndexAndAppurtenance(int index, int appurtenance, Graph graph) {
        TextureRegion textureRegion = textureBaseGreen;
        if (appurtenance == GameController.APPURTENANCE_GREEN) {
            switch (index) {
                case Module.MODULE_INDEX_BARRACKS:
                    textureRegion = textureBarracksGreen;
                    break;
                case Module.MODULE_INDEX_BASE:
                    textureRegion = textureBaseGreen;
                    break;
                case Module.MODULE_INDEX_DEFENSE:
                    textureRegion = textureDefenseGreen;
                    break;
                case Module.MODULE_INDEX_EXTRACTOR:
                    textureRegion = textureExtractorGreen;
                    break;
                case Module.MODULE_INDEX_LOOKOUT:
                    textureRegion = textureLookoutGreen;
                    break;
            }
        } else if (appurtenance == GameController.APPURTENANCE_RED) {
            switch (index) {
                case Module.MODULE_INDEX_BARRACKS:
                    textureRegion = textureBarracksEnemy;
                    break;
                case Module.MODULE_INDEX_BASE:
                    textureRegion = textureBaseEnemy;
                    break;
                case Module.MODULE_INDEX_DEFENSE:
                    textureRegion = textureDefenseEnemy;
                    break;
                case Module.MODULE_INDEX_EXTRACTOR:
                    textureRegion = textureExtractorEnemy;
                    break;
                case Module.MODULE_INDEX_LOOKOUT:
                    textureRegion = textureLookoutEnemy;
                    break;
            }
        }

        for (Module module : graph.modules) {
            if (module.isConstructing) continue;
            if (module.appurtenance != appurtenance) continue;
            if (module.index != index) continue;

            //different methods of drawing
            if (module.index == Module.MODULE_INDEX_BARRACKS) {
                spriteBatch.draw(textureRegion,
                        (float) module.visualX - 0.5f * moduleSize,
                        (float) module.visualY - 0.5f * moduleSize,
                        0.5f * moduleSize,
                        0.5f * moduleSize,
                        moduleSize,
                        moduleSize,
                        1f, 1f, (float) module.rotationAngle);
            } else if (module.index == Module.MODULE_INDEX_DEFENSE || module.index == Module.MODULE_INDEX_EXTRACTOR) {
                float k = 0.5f + 0.05f * (float) Math.sin(0.125 * module.rotationAngle);
                spriteBatch.draw(textureRegion, (float) module.visualX - k * moduleSize, (float) module.visualY - k * moduleSize, 2 * k * moduleSize, 2 * k * moduleSize);
            } else {
                spriteBatch.draw(textureRegion, (float) module.visualX - 0.5f * moduleSize, (float) module.visualY - 0.5f * moduleSize, moduleSize, moduleSize);
            }
        }
    }


    private void renderFissures(Graph graph) {
        float damageFactor;
        Color c = spriteBatch.getColor();
        for (Module module : graph.modules) {
            damageFactor = 1f - (float) module.hp / (float) module.maxHP;

            //different methods of drawing
            if (module.index == Module.MODULE_INDEX_BARRACKS) {
                if (damageFactor > 0.05) {
                    spriteBatch.setColor(c.r, c.g, c.b, damageFactor);
                    spriteBatch.draw(fissureTexture,
                            (float) module.visualX - 0.5f * moduleSize,
                            (float) module.visualY - 0.5f * moduleSize,
                            0.5f * moduleSize,
                            0.5f * moduleSize,
                            moduleSize,
                            moduleSize,
                            1f, 1f, (float) module.rotationAngle);
                }
            } else if (module.index == Module.MODULE_INDEX_DEFENSE || module.index == Module.MODULE_INDEX_EXTRACTOR) {
                float k = 0.5f + 0.05f * (float) Math.sin(0.125 * module.rotationAngle);
                if (damageFactor > 0.05) {
                    spriteBatch.setColor(c.r, c.g, c.b, damageFactor);
                    spriteBatch.draw(fissureTexture, (float) module.visualX - k * moduleSize, (float) module.visualY - k * moduleSize, 2 * k * moduleSize, 2 * k * moduleSize);
                }
            } else {
                if (damageFactor > 0.05) {
                    spriteBatch.setColor(c.r, c.g, c.b, damageFactor);
                    spriteBatch.draw(fissureTexture, (float) module.visualX - 0.5f * moduleSize, (float) module.visualY - 0.5f * moduleSize, moduleSize, moduleSize);
                }
            }
            spriteBatch.setColor(c.r, c.g, c.b, 1);
        }
    }


    public void beginSpawnProcess() {
        factorModelLighty.beginSpawnProcess();
        updateAnimationTexture();
    }


    public void lightUpAllLevel() {
        centralLight.factorOfLighting.factor = 1;
        centralLight.destroyIllumination();
    }


    public void beginDestroyProcess() {
        factorModelLighty.beginDestroyProcess();
        updateAnimationTexture();
    }


    void updateAnimationTexture() {
        frameBuffer.begin();
        renderInternals();
        frameBuffer.end();
        Texture texture = frameBuffer.getColorBufferTexture();
        animationTextureRegion = new TextureRegion(texture);
        animationTextureRegion.flip(false, true);
    }


    public void render() {
        if (factorModelLighty.factor() < 0.01) {
            //execution of this line of code is not guaranteed
            if (factorModelLighty.dy < 0 && animationTextureRegion != null && animationTextureRegion.getTexture() != null)
                animationTextureRegion.getTexture().dispose();
            return;
        } else if (factorModelLighty.factor() < 0.99) {
            spriteBatch.begin();
            spriteBatch.draw(animationTextureRegion, 0, ((float) factorModelLighty.factor() - 1) * h, w, h);
            spriteBatch.end();
        } else {
            renderInternals();
        }
    }


    void createSplat(float x, float y) {
        int c = 0, size = splats.size();
        Splat splat = null;
        while (c < size) {
            c++;
            if (currentSplatIndex < 0 || currentSplatIndex > splats.size() - 1)
                YioGdxGame.say("bad index: " + currentSplatIndex);
            splat = splats.get(currentSplatIndex);
            currentSplatIndex++;
            if (currentSplatIndex >= size) currentSplatIndex = 0;
            if (!splat.isVisible()) break;
        }
        if (splat != null) {
            splat.set(x, y, 5000);
            splat.factorModelLighty.beginVeryFastSpawnProcess();
        }
    }


    void move() {
        centralLight.move();
        for (Splat splat : splats) {
            splat.move();
        }
    }


    public static void drawLine(double x1, double y1, double x2, double y2, double thickness, SpriteBatch spriteBatch, TextureRegion blackPixel) {
        spriteBatch.draw(blackPixel, (float) x1, (float) (y1 - thickness * 0.5), 0f, (float) thickness * 0.5f, (float) YioGdxGame.distance(x1, y1, x2, y2), (float) thickness, 1f, 1f, (float) (180 / Math.PI * YioGdxGame.angle(x1, y1, x2, y2)));
    }


    public boolean coversAllScreen() {
        return factorModelLighty.factor() > 0.99;
    }


    boolean isInMotion() {
        return factorModelLighty.factor > 0.01 && factorModelLighty.factor < 0.99;
    }
}
