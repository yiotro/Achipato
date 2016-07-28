package yio.tro.achipato.behaviors;

import com.badlogic.gdx.Gdx;
import yio.tro.achipato.ButtonLighty;

/**
 * Created by ivan on 05.08.14.
 */
public class RbExit extends ReactBehavior{

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        Gdx.app.exit();
    }
}
