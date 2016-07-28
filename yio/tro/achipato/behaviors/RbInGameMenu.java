package yio.tro.achipato.behaviors;

import yio.tro.achipato.ButtonLighty;

/**
 * Created by ivan on 06.08.14.
 */
public class RbInGameMenu extends ReactBehavior{

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        buttonLighty.menuControllerLighty.createInGameMenu();
        buttonLighty.menuControllerLighty.yioGdxGame.setGamePaused(true);
    }
}
