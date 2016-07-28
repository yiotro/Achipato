package yio.tro.achipato;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.achipato.behaviors.ReactBehavior;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 * Created by ivan on 22.07.14.
 */
public class MenuControllerLighty {
    public YioGdxGame yioGdxGame;
    ArrayList<ButtonLighty> buttons;
    ButtonFactory buttonFactory;
    SimpleRectangle biggerBlockPosition;
    ButtonRenderer buttonRenderer;
    LanguagesManager languagesManager;
    Sound soundMenuButton, soundChangeButton;
    public ScrollerLighty scrollerLighty;
    TextureRegion unlockedLevelIcon, lockedLevelIcon, openedLevelIcon;

    public MenuControllerLighty(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        buttonFactory = new ButtonFactory(this);
        buttons = new ArrayList<ButtonLighty>();
        biggerBlockPosition = new SimpleRectangle(0.1 * Gdx.graphics.getWidth(), 0.1 * Gdx.graphics.getHeight(), 0.8 * Gdx.graphics.getWidth(), 0.8 * Gdx.graphics.getHeight());
        buttonRenderer = new ButtonRenderer();
        languagesManager = LanguagesManager.getInstance();
        soundMenuButton = Gdx.audio.newSound(Gdx.files.internal("sound/menu_button.ogg"));
        soundChangeButton = Gdx.audio.newSound(Gdx.files.internal("sound/change_button.ogg"));
        unlockedLevelIcon = GameView.loadTextureRegionByName("unlocked_level_icon.png", true);
        lockedLevelIcon = GameView.loadTextureRegionByName("locked_level_icon.png", true);
        openedLevelIcon = GameView.loadTextureRegionByName("opened_level_icon.png", true);

        scrollerLighty = new ScrollerLighty(yioGdxGame, generateRectangle(0.05, 0.05, 0.9, 0.8), 0.1f * Gdx.graphics.getHeight(), yioGdxGame.batch);
        if (scrollerLighty.selectionIndex == 0) scrollerLighty.addLine(unlockedLevelIcon, languagesManager.getString("how_to_play"));
        else scrollerLighty.addLine(openedLevelIcon, languagesManager.getString("how_to_play"));
        int si = scrollerLighty.selectionIndex;
        TextureRegion textureRegion = openedLevelIcon;
        for (int i=1; i<=YioGdxGame.INDEX_OF_LAST_LEVEL; i++) {
            if (i == si) textureRegion = unlockedLevelIcon;
            if (i == si + 1) textureRegion = lockedLevelIcon;
            scrollerLighty.addLine(textureRegion, languagesManager.getString("menu_level") + " " + i);

        }
        if (scrollerLighty.selectionIndex > 6) {
            scrollerLighty.pos = (scrollerLighty.selectionIndex - 1) * scrollerLighty.lineHeight - 0.5f * scrollerLighty.lineHeight;
            scrollerLighty.limit();
        }
//        Thread loadingThread = new Thread(new LoadingThread(scrollerLighty), "loading thread");
//        loadingThread.setPriority(1);
//        loadingThread.start();

        createMainMenu();
    }

    void updateScrollerLineTexture(int index) {
        if (index < 0 || index > YioGdxGame.INDEX_OF_LAST_LEVEL) return;
        TextureRegion textureRegion;
        if (index < yioGdxGame.gameController.progress) textureRegion = openedLevelIcon;
        else if (index == yioGdxGame.gameController.progress) textureRegion = unlockedLevelIcon;
        else textureRegion = lockedLevelIcon;
        scrollerLighty.icons.set(index, textureRegion);
        scrollerLighty.updateCacheLine(index);
    }

    public void updateScrollerCache() {
        for (int i=0; i<=YioGdxGame.INDEX_OF_LAST_LEVEL; i++) {
            updateScrollerLineTexture(i);
        }
    }

    Sound getDefaultSound() {
        return soundMenuButton;
    }

    public void move() {
        scrollerLighty.move();
        for (ButtonLighty buttonLighty : buttons) {
            buttonLighty.move();
        }
        for (int i=buttons.size()-1; i>=0; i--) {
            if (buttons.get(i).checkToPerformAction()) break;
        }
    }

    public void addMenuBlockToArray(ButtonLighty buttonLighty) {
        // considered that menu block is not in array at this moment
        ListIterator iterator = buttons.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
        iterator.add(buttonLighty);
    }

    public void removeMenuBlockFromArray(ButtonLighty buttonLighty) {
        ListIterator iterator = buttons.listIterator();
        ButtonLighty currentBlock;
        while (iterator.hasNext()) {
            currentBlock = (ButtonLighty) iterator.next();
            if (currentBlock == buttonLighty) {
                iterator.remove();
                return;
            }
        }
    }

    public ButtonLighty getButtonById(int id) { // can return null
        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.id == id) return buttonLighty;
        }
        return null;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (scrollerLighty.touchDown(screenX, screenY, pointer, button)) return true;
        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.isTouchable()) {
                if (buttonLighty.checkTouch(screenX, screenY, pointer, button)) return true;
            }
        }
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (scrollerLighty.touchUp(screenX, screenY, pointer, button)) return true;
        return false;
    }

    public void touchDragged(int screenX, int screenY, int pointer) {
        scrollerLighty.touchDragged(screenX, screenY, pointer);
    }

    void beginMenuCreation() {
        scrollerLighty.factorModel.beginFastDestroyProcess();
        for (ButtonLighty buttonLighty : buttons) {
            buttonLighty.destroy();
        }
        if (yioGdxGame.gameView != null) yioGdxGame.gameView.beginDestroyProcess();
    }

    void endMenuCreation() {

    }

    String getCurrentSpeedString() {
        switch (yioGdxGame.gameSettings.speed) {
            case GameSettings.SPEED_SLOW: return languagesManager.getString("game_settings_speed_slow");
            case GameSettings.SPEED_NORMAL: return languagesManager.getString("game_settings_speed_medium");
            case GameSettings.SPEED_FAST: return languagesManager.getString("game_settings_speed_fast");
            default: return "ERROR";
        }
    }

    String getCurrentDifficultyString() {
        switch (yioGdxGame.gameSettings.difficulty) {
            case GameSettings.DIFFICULTY_EASY: return languagesManager.getString("game_settings_ai_easy");
            case GameSettings.DIFFICULTY_NORMAL: return languagesManager.getString("game_settings_ai_normal");
            case GameSettings.DIFFICULTY_HARD: return languagesManager.getString("game_settings_ai_hard");
            default: return "ERROR";
        }
    }

    ArrayList<String> getArrayListFromString(String src) {
        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(src, "#");
        while (tokenizer.hasMoreTokens()) {
            list.add(tokenizer.nextToken());
        }
        return list;
    }

    SimpleRectangle generateRectangle(double x, double y, double width, double height) {
        return new SimpleRectangle(x * Gdx.graphics.getWidth(), y * Gdx.graphics.getHeight(), width * Gdx.graphics.getWidth(), height * Gdx.graphics.getHeight());
    }

    SimpleRectangle generateSquare(double x, double y, double size) {
        return generateRectangle(x, y, size, size * YioGdxGame.screenRatio);
    }

    void loadButtonOnce(ButtonLighty buttonLighty, String fileName) {
        if (buttonLighty.notRendered()) {
            buttonLighty.loadTexture(fileName);
        }
    }

    public void createMainMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(0);

        ButtonLighty exitButton = buttonFactory.getButton(generateSquare(0.8, 0.87, 0.15), 1, null);
        loadButtonOnce(exitButton, "shut_down.png");
        exitButton.setShadow(true);
        exitButton.setAnimType(ButtonLighty.ANIM_UP);
        exitButton.setReactBehavior(ReactBehavior.rbExit);
        exitButton.disableTouchAnimation();

        ButtonLighty infoButton = buttonFactory.getButton(generateSquare(0.05, 0.87, 0.15), 2, null);
        loadButtonOnce(infoButton, "info_icon.png");
        infoButton.setShadow(true);
        infoButton.setAnimType(ButtonLighty.ANIM_UP);
        infoButton.setReactBehavior(ReactBehavior.rbInfo);
        infoButton.disableTouchAnimation();

        ButtonLighty playButton = buttonFactory.getButton(generateSquare(0.3, 0.35, 0.4), 3, null);
        loadButtonOnce(playButton, "play_button.png");
        playButton.setReactBehavior(ReactBehavior.rbGameSetupMenu);
        playButton.disableTouchAnimation();

        endMenuCreation();
    }

    public void createInfoMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(1);

        ButtonLighty backButton = buttonFactory.getButton(generateRectangle(0.05, 0.9, 0.4, 0.07), 10, null);
        loadButtonOnce(backButton, "back_icon.png");
        backButton.setShadow(true);
        backButton.setAnimType(ButtonLighty.ANIM_UP);
        backButton.setReactBehavior(ReactBehavior.rbMainMenu);

        ButtonLighty resetButton = buttonFactory.getButton(generateRectangle(0.5, 0.9, 0.45, 0.07), 12, languagesManager.getString("menu_reset"));
        resetButton.setReactBehavior(ReactBehavior.rbTurnOffSound);
        resetButton.setAnimType(ButtonLighty.ANIM_UP);

        ButtonLighty infoPanel = buttonFactory.getButton(generateRectangle(0.05, 0.1, 0.9, 0.7), 11, null);
        if (infoPanel.notRendered()) {
            infoPanel.addManyLines(getArrayListFromString(languagesManager.getString("info_array")));
            buttonRenderer.renderButton(infoPanel);
        }
        infoPanel.setTouchable(false);
        infoPanel.setAnimType(ButtonLighty.ANIM_DOWN);

        endMenuCreation();
    }

    public void updateSpeedButtonTexture() {
        ButtonLighty speedButton = getButtonById(22);
        speedButton.setTextLine(getCurrentSpeedString());
        buttonRenderer.renderButton(speedButton);
    }

    public void updateDifficultyButtonTexture() {
        ButtonLighty difficultyButton = getButtonById(23);
        difficultyButton.setTextLine(getCurrentDifficultyString());
        buttonRenderer.renderButton(difficultyButton);
    }

    public void createGameSetupMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(2);

        ButtonLighty backButton = buttonFactory.getButton(generateRectangle(0.05, 0.9, 0.4, 0.07), 20, null);
        loadButtonOnce(backButton, "back_icon.png");
        backButton.setShadow(true);
        backButton.setAnimType(ButtonLighty.ANIM_UP);
        backButton.setReactBehavior(ReactBehavior.rbMainMenu);

        ButtonLighty startButton = buttonFactory.getButton(generateRectangle(0.55, 0.9, 0.4, 0.07), 24, languagesManager.getString("game_settings_start"));
        startButton.setReactBehavior(ReactBehavior.rbStartGame);
        startButton.setAnimType(ButtonLighty.ANIM_UP);

        if (scrollerLighty.selectionIndex > 6) {
            scrollerLighty.pos = (scrollerLighty.selectionIndex - 1) * scrollerLighty.lineHeight - 0.5f * scrollerLighty.lineHeight;
            scrollerLighty.limit();
        }
        scrollerLighty.factorModel.beginSpawnProcess();

        endMenuCreation();
    }

    public void createGameOverlay() {
        beginMenuCreation();

        ButtonLighty inGameMenuButton = buttonFactory.getButton(generateSquare(1 - 0.1 / YioGdxGame.screenRatio, 0.9, 0.1 / YioGdxGame.screenRatio), 30, null);
        loadButtonOnce(inGameMenuButton, "menu_icon.png");
        inGameMenuButton.setReactBehavior(ReactBehavior.rbInGameMenu);
        inGameMenuButton.setAnimType(ButtonLighty.ANIM_UP);
        inGameMenuButton.rectangularMask = true;

        endMenuCreation();
    }

    public void createInGameMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(3);

        ButtonLighty basePanel = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.4), 40, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;
        basePanel.setAnimType(ButtonLighty.ANIM_COLLAPSE_UP);

        ButtonLighty mainMenuButton = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.1), 41, languagesManager.getString("in_game_menu_main_menu"));
        mainMenuButton.setReactBehavior(ReactBehavior.rbMainMenu);
        mainMenuButton.setShadow(false);
        mainMenuButton.setAnimType(ButtonLighty.ANIM_UP);

        ButtonLighty resumeButton = buttonFactory.getButton(generateRectangle(0.1, 0.6, 0.8, 0.1), 42, languagesManager.getString("in_game_menu_resume"));
        resumeButton.setReactBehavior(ReactBehavior.rbResumeGame);
        resumeButton.setShadow(false);
        resumeButton.setAnimType(ButtonLighty.ANIM_UP);

        ButtonLighty restartButton = buttonFactory.getButton(generateRectangle(0.1, 0.5, 0.8, 0.1), 44, languagesManager.getString("in_game_menu_restart"));
        restartButton.setReactBehavior(ReactBehavior.rbStartGame);
        restartButton.setShadow(false);
        restartButton.setAnimType(ButtonLighty.ANIM_UP);

        ButtonLighty chooseLevelButton = buttonFactory.getButton(generateRectangle(0.1, 0.4, 0.8, 0.1), 43, languagesManager.getString("in_game_menu_new_game"));
        chooseLevelButton.setReactBehavior(ReactBehavior.rbGameSetupMenu);
        chooseLevelButton.setShadow(false);
        chooseLevelButton.setAnimType(ButtonLighty.ANIM_UP);

        endMenuCreation();
    }

    public void createTutorialTip(ArrayList<String> text) {
        yioGdxGame.setGamePaused(true);
        getButtonById(30).setTouchable(false);

        for (int i=0; i<2; i++) text.add("");
        ButtonLighty textPanel = buttonFactory.getButton(generateRectangle(0, 0, 1, 1), 50, null);
        textPanel.setPosition(generateRectangle(0, 0.3, 1, 0.05 * (double)text.size()));
        textPanel.cleatText();
        textPanel.addManyLines(text);
        buttonRenderer.renderButton(textPanel);
        textPanel.setTouchable(false);

        ButtonLighty okButton = buttonFactory.getButton(generateRectangle(0.6, 0.3, 0.4, 0.07), 51, languagesManager.getString("end_game_ok"));
        okButton.setShadow(false);
        okButton.setReactBehavior(ReactBehavior.rbCloseTutorialTip);
    }

    public void createAfterGameMenu(boolean playerWon) {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(3);

        String message = new String("ERROR");
        if (playerWon) message = languagesManager.getString("end_game_player_won");
        else message = languagesManager.getString("end_game_computer_won");
        if (yioGdxGame.gameController.tutorial && playerWon) message = languagesManager.getString("you_completed_tutorial");
        ButtonLighty textPanel = buttonFactory.getButton(generateRectangle(0.15, 0.4, 0.7, 0.2), 60, null);
        textPanel.cleatText();
        textPanel.addTextLine(message);
        textPanel.addTextLine("");
        textPanel.addTextLine("");
        buttonRenderer.renderButton(textPanel);
        textPanel.setTouchable(false);

        ButtonLighty okButton = buttonFactory.getButton(generateRectangle(0.45, 0.4, 0.4, 0.07), 61, languagesManager.getString("end_game_ok"));
        okButton.setShadow(false);
        okButton.setReactBehavior(ReactBehavior.rbGameSetupMenu);

        endMenuCreation();
    }

    public void createExceptionReport(Exception exception) {
        beginMenuCreation();
        yioGdxGame.setGamePaused(true);

        ArrayList<String> text = new ArrayList<String>();
        text.add("Error : " + exception.toString());
        String temp;
        int start, end;
        boolean go;
        for (int i=0; i<exception.getStackTrace().length; i++) {
            temp = exception.getStackTrace()[i].toString();
            start = 0;
            go = true;
            while (go) {
                end = start + 40;
                if (end > temp.length() - 1) {
                    go = false;
                    end = temp.length() - 1;
                }
                text.add(temp.substring(start, end));
                start = end + 1;
            }
        }
        ButtonLighty textPanel = buttonFactory.getButton(generateRectangle(0.1, 0.2, 0.8, 0.7), 6731267, null);
        if (textPanel.notRendered()) {
            textPanel.addManyLines(text);
            for (int i=0; i<10; i++) textPanel.addTextLine(" ");
            buttonRenderer.renderButton(textPanel);
        }
        textPanel.setTouchable(false);

        ButtonLighty okButton = buttonFactory.getButton(generateRectangle(0.1, 0.1, 0.8, 0.1), 73612321, "Ok");
        okButton.setReactBehavior(ReactBehavior.rbInGameMenu);

        endMenuCreation();
    }
}
