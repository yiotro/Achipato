package yio.tro.achipato;

import android.util.Log;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by ivan on 11.08.2014.
 */
public class UnitGroup {
    ArrayList<Unit> unitsInGroup;
    double x, y, r;
    double lastAppointedAngle, lastAppointedRadius, angleStep;
    double averageUnitSpeed;
    int appurtenance;
    Random random;
    boolean assignedPath;
    double a; // local variable
    GameController gameController;
    double approximateX, approximateY, lastX, lastY;
    FactorModelLighty approximateFactor;

    public UnitGroup(double x, double y, int appurtenance, GameController gameController) {
        this.x = x;
        this.y = y;
        this.appurtenance = appurtenance;
        this.gameController = gameController;
        random = new Random();
        unitsInGroup = new ArrayList<Unit>();
        r = 0;
        lastAppointedAngle = 0;
        lastAppointedRadius = 0;
        angleStep = 0;
        averageUnitSpeed = 0.001 * Gdx.graphics.getWidth();
        approximateFactor = new FactorModelLighty();
        approximateX = x;
        approximateY = y;
        lastX = x;
        lastY = y;
        approximateFactor.gravity = 0;
    }

    void addUnit(Unit unit) {
        ListIterator iterator = unitsInGroup.listIterator();
        iterator.add(unit);
        unit.groupPositionAngle = 2d * Math.PI * random.nextDouble();
        unit.groupPositionRadius = r * Math.pow(random.nextDouble(), 0.1);
        unit.group = this;
        updateRadius();
    }

    void updateRadius() {
        r = 0.012 * Gdx.graphics.getWidth() * Math.sqrt(unitsInGroup.size());
    }

    void setPosition(double x, double y) {
        ArrayList<Bubble> list = gameController.obstacleCache[(int)(x / gameController.cacheUnitsCellSize)][(int)(y / gameController.cacheUnitsCellSize)];
        if (list.size() > 0) {
            Bubble obstacle = list.get(0);
            if (YioGdxGame.distance(obstacle.x, obstacle.y, x, y) < obstacle.r + 2 * GameView.unitRadius) {
                double d = 2 * GameView.unitRadius + obstacle.r - YioGdxGame.distance(x, y, obstacle.x, obstacle.y);
                double a = YioGdxGame.angle(obstacle.x, obstacle.y, x, y);
                x += d * Math.cos(a);
                y += d * Math.sin(a);
                if (x < 0.05 * Gdx.graphics.getWidth()) return;
                if (x > 0.95 * Gdx.graphics.getWidth()) return;
                if (y < 0.15 * Gdx.graphics.getHeight()) return;
                if (y > 0.85 * Gdx.graphics.getHeight()) return;
            }
        }
        lastX = approximateX;
        lastY = approximateY;
        this.x = x;
        this.y = y;
        assignedPath = true;
        for (Unit unit : unitsInGroup) {
            unit.groupPositionAngle = 2d * Math.PI * random.nextDouble();
            unit.groupPositionRadius = r * Math.pow(random.nextDouble(), 0.5);
            sendUnitToDestination(unit);
        }
        approximateFactor.setStartConditions(0, averageUnitSpeed / YioGdxGame.distance(lastX, lastY, x, y));
        approximateFactor.speedMultiplier = 1.8;
    }

    void setPosition(PointLighty position) {
        setPosition(position.x, position.y);
    }

    void sendUnitToDestination(Unit unit) {
        sendUnitToSpecificLocation(unit, x + unit.groupPositionRadius * Math.cos(unit.groupPositionAngle), y + unit.groupPositionRadius * Math.sin(unit.groupPositionAngle));
    }

    int numberOfUnitsNearApproximatePoint() { // может приводить к глюкам если в начале игры неаккуратно спавнить юнитов
        int n = 0;
        for (Unit unit : unitsInGroup) {
            if (Math.abs(unit.x - approximateX) < r && Math.abs(unit.y - approximateY) < r) n++;
        }
        return n;
    }

    void sendUnitToSpecificLocation(Unit unit, double x, double y) {
        if (x < 0) x = GameView.unitRadius;
        if (y < 0.1 * Gdx.graphics.getHeight()) y = 0.1 * Gdx.graphics.getHeight() + GameView.unitRadius;
        if (x > Gdx.graphics.getWidth()) x = Gdx.graphics.getWidth() - GameView.unitRadius;
        if (y > 0.9 * Gdx.graphics.getHeight()) y = 0.9 * Gdx.graphics.getHeight() - GameView.unitRadius;
        ArrayList<Bubble> list = gameController.obstacleCache[(int)(x / gameController.cacheUnitsCellSize)][(int)(y / gameController.cacheUnitsCellSize)];
        if (list.size() > 0) {
            Bubble obstacle = list.get(0);
            double d2 = 2 * GameView.unitRadius + obstacle.r - YioGdxGame.distance(x, y, obstacle.x, obstacle.y);
            double a = YioGdxGame.angle(obstacle.x, obstacle.y, x, y);
            x += d2 * Math.cos(a);
            y += d2 * Math.sin(a);
            if (x < 0 || y < 0.1 * Gdx.graphics.getHeight() || x > Gdx.graphics.getWidth() || y > 0.9 * Gdx.graphics.getHeight() ||
                    YioGdxGame.distance(x, y, this.x, this.y) > this.r) {
                x = this.x;
                y = this.y;
            }
        }
        unit.destinationX = x;
        unit.destinationY = y;
        a = YioGdxGame.angle(unit.x, unit.y, unit.destinationX, unit.destinationY);
        unit.dx = averageUnitSpeed * Math.cos(a);
        unit.dy = averageUnitSpeed * Math.sin(a);
        unit.setSpeedFactor(1.6 + 0.4 * Math.sqrt(random.nextDouble()));
        unit.inMotion = true;
    }

    void clear() {
        ListIterator iterator = unitsInGroup.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    boolean isGreen() {
        return appurtenance == GameController.APPURTENANCE_GREEN;
    }

    boolean isRed() {
        return appurtenance == GameController.APPURTENANCE_RED;
    }

    void move() {
        approximateFactor.move();
        approximateX = approximateFactor.factor * (x - lastX) + lastX;
        approximateY = approximateFactor.factor * (y - lastY) + lastY;
        for (Unit unit : unitsInGroup) {
            unit.move();
        }
    }

    boolean hasUnits() {
        return unitsInGroup.size() > 0;
    }

    public double getApproximateX() {
        return approximateX;
    }

    public double getApproximateY() {
        return approximateY;
    }

    public void setApproximatePosition(double x, double y) {
        approximateX = x;
        approximateY = y;
    }
}
