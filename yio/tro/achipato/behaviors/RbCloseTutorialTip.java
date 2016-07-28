package yio.tro.achipato.behaviors;

import yio.tro.achipato.ButtonLighty;

/**
 * Created by ivan on 05.10.2014.
 */
public class RbCloseTutorialTip extends ReactBehavior{

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        buttonLighty.menuControllerLighty.yioGdxGame.setGamePaused(false);
        buttonLighty.menuControllerLighty.getButtonById(50).destroy();
        buttonLighty.menuControllerLighty.getButtonById(51).destroy();
        buttonLighty.menuControllerLighty.getButtonById(30).setTouchable(true);
    }
}
