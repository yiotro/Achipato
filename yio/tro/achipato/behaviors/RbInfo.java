package yio.tro.achipato.behaviors;

import yio.tro.achipato.ButtonLighty;

/**
 * Created by ivan on 05.08.14.
 */
public class RbInfo extends ReactBehavior{

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        buttonLighty.menuControllerLighty.createInfoMenu();
    }
}
