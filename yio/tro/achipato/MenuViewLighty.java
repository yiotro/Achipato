package yio.tro.achipato;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

/**
 * Created by ivan on 22.07.14.
 */
public class MenuViewLighty {
    YioGdxGame yioGdxGame;
    MenuControllerLighty menuControllerLighty;
    TextureRegion buttonPixel, shadowCorner, shadowSide, blackCircle, blackPixel;
    ShapeRenderer shapeRenderer;
    SpriteBatch batch;
    int cornerSize;
    float x1, y1, x2, y2; // local variables for rendering
    Color c; // local variable for rendering

    public MenuViewLighty(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        menuControllerLighty = yioGdxGame.menuControllerLighty;
        shapeRenderer = new ShapeRenderer();
        cornerSize = (int)(0.02 * Gdx.graphics.getHeight());
        buttonPixel = GameView.loadTextureRegionByName("button_pixel.png", false);
        shadowCorner = GameView.loadTextureRegionByName("corner_shadow.png", true);
        shadowSide = GameView.loadTextureRegionByName("side_shadow.png", true);
        blackCircle = GameView.loadTextureRegionByName("anim_circle_high_res.png", false);
        blackPixel = GameView.loadTextureRegionByName("black_pixel.png", false);
    }

    private void drawRoundRect(SimpleRectangle pos) {
        shapeRenderer.rect((float)pos.x + cornerSize, (float)pos.y, (float)pos.width - 2 * cornerSize, (float)pos.height);
        shapeRenderer.rect((float)pos.x, (float)pos.y + cornerSize, (float)pos.width, (float)pos.height - 2 * cornerSize);
        shapeRenderer.circle((float)pos.x + cornerSize, (float)pos.y + cornerSize, cornerSize);
        shapeRenderer.circle((float)pos.x + (float)pos.width - cornerSize, (float)pos.y + cornerSize, cornerSize);
        shapeRenderer.circle((float)pos.x + cornerSize, (float)pos.y + (float)pos.height - cornerSize, cornerSize);
        shapeRenderer.circle((float)pos.x + (float)pos.width - cornerSize, (float)pos.y + (float)pos.height - cornerSize, cornerSize);
    }

    private void drawRect(SimpleRectangle pos) {
        shapeRenderer.rect((float)pos.x, (float)pos.y, (float)pos.width, (float)pos.height);
    }

    private void renderShadow(ButtonLighty buttonLighty, SpriteBatch batch) {
        x1 = buttonLighty.x1;
        x2 = buttonLighty.x2;
        y1 = buttonLighty.y1;
        y2 = buttonLighty.y2;
        if (buttonLighty.factorModelLighty.factor <= 1)
            batch.setColor(c.r, c.g, c.b, (float) buttonLighty.factorModelLighty.factor());
        else batch.setColor(c.r, c.g, c.b, 1);
        batch.draw(shadowSide, x1 + cornerSize, y2 - cornerSize, 2 * (buttonLighty.hor - cornerSize), 2 * cornerSize);
        batch.draw(shadowSide, x1 + cornerSize, y1 + cornerSize, 0, 0, 2 * (buttonLighty.ver - cornerSize), 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowSide, x2 - cornerSize, y1 + cornerSize, 0, 0, 2 * (buttonLighty.hor - cornerSize), 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowSide, x2 - cornerSize, y2 - cornerSize, 0, 0, 2 * (buttonLighty.ver - cornerSize), 2 * cornerSize, 1, 1, 270);
        batch.draw(shadowCorner, x1 - cornerSize, y2 - cornerSize, 2 * cornerSize, 2 * cornerSize);
        batch.draw(shadowCorner, x1 + cornerSize, y1 - cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowCorner, x2 + cornerSize, y1 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowCorner, x2 - cornerSize, y2 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 270);
    }

    private void renderShadow(SimpleRectangle rectangle, float factor, SpriteBatch batch) {
        float hor = 0.5f * factor * (float)rectangle.width;
        float ver = 0.5f * factor * (float)rectangle.height;
        float cx = (float)rectangle.x + 0.5f * (float)rectangle.width;
        float cy = (float)rectangle.y + 0.5f * (float)rectangle.height;
        x1 = cx - hor;
        x2 = cx + hor;
        y1 = cy - ver;
        y2 = cy + ver;
        if (factor <= 1)
            batch.setColor(c.r, c.g, c.b, factor);
        else batch.setColor(c.r, c.g, c.b, 1);
        batch.draw(shadowSide, x1 + cornerSize, y2 - cornerSize, 2 * (hor - cornerSize), 2 * cornerSize);
        batch.draw(shadowSide, x1 + cornerSize, y1 + cornerSize, 0, 0, 2 * (ver - cornerSize), 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowSide, x2 - cornerSize, y1 + cornerSize, 0, 0, 2 * (hor - cornerSize), 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowSide, x2 - cornerSize, y2 - cornerSize, 0, 0, 2 * (ver - cornerSize), 2 * cornerSize, 1, 1, 270);
        batch.draw(shadowCorner, x1 - cornerSize, y2 - cornerSize, 2 * cornerSize, 2 * cornerSize);
        batch.draw(shadowCorner, x1 + cornerSize, y1 - cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowCorner, x2 + cornerSize, y1 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowCorner, x2 - cornerSize, y2 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 270);
    }

    boolean checkForSpecialMask(ButtonLighty buttonLighty) {
        switch (buttonLighty.id) {
            case 3:
                shapeRenderer.circle(buttonLighty.cx, buttonLighty.cy, (float)(0.8 + 0.2 * buttonLighty.selectionFactor.factor()) * buttonLighty.hor);
                return true;
        }
        return false;
    }

    boolean checkForSpecialAnimationMask(ButtonLighty buttonLighty) { // mask when circle fill animation on press
        SimpleRectangle pos = buttonLighty.animPos;
        switch (buttonLighty.id) {
            case 41: // main menu button
                shapeRenderer.rect((float)pos.x, (float)(pos.y + 0.5 * pos.height), (float)pos.width, 0.5f * (float)pos.height);
                return true;
            case 42: // resume button
                shapeRenderer.rect((float)pos.x, (float)pos.y, (float)pos.width, 0.5f * (float)pos.height);
                return true;
            case 43: // new game button
                shapeRenderer.rect((float)pos.x, (float)pos.y, (float)pos.width, (float)pos.height);
                return true;
            case 44: // restart button
                shapeRenderer.rect((float)pos.x, (float)pos.y, (float)pos.width, (float)pos.height);
                return true;
        }
        return false;
    }

    public void render() {
        ArrayList<ButtonLighty> buttons = menuControllerLighty.buttons;
        batch = yioGdxGame.batch;
        c = batch.getColor();

        //shadows
        batch.begin();
        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.isVisible() && !buttonLighty.currentlyTouched && buttonLighty.hasShadow && !buttonLighty.mandatoryShadow && buttonLighty.factorModelLighty.factor() > 0.1) {
                renderShadow(buttonLighty, batch);
            }
        }
        batch.end();

        // Drawing masks
        YioGdxGame.maskingBegin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.isVisible()) {
                if (checkForSpecialMask(buttonLighty)) continue;
                if (buttonLighty.rectangularMask && !buttonLighty.currentlyTouched) {
                    drawRect(buttonLighty.position);
                    continue;
                }
                drawRoundRect(buttonLighty.animPos);
            }
        }
        shapeRenderer.end();


        // Drawing buttons
        batch.begin();
        YioGdxGame.maskingContinue();
        SimpleRectangle ap;
        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.isVisible() && !buttonLighty.onlyShadow) {
                if (buttonLighty.mandatoryShadow) renderShadow(buttonLighty, batch);
                if (buttonLighty.factorModelLighty.factor <= 1)
                    batch.setColor(c.r, c.g, c.b, (float) buttonLighty.factorModelLighty.factor());
                else batch.setColor(c.r, c.g, c.b, 1);
                ap = buttonLighty.animPos;
                batch.draw(buttonLighty.textureRegion, (float)ap.x, (float)ap.y, (float)ap.width, (float)ap.height);
                if (buttonLighty.isCurrentlyTouched() && (!buttonLighty.touchAnimation || buttonLighty.selectionFactor.factor() > 0.99)) {
                    batch.setColor(c.r, c.g, c.b, 0.5f);
                    batch.draw(buttonPixel, (float)ap.x, (float)ap.y, (float)ap.width, (float)ap.height);
                    if (buttonLighty.touchAnimation && buttonLighty.lockAction) buttonLighty.lockAction = false;
                }
            }
        }
        batch.setColor(c.r, c.g, c.b, 1);
        batch.end();
        YioGdxGame.maskingEnd();

        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.isVisible() && buttonLighty.isCurrentlyTouched() && buttonLighty.touchAnimation && buttonLighty.selectionFactor.factor() <= 0.99) {
                YioGdxGame.maskingBegin();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                checkForSpecialAnimationMask(buttonLighty);
                drawRoundRect(buttonLighty.animPos);
                shapeRenderer.end();

                batch.begin();
                YioGdxGame.maskingContinue();
                batch.setColor(c.r, c.g, c.b, 0.5f);
                float r = (float)buttonLighty.selectionFactor.factor() * buttonLighty.animR;
                batch.draw(blackCircle, buttonLighty.touchX - r, buttonLighty.touchY - r, 2 * r, 2 * r);
                batch.end();
                batch.setColor(c.r, c.g, c.b, 1);
                YioGdxGame.maskingEnd();
            }
        }
    }

    public void renderScroller() {
        if (menuControllerLighty.scrollerLighty.isVisible()) renderScroller(menuControllerLighty.scrollerLighty);
    }

    void renderScroller(ScrollerLighty scrollerLighty) {
        batch = yioGdxGame.batch;
        c = batch.getColor();
        batch.begin();
        if (scrollerLighty.factorModel.factor() > 0.1) renderShadow(scrollerLighty.animFrame, (float)scrollerLighty.factorModel.factor, batch);
        batch.end();
        YioGdxGame.maskingBegin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawRoundRect(scrollerLighty.animFrame);
        shapeRenderer.end();
        batch.begin();
        YioGdxGame.maskingContinue();
        batch.draw(scrollerLighty.bg1, (float)scrollerLighty.animFrame.x, (float)scrollerLighty.animFrame.y, (float)scrollerLighty.animFrame.width, (float)scrollerLighty.animFrame.height);
        float y = (float)scrollerLighty.animFrame.y + (float)scrollerLighty.animFrame.height + scrollerLighty.pos;
        int index = 0;
        for (TextureRegion textureRegion : scrollerLighty.cache) {
            if (y <= scrollerLighty.animFrame.y + scrollerLighty.animFrame.height + scrollerLighty.lineHeight && y >= scrollerLighty.animFrame.y) {
                batch.draw(textureRegion, (float)scrollerLighty.frame.x, y - scrollerLighty.lineHeight, (float)scrollerLighty.frame.width, scrollerLighty.lineHeight);
                if (index == scrollerLighty.selectionIndex && scrollerLighty.selectionFactor.factor() > 0.99) {
                    batch.setColor(c.r, c.g, c.b, 0.5f);
                    batch.draw(buttonPixel, (float)scrollerLighty.frame.x, y - scrollerLighty.lineHeight, (float)scrollerLighty.frame.width, scrollerLighty.lineHeight);
                    batch.setColor(c.r, c.g, c.b, 1);
                }
            }
            y -= scrollerLighty.lineHeight;
            index++;
        }
        batch.end();
        batch.setColor(c.r, c.g, c.b, 1);
        if (scrollerLighty.selectionFactor.factor() <= 0.99) {
            y = (float)scrollerLighty.animFrame.y + (float)scrollerLighty.animFrame.height + scrollerLighty.pos - scrollerLighty.selectionIndex * scrollerLighty.lineHeight;
            if (y > scrollerLighty.animFrame.y + scrollerLighty.animFrame.height + scrollerLighty.lineHeight && y < scrollerLighty.animFrame.y) return;
            YioGdxGame.maskingBegin();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            float ay = y - scrollerLighty.lineHeight;
            float ah = scrollerLighty.lineHeight;
            if (ay < scrollerLighty.frame.y) {
                float d = (float)scrollerLighty.frame.y - ay;
                ay += d;
                ah -= d;
            } else if (ay + ah > scrollerLighty.frame.y + scrollerLighty.frame.height) {
                float d = (float)(y + scrollerLighty.lineHeight - scrollerLighty.frame.y - scrollerLighty.frame.height);
                ah -= d - scrollerLighty.lineHeight;
            }
            shapeRenderer.rect((float) scrollerLighty.frame.x, ay, (float) scrollerLighty.frame.width, ah);
            shapeRenderer.end();
            batch.begin();
            batch.setColor(c.r, c.g, c.b, 0.5f);
            YioGdxGame.maskingContinue();
            float cx = scrollerLighty.selectX;
            float cy = (float)(y - 0.5 * scrollerLighty.lineHeight);
            float dw = 1.1f * (float)scrollerLighty.selectionFactor.factor() * scrollerLighty.animRadius;
            batch.draw(blackCircle, cx - dw, cy - dw, 2 * dw, 2 * dw);
            batch.end();
            batch.setColor(c.r, c.g, c.b, 1);
        }
        YioGdxGame.maskingEnd();
    }
}
