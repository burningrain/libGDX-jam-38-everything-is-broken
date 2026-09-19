package com.github.br.libgdx.jam38.game.screens.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;
import com.github.br.libgdx.jam38.game.model.GameVertexState;
import com.github.br.libgdx.jam38.game.model.vertex.GameVertex;
import com.github.br.libgdx.jam38.structure.ui.AnimatedImage;

public class UiEdge extends Image {

    // модель
    private final UiNode from;
    private final UiNode to;

    private final AnimatedImage emitEdge;
    private final AnimatedImage emptyEdge;
    private final AnimatedImage freezeEdge;
    private final AnimatedImage targetEdge;

    // Временный вектор для избежания аллокаций памяти в методе draw
    private final Vector2 direction = new Vector2();

    public UiEdge(
        UiNode from,
        UiNode to,
        AnimatedImage emitEdge,
        AnimatedImage emptyEdge,
        AnimatedImage freezeEdge,
        AnimatedImage targetEdge
    ) {
        super(emptyEdge.getDrawable());
        this.setSize(this.getWidth(), this.getHeight());
        this.setOrigin(this.getWidth() / 2f, this.getHeight() / 2f);

        this.from = from;
        this.to = to;

        this.emitEdge = emitEdge;
        this.emptyEdge = emptyEdge;
        this.freezeEdge = freezeEdge;
        this.targetEdge = targetEdge;

        emitEdge.setFrameAndPause(0);
        emptyEdge.setFrameAndPause(0);
        freezeEdge.setFrameAndPause(0);
        targetEdge.setFrameAndPause(0);

        AnimatedImage edge = getEdgeType(from, to);
        edge.setLooping(true);
        edge.setPlayMode(Animation.PlayMode.LOOP_PINGPONG); // слева-направо
        edge.play();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        AnimatedImage edge = getEdgeType(from, to);

        // 1. Динамически рассчитываем трансформацию ребра на основе позиций вершин
        float toX = to.getX() + to.getOriginX();
        float toY = to.getY() + to.getOriginY();
        float fromX = from.getX() + from.getOriginX();
        float fromY = from.getY() + from.getOriginY();
        direction.set(toX, toY).sub(fromX, fromY);
        float distance = direction.len();
        float angle = direction.angleDeg(); // Угол наклона в градусах

        // Выставляем размеры и позицию самому UiEdge, чтобы Scene2D знал его актуальные границы
        float edgeHeight = emptyEdge.getHeight() > 0 ? emptyEdge.getHeight() : getHeight();
        this.setPosition(fromX, fromY - edgeHeight / 2f); // Центрируем по оси Y
        this.setSize(distance, edgeHeight);
        this.setOrigin(0, edgeHeight / 2f); // Точка поворота — строго начало ребра (от ноды From)
        this.setRotation(angle);

        // 2. Получаем параметры трансформации
        float x = getX();
        float y = getY();
        float width = getWidth();
        float height = getHeight();
        float scaleX = getScaleX();
        float scaleY = getScaleY();
        float rotation = getRotation();
        float originX = getOriginX();
        float originY = getOriginY();

        // Настраиваем цвет батча под параметры родителя
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        // 3. Отрисовываем активную анимацию ребра с учетом всех трансформаций
        drawChild(batch, edge, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
    }

    // Помощник для отрисовки вложенных изображений с учетом трансформации родителя (как в UiNode)
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

    private AnimatedImage getEdgeType(UiNode uiFrom, UiNode uiTo) {
        GameVertex from = uiFrom.getModel();
        GameVertex to = uiTo.getModel();
        if (from.isFreeze() || to.isFreeze()) {
            return freezeEdge;
        }
        if (GameVertexState.EMITTER == from.getState() || GameVertexState.EMITTER == to.getState()) {
            return emitEdge;
        }
        if (GameVertexState.TARGET == to.getState() && GameVertexState.EMITTER != from.getState()) {
            return targetEdge;
        } else {
            return emptyEdge;
        }
    }

}
