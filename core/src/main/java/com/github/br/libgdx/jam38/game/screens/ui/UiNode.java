package com.github.br.libgdx.jam38.game.screens.ui;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;
import com.github.br.libgdx.jam38.game.model.GameVertexState;
import com.github.br.libgdx.jam38.game.model.vertex.GameVertex;
import com.github.br.libgdx.jam38.structure.ui.AnimatedImage;

public class UiNode extends Image {

    public enum FreezeAnimState {
        TO_FREEZE, TO_UNFREEZE, FREEZE, UNFREEZE
    }

    // константы
    private final GameVertex gameVertex; // модель

    private final AnimatedImage emitFrom;
    private final AnimatedImage emitToFromTime;
    private final AnimatedImage emitTo;
    private final AnimatedImage selectedNode;
    private final AnimatedImage freezeNode;
    private final AnimatedImage emitterNode;
    private final AnimatedImage emptyNode;
    private final AnimatedImage targetNode;
    private final TextureAtlas.AtlasRegion icon;

    // переменные
    private GameVertexState prevState;
    private boolean prevIsFreeze;
    private FreezeAnimState freezeAnimState = FreezeAnimState.UNFREEZE;

    private boolean isSelected = false;

    public UiNode(
        GameVertex gameVertex,
        TextureAtlas.AtlasRegion icon,
        AnimatedImage emitFrom,
        AnimatedImage emitToFromTime,
        AnimatedImage emitTo,
        AnimatedImage selectedNode,
        AnimatedImage freezeNode,
        AnimatedImage emitterNode,
        AnimatedImage emptyNode,
        AnimatedImage targetNode
    ) {
        super(emptyNode.getDrawable());
        this.setSize(this.getWidth(), this.getHeight());
        this.setOrigin(this.getWidth() / 2f, this.getHeight() / 2f);

        this.gameVertex = gameVertex;
        this.icon = icon;
        this.emitFrom = emitFrom;
        this.emitToFromTime = emitToFromTime;
        this.emitTo = emitTo;
        this.selectedNode = selectedNode;
        this.freezeNode = freezeNode;
        this.emitterNode = emitterNode;
        this.emptyNode = emptyNode;
        this.targetNode = targetNode;

        this.prevIsFreeze = false;
        this.prevState = null;

        emitFrom.setFrameAndPause(0);
        emitToFromTime.setFrameAndPause(0);
        emitTo.setFrameAndPause(0);
        selectedNode.setFrameAndPause(0);
        freezeNode.setFrameAndPause(0);
        emitterNode.setFrameAndPause(0);
        emptyNode.setFrameAndPause(0);
        targetNode.setFrameAndPause(0);

        AnimatedImage nodeImage = getNodeImage(gameVertex.getState());
        nodeImage.play();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float diffEnergy = gameVertex.getDiffEnergy();
        GameVertexState newState = gameVertex.getState();
        if (newState != prevState) {
            // переход целевой ноды в дефолтовую
            if (!prevIsFreeze && gameVertex.isFreeze()) { // однократное переключение
                // заморозка ноды
                freezeNode.resetAndPause();
                freezeNode.setLooping(false);
                freezeNode.setPlayMode(Animation.PlayMode.REVERSED);
                freezeNode.play();

                freezeAnimState = FreezeAnimState.TO_FREEZE;
            } else if (prevIsFreeze && !gameVertex.isFreeze()) { // однократное переключение
                // разморозка ноды
                emitToFromTime.resetAndPause();
                freezeNode.setLooping(false);
                emitToFromTime.play();

                freezeNode.setLastKeyFrameAndPause();
                freezeNode.setPlayMode(Animation.PlayMode.NORMAL);
                freezeNode.play();
                freezeAnimState = FreezeAnimState.TO_UNFREEZE;
            }
        }

        if (diffEnergy < 0) { // многократное переключение
            // была отдача энергии
            if (emitFrom.isPaused()) { // иначе будет перетираться уже играющая анимация?
                emitFrom.setFrameAndPause(0);
                emitFrom.play();
            }
        } else if (diffEnergy > 0) { // многократное переключение
            // было получение энергии
            if (emitTo.isPaused()) {
                emitTo.resetAndPause();
                emitTo.play();
            }
        }

        // текущая нода: целевая, дефолтовая, эмиттер
        AnimatedImage nodeImage = getNodeImage(newState);

        // ОТРИСОВКА
        // Получаем актуальные параметры текущей ноды UiNode
        float x = getX();
        float y = getY();
        float width = getWidth();
        float height = getHeight();
        float scaleX = getScaleX();
        float scaleY = getScaleY();
        float rotation = getRotation();
        float originX = getOriginX();
        float originY = getOriginY();

        // Настраиваем цвет и альфу родителя для батча
        com.badlogic.gdx.graphics.Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        drawChild(batch, nodeImage, x, y, originX, originY, width, height, scaleX, scaleY, rotation);

        // приход или уход энергии в/из ноды
        // уход из ноды
        if (!emitFrom.isPaused()) {
            drawChild(batch, emitFrom, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
            if (emitFrom.isAnimationEnd()) {
                emitFrom.resetAndPause();
                if (GameVertexState.EMITTER == newState) {
                    emitFrom.play();
                }
            }
        }
        // приход энергии в ноду из другой ноды
        if (!emitTo.isPaused()) {
            drawChild(batch, emitTo, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
            if (emitTo.isAnimationEnd()) {
                emitTo.resetAndPause();
                if (diffEnergy > 0) {
                    emitTo.play();
                }
            }
        }
        // приход энергии из камня времени при заморозке
        if (!emitToFromTime.isPaused()) {
            drawChild(batch, emitToFromTime, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
            if (emitToFromTime.isAnimationEnd()) {
                emitToFromTime.resetAndPause();
            }
        }

        // заморозка вкл/выкл?
        if (FreezeAnimState.UNFREEZE != freezeAnimState) {
            if (FreezeAnimState.FREEZE == freezeAnimState) {
                drawChild(batch, freezeNode, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
            }
            if (!freezeNode.isPaused()) {
                drawChild(batch, freezeNode, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
                if (freezeNode.isAnimationEnd()) {
                    if (FreezeAnimState.TO_FREEZE == freezeAnimState) {
                        freezeAnimState = FreezeAnimState.FREEZE;
                        freezeNode.pause();
                    } else if (FreezeAnimState.TO_UNFREEZE == freezeAnimState) {
                        freezeAnimState = FreezeAnimState.UNFREEZE;
                        freezeNode.pause();
                    }
                }
            }
        }

        // нода выбрана игроком или не выбрана?
        if (isSelected) {
            drawChild(batch, selectedNode, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
        }

        float iconX = (width - icon.getRegionWidth()) / 2f;
        float iconY = (height - icon.getRegionHeight()) / 2f;
        batch.draw(
            icon,
            x + iconX, y + iconY,
            originX - iconX, originY - iconY,
            icon.getRegionWidth(), icon.getRegionHeight(),
            scaleX, scaleY,
            rotation
        );

        prevState = newState;
        prevIsFreeze = gameVertex.isFreeze();
    }

    private AnimatedImage getNodeImage(GameVertexState newState) {
        return switch (newState) {
            case TARGET -> targetNode;
            case NONE -> emptyNode;
            case EMITTER -> emitterNode;
        };
    }

    // Помощник для отрисовки вложенных изображений с учетом трансформации родителя
    private void drawChild(
        Batch batch,
        AnimatedImage child,
        float x,
        float y,
        float originX,
        float originY,
        float w,
        float h,
        float scaleX,
        float scaleY,
        float rotation
    ) {
        Drawable drawable = child.getDrawable();
        if (drawable == null) return;

        if (scaleX != 1 || scaleY != 1 || rotation != 0) {
            if (drawable instanceof TransformDrawable) {
                ((TransformDrawable) drawable).draw(
                    batch, x, y, originX, originY, w, h, scaleX, scaleY, rotation
                );
                return;
            }
        }
        drawable.draw(batch, x, y, w * scaleX, h * scaleY);
    }

    public void select() {
        if (isSelected) {
            return;
        }

        selectedNode.setLooping(true);
        selectedNode.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        selectedNode.setFrameAndPause(0);
        selectedNode.play();

        this.isSelected = true;

    }

    public void deselect() {
        this.isSelected = false;
    }

}
