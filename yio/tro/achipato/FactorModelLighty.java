package yio.tro.achipato;

public class FactorModelLighty {
    double factor;
    double gravity;
    double dy;
    double speedMultiplier;

    public FactorModelLighty() {
        beginSpawnProcess();
        setStartConditions(0, -0.05);
    }

    public void beginSpawnProcess() {
        gravity = 0.01;
        speedMultiplier = 0.3;
    }

    public void beginSlowSpawnProcess() {
        gravity = 0.01;
        speedMultiplier = 0.1;
    }

    public void beginFastSpawnProcess() {
        gravity = 0.01;
        speedMultiplier = 0.5;
    }

    public void beginVeryFastSpawnProcess() {
        gravity = 0.03;
        speedMultiplier = 0.7;
    }

    public void beginFastestSpawnProcess() {
        gravity = 0.03;
        speedMultiplier = 0.7;
    }

    public void beginDestroyProcess() {
        gravity = -0.01;
        speedMultiplier = 0.3;
    }

    public void beginFastDestroyProcess() {
        gravity = -0.01;
        speedMultiplier = 0.5;
    }

    public void beginSlowDestroyProcess() {
        gravity = -0.01;
        speedMultiplier = 0.05;
    }

    public void setStartConditions(double startFactor, double startDy) {
        factor = startFactor;
        dy = startDy;
    }

    public void move() {
        dy += gravity;
        factor += speedMultiplier * dy;
        if ((gravity >= 0) && factor > 1) {
            dy = 0;
            factor = 1;
        } else if ((gravity < 0 || dy < 0) && factor < 0) {
            dy = 0;
            factor = 0;
        }
    }

    public double factor() {
        return factor;
    }
}
