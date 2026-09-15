package com.github.br.libgdx.jam38.structure.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class AnimatedDrawable extends TextureRegionDrawable {

    private final Animation<TextureRegion> animation;
    private TextureRegion keyFrame;

    private float stateTime = 0;
    private boolean isPaused = false;

    public AnimatedDrawable(Animation<TextureRegion> animation) {
        this.animation = animation;
        TextureRegion key = animation.getKeyFrame(0);

        this.setLeftWidth(key.getRegionWidth() / 2f);
        this.setRightWidth(key.getRegionWidth() / 2f);
        this.setTopHeight(key.getRegionHeight() / 2f);
        this.setBottomHeight(key.getRegionHeight() / 2f);
        this.setMinWidth(key.getRegionWidth());
        this.setMinHeight(key.getRegionHeight());
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        if (!isPaused) {
            stateTime += Gdx.graphics.getDeltaTime();
        }

        keyFrame = animation.getKeyFrame(stateTime, false);
        setRegion(keyFrame);

        super.draw(batch, x, y, width, height);
    }

    @Override
    public void draw(
        Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX,
        float scaleY, float rotation
    ) {
        if (!isPaused) {
            stateTime += Gdx.graphics.getDeltaTime();
        }

        keyFrame = animation.getKeyFrame(stateTime, true);
        setRegion(keyFrame);

        super.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
    }

    public boolean isAnimationEnd() {
        return animation.isAnimationFinished(stateTime);
    }

    public void play() {
        isPaused = false;
    }

    public void pause() {
        isPaused = true;
    }

    public void resetAndPause() {
        pause();
        stateTime = 0;
    }

    public void setFrameAndPause(int frameIndex) {
        TextureRegion keyFrame = animation.getKeyFrames()[frameIndex];
        setRegion(keyFrame);

        stateTime = frameIndex * animation.getFrameDuration();
        pause();
    }
}
