package yio.tro.achipato;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by ivan on 13.08.2014.
 */
public class Splat {

    int splatType;
    float x, y, dx, dy;
    FactorModelLighty factorModelLighty;
    long timeToDestroy;

    public Splat(int splatType, float x, float y) {
        this.splatType = splatType;
        this.x = x;
        this.y = y;
        factorModelLighty = new FactorModelLighty();
    }

    void move() {
        factorModelLighty.move();
        if (System.currentTimeMillis() > timeToDestroy) {
            factorModelLighty.beginSlowDestroyProcess();
        }
        x += dx;
        y += dy;
    }

    void set(float x, float y, int lifeTime) {
        this.x = x;
        this.y = y;
        timeToDestroy = System.currentTimeMillis() + lifeTime + YioGdxGame.random.nextInt(200);
        factorModelLighty.beginSlowSpawnProcess();
    }

    void setSpeed(float sdx, float sdy) {
        dx = sdx;
        dy = sdy;
    }

    boolean isVisible() {
        return factorModelLighty.factor() > 0.1;
    }
}
