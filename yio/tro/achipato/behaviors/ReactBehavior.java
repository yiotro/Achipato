package yio.tro.achipato.behaviors;

import yio.tro.achipato.ButtonLighty;

/**
 * Created by ivan on 05.08.14.
 */
public abstract class ReactBehavior {

    public abstract void reactAction(ButtonLighty buttonLighty);

    public static RbExit rbExit = new RbExit();
    public static RbInfo rbInfo = new RbInfo();
    public static RbMainMenu rbMainMenu = new RbMainMenu();
    public static RbGameSetupMenu rbGameSetupMenu = new RbGameSetupMenu();
    public static RbChangeSpeed rbChangeSpeed = new RbChangeSpeed();
    public static RbChangeDifficulty rbChangeDifficulty = new RbChangeDifficulty();
    public static RbStartGame rbStartGame = new RbStartGame();
    public static RbInGameMenu rbInGameMenu = new RbInGameMenu();
    public static RbResumeGame rbResumeGame = new RbResumeGame();
    public static RbCloseTutorialTip rbCloseTutorialTip = new RbCloseTutorialTip();
    public static RbTurnOffSound rbTurnOffSound = new RbTurnOffSound();
}
