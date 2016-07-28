package yio.tro.achipato.behaviors;

import yio.tro.achipato.ButtonLighty;

/**
 * Created by ivan on 05.08.14.
 */
public class RbGameSetupMenu extends ReactBehavior{

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        buttonLighty.menuControllerLighty.yioGdxGame.setGamePaused(true);
        buttonLighty.menuControllerLighty.createGameSetupMenu();
        buttonLighty.menuControllerLighty.yioGdxGame.setFireworkEnabled(false);
    }
}
