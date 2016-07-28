package yio.tro.achipato;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by ivan on 09.08.2014.
 */
public abstract class Module {

    double x, y;
    int appurtenance;
    ArrayList<Module> connectedModules;
    double visualX, visualY, dx, dy, visualRadius;
    public static Random random = new Random();
    boolean standStill;
    Light light;
    boolean isConstructing, alive, connectedToBottom, connectedToTop;
    long timeToEndConstruction;
    float visibilityRange, afterConstructionVisibilityRange;
    int rotationAngle;
    Graph graph;
    int price;
    int maxHP, hp;
    public static final int MODULE_INDEX_BASE = 0;
    public static final int MODULE_INDEX_BARRACKS = 1;
    public static final int MODULE_INDEX_EXTRACTOR = 2;
    public static final int MODULE_INDEX_LOOKOUT = 3;
    public static final int MODULE_INDEX_DEFENSE = 4;
    int index;
    ArrayList<WayPoint> deactivatedWayPoints;

    protected Module(double x, double y, int appurtenance, Graph graph) {
        this.x = x;
        this.y = y;
        this.appurtenance = appurtenance;
        this.graph = graph;
        visualX = x;
        visualY = y;
        connectedModules = new ArrayList<Module>();
        visualRadius = 0.02 * Gdx.graphics.getWidth();
        randomizeSpeed();
        standStill = false;
        visibilityRange = (float) visualRadius * 3;
        afterConstructionVisibilityRange = (float) visualRadius * 5;
        light = new Light((float)visualX, (float)visualY, visibilityRange);
        light.beginIllumination();
        isConstructing = true;
        alive = true;
        timeToEndConstruction = System.currentTimeMillis() + 1000; // this must be set for every module type
        deactivatedWayPoints = new ArrayList<WayPoint>();
    }

    void move() {
        rotationAngle += 1;
        if (isConstructing && System.currentTimeMillis() > timeToEndConstruction) {
            float k = visibilityRange / afterConstructionVisibilityRange;
            light.r = afterConstructionVisibilityRange;
            light.factorOfLighting.factor = k;
            graph.gameController.reportAboutModuleConstructionEnd(this);
            isConstructing = false;
        }
        light.move();
        if (standStill) {
            visualX = x;
            visualY = y;
            return;
        }
        visualX += dx;
        visualY += dy;
        if (YioGdxGame.distance(x, y, visualX, visualY) > visualRadius) {
            visualX += 0.1 * (x - visualX);
            visualY += 0.1 * (y - visualY);
            randomizeSpeed(YioGdxGame.angle(visualX, visualY, x, y));
        }
        light.setPosition((float)visualX, (float)visualY);
    }

    boolean isConnectedToDefenseModule() {
        if (index == MODULE_INDEX_DEFENSE) return true;
        for (int i=connectedModules.size()-1; i>=0; i--)
            if (connectedModules.get(i).index == MODULE_INDEX_DEFENSE) return true;
        return false;
    }

    void randomizeSpeed() {
        dx = 0.03 * random.nextDouble() * visualRadius - 0.015 * visualRadius;
        dy = 0.03 * random.nextDouble() * visualRadius - 0.015 * visualRadius;
    }

    void randomizeSpeed(double angle) {
        double a = angle - 0.4 * Math.PI + 0.8 * Math.PI * random.nextDouble();
        dx = 0.02 * visualRadius * Math.cos(a);
        dy = 0.02 * visualRadius * Math.sin(a);
    }

    public boolean isGreen() {
        return appurtenance == GameController.APPURTENANCE_GREEN;
    }

    public boolean isRed() {
        return appurtenance == GameController.APPURTENANCE_RED;
    }

    void connectToModule(Module module) {
        if (isConnectedToModule(module)) return;
        ListIterator iterator = connectedModules.listIterator();
        iterator.add(module);
    }

    boolean inPosition() {
        if (Math.abs(x - visualX) > visualRadius) return false;
        if (Math.abs(y - visualY) > visualRadius) return false;
        return true;
    }

    void deactivateWayPoints() {
        WayPoint wayPoint;
        for (int i=deactivatedWayPoints.size()-1; i>=0; i--) {
            wayPoint = deactivatedWayPoints.get(i);
            wayPoint.deactivate();
        }
    }

    void activateWayPoints() {
        WayPoint wayPoint;
        for (int i=deactivatedWayPoints.size()-1; i>=0; i--) {
            wayPoint = deactivatedWayPoints.get(i);
            wayPoint.activate();
        }
    }

    boolean isConnectedToModule(Module module) {
        for (Module m : connectedModules) {
            if (m == module) return true;
        }
        return false;
    }

    void checkIfConnectedToSide() {
        if (y < 0.18 * Gdx.graphics.getHeight()) connectedToBottom = true;
        if (y > 0.82 * Gdx.graphics.getHeight()) connectedToTop = true;
    }

    public int getPrice() {
        return price;
    }

    public void setStandStill(boolean standStill) {
        this.standStill = standStill;
    }

    public abstract void timeCorrection(long correction);
}
