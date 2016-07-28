package yio.tro.achipato.behaviors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.achipato.ButtonLighty;
import yio.tro.achipato.YioGdxGame;

/**
 * Created by ivan on 06.10.2014.
 */
public class RbTurnOffSound extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        YioGdxGame.turnOffSound();

//        Preferences preferences = Gdx.app.getPreferences("main");
//        preferences.putInteger("progress", 0);
//        preferences.flush();
//        buttonLighty.menuControllerLighty.yioGdxGame.setSelectedLevelIndex(0);
//        buttonLighty.menuControllerLighty.yioGdxGame.gameController.setProgress(0);
//        buttonLighty.menuControllerLighty.scrollerLighty.setSelectionIndex(0);
//        buttonLighty.menuControllerLighty.updateScrollerCache();
    }
}
