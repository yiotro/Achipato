package yio.tro.achipato;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by ivan on 06.10.2014.
 */
public class ScrollerLighty {

    float pos, speed, topLimit, bottomLimit, lineHeight, maxOffset;
    YioGdxGame yioGdxGame;
    SimpleRectangle frame, animFrame;
    ArrayList<TextureRegion> cache, icons;
    ArrayList<String> strings;
    FactorModelLighty factorModel, selectionFactor;
    FrameBuffer frameBuffer;
    SpriteBatch batch;
    BitmapFont font;
    TextureRegion bg1, bg2;
    float startY, lastY, variation, selectX, animRadius;
    boolean dragged;
    int selectionIndex;

    public ScrollerLighty(YioGdxGame yioGdxGame, SimpleRectangle frame, float lineHeight, SpriteBatch batch) {
        this.yioGdxGame = yioGdxGame;
        this.frame = frame;
        this.lineHeight = lineHeight;
        this.batch = batch;
        this.animFrame = new SimpleRectangle(frame);
        selectionIndex = -1;
        pos = 0;
        speed = 0;
        cache = new ArrayList<TextureRegion>();
        icons = new ArrayList<TextureRegion>();
        strings = new ArrayList<String>();
        bg1 = GameView.loadTextureRegionByName("scroller_bg1.png", false);
        bg2 = GameView.loadTextureRegionByName("scroller_bg2.png", false);
        factorModel = new FactorModelLighty();
        selectionFactor = new FactorModelLighty();
        bottomLimit = 0;
        maxOffset = 3 * lineHeight;
        selectionIndex = yioGdxGame.selectedLevelIndex;
    }

    public void move() {
        factorModel.move();
        selectionFactor.move();
        animFrame.set(frame.x, frame.y - (1 - factorModel.factor) * 1.2 * frame.height, frame.width, frame.height);
        limit();
        pos += speed;
        speed *= 0.95;
    }

    void limit() {
        if (pos > topLimit) {
            pos -= 0.15 * lineHeight;
            speed *= 0.9;
            if (pos < topLimit) pos = topLimit;
            if (pos > topLimit + maxOffset) { // далековато отклонился
                pos = topLimit + maxOffset;
                speed = 0;
            }
        }
        if (pos < bottomLimit) {
            pos += 0.15 * lineHeight;
            speed *= 0.9;
            if (pos > bottomLimit) pos = bottomLimit;
            if (pos < bottomLimit - maxOffset) { // далековато отклонился
                pos = bottomLimit - maxOffset;
                speed = 0;
            }
        }
    }

    private void updateTopLimit() {
        topLimit = lineHeight * cache.size() - (float)frame.height;
    }

    private void addIcon(TextureRegion icon) {
        ListIterator iterator = icons.listIterator();
        while (iterator.hasNext()) iterator.next();
        iterator.add(icon);
    }

    private void addString(String string) {
        ListIterator iterator = strings.listIterator();
        while (iterator.hasNext()) iterator.next();
        iterator.add(string);
    }

    private void addCacheLine(TextureRegion cl) {
        ListIterator iterator = cache.listIterator();
        while (iterator.hasNext()) iterator.next();
        iterator.add(cl);
    }

    void addLine(TextureRegion icon, String string) {
        addIcon(icon);
        addString(string);
        addCacheLine(renderLine(icon, string, cache.size()));
        updateTopLimit();
    }

    void addRenderedLine(TextureRegion icon, String string, TextureRegion cache) {
        addIcon(icon);
        addString(string);
        addCacheLine(cache);
        updateTopLimit();
    }

    void updateCacheLine(int index) {
        cache.set(index, renderLine(icons.get(index), strings.get(index), index));
    }

    TextureRegion renderLine(TextureRegion icon, String string, int n) {
        TextureRegion result;
        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        TextureRegion bg;
        if (n % 2 == 0) bg = bg1;
        else bg = bg2;
        batch.begin();
        batch.draw(bg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        font = YioGdxGame.listFont;

        batch.begin();
        batch.draw(icon, lineHeight, lineHeight, 0, 0, lineHeight, lineHeight, 1, 1, 180);
        font.draw(batch, string, 1.1f * lineHeight, 0.3f * lineHeight);
        batch.end();

        Texture texture = frameBuffer.getColorBufferTexture();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        result = new TextureRegion(texture, (int)frame.width, (int)lineHeight);
        frameBuffer.end();
        return result;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!isVisible() || screenX < frame.x || screenX > frame.x + frame.width || screenY < frame.y || screenY > frame.y + frame.height) return false;
        if (factorModel.factor < 0.95) return false;
        dragged = false;
        lastY = screenY;
        startY = screenY;
        variation = 0;
        return true;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (!isVisible() || screenX < frame.x || screenX > frame.x + frame.width || screenY < frame.y || screenY > frame.y + frame.height) return false;
        if (factorModel.factor < 0.95) return false;
        if (variation < 0.2f * lineHeight && Math.abs(screenY - startY) < 0.2f * lineHeight) {
            int sel = (int)((frame.y + frame.height + pos - screenY) / lineHeight);
            if (sel == selectionIndex) return false;
            selectionIndex = sel;
            yioGdxGame.setSelectedLevelIndex(selectionIndex);
            selectionFactor.setStartConditions(0.1, 0);
            selectionFactor.beginFastestSpawnProcess();
            selectX = screenX;
            animRadius = Math.max((float)(selectX - frame.x), (float)(frame.x + frame.width - selectX));
            return true;
        }
        return false;
    }

    void increaseSelection() {
        selectionIndex++;
        limitSelection();
    }

    void limitSelection() {
        if (selectionIndex > YioGdxGame.INDEX_OF_LAST_LEVEL) selectionIndex = YioGdxGame.INDEX_OF_LAST_LEVEL;
    }

    public void touchDragged(int screenX, int screenY, int pointer) {
        if (!isVisible() || screenX < frame.x || screenX > frame.x + frame.width || screenY < frame.y || screenY > frame.y + frame.height) return;
        dragged = true;
        float dy = screenY - lastY;
        if (Math.abs(dy) >= 0.5f * Math.abs(speed)) speed = dy;
        else speed *= 0.5f;
        lastY = screenY;
        variation += Math.abs(dy);
    }

    public void setSelectionIndex(int selectionIndex) {
        this.selectionIndex = selectionIndex;
    }

    boolean isVisible() {
        return factorModel.factor() > 0.01;
    }
}
