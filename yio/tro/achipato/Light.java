package yio.tro.achipato;

/**
 * Created by ivan on 10.08.2014.
 */
public class Light {

    float x, y, r;
    FactorModelLighty factorOfLighting;

    public Light(float x, float y, float r) {
        this.x = x;
        this.y = y;
        this.r = r;
        factorOfLighting = new FactorModelLighty();
    }

    void move() {
        factorOfLighting.move();
    }

    void beginIllumination() {
        factorOfLighting.beginSpawnProcess();
    }

    void destroyIllumination() {
        factorOfLighting.beginSlowDestroyProcess();
    }

    void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float currentRadius() {
        return r * (float)factorOfLighting.factor();
    }
}
