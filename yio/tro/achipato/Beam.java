package yio.tro.achipato;

/**
 * Created by ivan on 26.08.2014.
 */
public class Beam {
    float x, y, a, length;
    FactorModelLighty factorModelLighty;

    public Beam() {
        factorModelLighty = new FactorModelLighty();
    }

    void set(float x1, float y1, float x2, float y2) {
        x = x1;
        y = y1;
        a = (float)YioGdxGame.angle(x1, y1, x2, y2);
        length = (float)YioGdxGame.distance(x1, y1, x2, y2);
        factorModelLighty.factor = 1;
        factorModelLighty.beginDestroyProcess();
    }

    void move() {
        factorModelLighty.move();
    }

    boolean isVisible() {
        return factorModelLighty.factor() > 0.1;
    }
}
