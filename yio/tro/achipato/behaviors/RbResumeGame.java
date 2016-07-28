package yio.tro.achipato.behaviors;

import yio.tro.achipato.ButtonLighty;

/**
 * Created by ivan on 06.08.14.
 */
public class RbResumeGame extends ReactBehavior{

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        buttonLighty.menuControllerLighty.createGameOverlay();
        buttonLighty.menuControllerLighty.yioGdxGame.gameView.beginSpawnProcess();
        buttonLighty.menuControllerLighty.yioGdxGame.setGamePaused(false);
    }
}
