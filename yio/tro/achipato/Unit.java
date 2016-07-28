package yio.tro.achipato;

import android.util.Log;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ivan on 11.08.2014.
 */
public class Unit {
    float x, y, lastX, lastY;
    double dx, dy, sDx, sDy, groupPositionAngle, groupPositionRadius, speedFactor;
    double destinationX, destinationY;
    boolean inMotion;
    float visualX, visualY, diffX, diffY, diffDestX, diffDestY;
    double diffAngle;
    public static final float DIFF_RADIUS = 0.02f * Gdx.graphics.getWidth();
    public static final Random random = new Random();
    public static final float downLim = 0.1f * Gdx.graphics.getHeight();
    public static final float upLim = 0.9f * Gdx.graphics.getHeight();
    public static final float rightLim = Gdx.graphics.getWidth();
    double currentSpeed, a; // a - local
    GameController gameController;
    UnitGroup group;
    FactorModelLighty lightFactor, factorRadius;
    boolean alive, needToUpdateSpeedVector;
    long lastTimeCollidedWithObstacle;

    public Unit(float x, float y, GameController gameController) {
        this.x = x;
        this.y = y;
        this.gameController = gameController;
        lightFactor = new FactorModelLighty();
        lightFactor.beginSpawnProcess();
        factorRadius = new FactorModelLighty();
        factorRadius.beginSlowSpawnProcess();
        alive = true;
    }

    public void setSpeedFactor(double speedFactor) {
        this.speedFactor = speedFactor;
        sDx = speedFactor * dx;
        sDy = speedFactor * dy;
        currentSpeed = YioGdxGame.distance(0, 0, sDx, sDy);
    }

    public void setGroup(UnitGroup group) {
        this.group = group;
    }

    void checkToStop() {
        if (Math.abs(destinationX - x) < 3 * Math.abs(sDx) && Math.abs(destinationY - y) < 3 * Math.abs(sDy)) {
            if (YioGdxGame.distance(destinationX, destinationY, x, y) < 1.5 * currentSpeed)
                inMotion = false;
        }
    }

    void move() {
        lightFactor.move();
        factorRadius.move();
        ArrayList lastList = gameController.cacheMatrixUnits[(int)(visualX / gameController.cacheUnitsCellSize)][(int)(visualY / gameController.cacheUnitsCellSize)];
        if (inMotion) {
            checkToStop();
            lastX = x;
            lastY = y;
            x += sDx;
            y += sDy;
            if (x < 0) {
                x = 1;
                inMotion = false;
            }
            if (y < downLim) {
                y = downLim + 1;
                inMotion = false;
            }
            if (x > rightLim) {
                x = rightLim - 1;
                inMotion = false;
            }
            if (y > upLim) {
                y = upLim - 1;
                inMotion = false;
            }
            ArrayList<Bubble> obs = gameController.obstacleCache[(int)(x / gameController.cacheUnitsCellSize)][(int)(y / gameController.cacheUnitsCellSize)];
            if (obs.size() > 0) {
                Bubble bubble = obs.get(0);
                a = gameController.angles[(int)(x / gameController.cacheUnitsCellSize)][(int)(y / gameController.cacheUnitsCellSize)];
                if (YioGdxGame.distance(x, y, bubble.x, bubble.y) < bubble.r + 2 * GameView.unitRadius) {
                    x += currentSpeed * Math.cos(a);
                    y += currentSpeed * Math.sin(a);
                    needToUpdateSpeedVector = true;
                    lastTimeCollidedWithObstacle = System.currentTimeMillis();
                }
            }
            if (needToUpdateSpeedVector && System.currentTimeMillis() > lastTimeCollidedWithObstacle + 100) {
                needToUpdateSpeedVector = false;
                a = YioGdxGame.angle(x, y, destinationX, destinationY);
                sDx = currentSpeed * Math.cos(a);
                sDy = currentSpeed * Math.sin(a);
                dx = sDx / speedFactor;
                dy = sDy / speedFactor;
            }
        }
        visualX = x + diffX;
        visualY = y + diffY;
        if (visualX < 0) {
            visualX = 1;
            inMotion = false;
        }
        if (visualY < downLim) {
            visualY = downLim + 1;
            inMotion = false;
        }
        if (visualX > rightLim) {
            visualX = rightLim - 1;
            inMotion = false;
        }
        if (visualY > upLim) {
            visualY = upLim - 1;
            inMotion = false;
        }
        diffX += 0.02 * (diffDestX - diffX);
        diffY += 0.02 * (diffDestY - diffY);
        if (Math.abs(diffDestX - diffX) < 0.1 * DIFF_RADIUS && Math.abs(diffDestY - diffY) < 0.1 * DIFF_RADIUS) {
            diffAngle = 2f * Math.PI * random.nextDouble();
            diffDestX = DIFF_RADIUS * (float)Math.cos(diffAngle);
            diffDestY = DIFF_RADIUS * (float)Math.sin(diffAngle);
        }
        ArrayList currentList = gameController.cacheMatrixUnits[(int)(visualX / gameController.cacheUnitsCellSize)][(int)(visualY / gameController.cacheUnitsCellSize)];
        if (currentList != lastList) {
            currentList.add(this);
            lastList.remove(this);
            for (int i=currentList.size()-1; i>=0; i--) {
                if (i > 0) ((Unit)(currentList.get(i))).lightFactor.beginDestroyProcess();
                else ((Unit)(currentList.get(i))).lightFactor.beginSpawnProcess();
            }
        }
    }
}
