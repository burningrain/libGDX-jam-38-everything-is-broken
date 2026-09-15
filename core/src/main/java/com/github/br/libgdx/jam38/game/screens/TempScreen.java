package com.github.br.libgdx.jam38.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.br.libgdx.jam38.game.Resources;
import com.github.br.libgdx.jam38.game.model.Game;
import com.github.br.libgdx.jam38.game.model.GameApplicationContext;
import com.github.br.libgdx.jam38.game.model.GameGraph;
import com.github.br.libgdx.jam38.game.model.action.edge.GameEdge;
import com.github.br.libgdx.jam38.game.model.vertex.GameVertex;
import com.github.br.libgdx.jam38.structure.GameSettings;
import com.github.br.libgdx.jam38.structure.screen.AbstractGameScreen;

public class TempScreen extends AbstractGameScreen {

    private GameApplicationContext applicationContext;

    protected OrthographicCamera camera;
    protected Viewport viewport;

    private SpriteBatch spriteBatch;

    private Array<ParticleEffect> nodes;
    private Array<ParticleEffect> edges;

    @Override
    public void show() {
        GameSettings gameSettings = getGameManager().gameSettings;
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(
            gameSettings.getVirtualScreenWidth(),
            gameSettings.getVirtualScreenHeight(),
            camera
        );
        viewport.apply(true);
        spriteBatch = new SpriteBatch();

        applicationContext = new GameApplicationContext();

        Game game = applicationContext.getGame();
        game.loadGraph("graphs/graph_1.json");
        createGraphUi(game.getGameGraph());

        //Gdx.input.setInputProcessor();
    }

    private void createGraphUi(GameGraph gameGraph) {
        AssetManager assetManager = getGameManager().assetManager;

        nodes = new Array<>(gameGraph.getVertices().size);
        for (GameVertex vertex : gameGraph.getVertices().values()) {
            ParticleEffect node = new ParticleEffect(assetManager.get(Resources.Particles.NODE, ParticleEffect.class));

            Vector2 position = vertex.getPosition();
            node.setPosition(position.x, position.y);

            nodes.add(node);
            node.start();
        }

        edges = new Array<>(gameGraph.getEdges().size);
        for (GameEdge graphEdge : gameGraph.getEdges()) {
            ParticleEffect edge = new ParticleEffect(assetManager.get(Resources.Particles.EDGE, ParticleEffect.class));

            GameVertex from = graphEdge.getFrom();
            GameVertex to = graphEdge.getTo();

            Vector2 fromPos = from.getPosition();
            Vector2 toPos = to.getPosition();

            float nodeRadius = 30f;
            // Создаем вектор направления от From к To
            Vector2 direction = new Vector2(toPos).sub(fromPos).nor();

            // Сдвигаем начальную точку ребра на величину радиуса вдоль направления
            Vector2 edgeStartPos = new Vector2(fromPos).add(new Vector2(direction).scl(nodeRadius));

            // Новая точка старта эмиттера ребра
            edge.setPosition(edgeStartPos.x, edgeStartPos.y);

            // Если вы хотите, чтобы ребро и НЕ ДОЛЕТАЛО до центра второго узла,
            // уменьшите расчетную дистанцию для скорости:
            float distance = fromPos.dst(toPos) - (nodeRadius * 2);
            float angle = MathUtils.atan2(toPos.y - fromPos.y, toPos.x - fromPos.x) * MathUtils.radiansToDegrees;

            // 3. Поворачиваем каждый эмиттер внутри этого ParticleEffect
            for (com.badlogic.gdx.graphics.g2d.ParticleEmitter emitter : edge.getEmitters()) {

                // Поворачиваем направление полета частиц (Angle)
                float angleHighMin = emitter.getAngle().getHighMin();
                float angleHighMax = emitter.getAngle().getHighMax();
                // Смещаем относительно базового угла в файле .p
                emitter.getAngle().setHigh(angleHighMin + angle, angleHighMax + angle);

                // Поворачиваем спрайты частиц (Rotation), если нужно, чтобы они смотрели вдоль ребра
                float rotHighMin = emitter.getRotation().getHighMin();
                float rotHighMax = emitter.getRotation().getHighMax();
                emitter.getRotation().setHigh(rotHighMin + angle, rotHighMax + angle);

                // 4. Подгоняем скорость (Velocity) под длину ребра
                // Формула: Скорость = Расстояние / Время жизни (в секундах)
                // В файле edge.p highMin/highMax жизненного цикла равен 6000.0 мс = 6.0 секунд.
                float lifeTimeSeconds = emitter.getLife().getHighMax() / 1000f;
                float requiredVelocity = distance / lifeTimeSeconds;
                emitter.getVelocity().setHigh(requiredVelocity);


                // Задаем базовую плотность. Например: 1 условная частица на каждые 5 пикселей длины.
                float baseDensity = 20f;
                // Рассчитываем, сколько всего частиц должно быть на таком расстоянии
                float totalParticlesForDistance = distance / baseDensity;
                // Emission — это сколько частиц спавнится В СЕКУНДУ. Делим общее число на время жизни:
                float emissionPerSecond = totalParticlesForDistance / lifeTimeSeconds;

                emitter.getEmission().setHigh(emissionPerSecond);

                // Важно: увеличиваем максимальное количество частиц (Max Count),
                // чтобы лимит пула не обрезал наш поток на длинных дистанциях
                int maxCount = MathUtils.ceil(totalParticlesForDistance * 1.5f); // с запасом
                emitter.setMaxParticleCount(maxCount);
            }

            edges.add(edge);
            edge.start();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        spriteBatch.setProjectionMatrix(camera.projection);
        spriteBatch.setTransformMatrix(camera.view);

        spriteBatch.begin();

        for (int i = 0; i < nodes.size; i++) {
            nodes.get(i).draw(spriteBatch, delta);
        }

        for (int j = 0; j < edges.size; j++) {
            edges.get(j).draw(spriteBatch, delta);
        }

        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
