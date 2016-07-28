package yio.tro.achipato;

/**
 * Created by ivan on 13.08.2014.
 */
public class AnimPoint {
    float x, y;
    int appurtenance;
    FactorModelLighty factorModelLighty;

    public AnimPoint(float x, float y, int appurtenance) {
        this.x = x;
        this.y = y;
        this.appurtenance = appurtenance;
        factorModelLighty = new FactorModelLighty();
    }
}
