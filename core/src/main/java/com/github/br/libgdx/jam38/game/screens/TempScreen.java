package com.github.br.libgdx.jam38.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.br.libgdx.jam38.game.Resources;
import com.github.br.libgdx.jam38.game.model.Game;
import com.github.br.libgdx.jam38.game.model.GameModelApplicationContext;
import com.github.br.libgdx.jam38.game.model.GameGraph;
import com.github.br.libgdx.jam38.game.model.action.edge.GameEdge;
import com.github.br.libgdx.jam38.game.model.vertex.GameVertex;
import com.github.br.libgdx.jam38.game.screens.ui.UiEdge;
import com.github.br.libgdx.jam38.game.screens.ui.UiNode;
import com.github.br.libgdx.jam38.game.screens.ui.UiObjectFactory;
import com.github.br.libgdx.jam38.structure.GameSettings;
import com.github.br.libgdx.jam38.structure.screen.AbstractGameScreen;

public class TempScreen extends AbstractGameScreen {

    private GameModelApplicationContext applicationContext;
    private UiObjectFactory uiObjectFactory;

    protected OrthographicCamera camera;
    protected Viewport viewport;

    protected OrthographicCamera backgroundCamera;
    protected Viewport backgroundViewport;

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;

    private ObjectMap<String, UiNode> nodesMap = new ObjectMap<>();
    private Array<UiNode> nodes;
    private Array<UiEdge> edges;

    private Texture background;

    private float accumulator = 0f;
    private float stepTime = 1 / 2.5f; // число шагов эмуляции в секунду

    private Stage stage;
    private ClickListener nodeClickListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            Actor target = event.getTarget();
            UiNode uiNode = (UiNode) target;
            if (uiNode.isSelected()) {
                uiNode.deselect();
            } else {
                uiNode.select();
            }
        }
    };

    @Override
    public void show() {
        AssetManager assetManager = getGameManager().assetManager;
        uiObjectFactory = new UiObjectFactory(assetManager);
        background = assetManager.get(Resources.GraphGame.BACKGROUND, Texture.class);

        GameSettings gameSettings = getGameManager().gameSettings;
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(
            gameSettings.getVirtualScreenWidth(),
            gameSettings.getVirtualScreenHeight(),
            camera
        );
        viewport.apply(true);

        // --- Инициализация вьюпорта для фона ---
        backgroundCamera = new OrthographicCamera();
        // Инициализируем под физический размер самой текстуры фона (2400x1080)
        // Scaling.fill растянет этот виртуальный экран так, чтобы он заполнил монитор без черных полос, обрезав лишнее
        backgroundViewport = new ScalingViewport(
            Scaling.stretch,
            gameSettings.getVirtualScreenWidth(),
            gameSettings.getVirtualScreenHeight(),
            backgroundCamera
        );
        backgroundViewport.apply(true);

        spriteBatch = new SpriteBatch();
        stage = new Stage(viewport, spriteBatch);
        shapeRenderer = new ShapeRenderer();

        applicationContext = new GameModelApplicationContext();

        Game game = applicationContext.getGame();
        game.loadGraph("graphs/graph_1.json");
        createGraphUi(game.getGameGraph());

        Gdx.input.setInputProcessor(stage);
    }

    private void handleLogic(float delta) {
        Game game = applicationContext.getGame();

        accumulator += delta;
        while (accumulator >= stepTime) {
            accumulator =- stepTime;
            game.tick(stepTime);
        }
    }

    @Override
    public void render(float delta) {
        handleLogic(delta);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // ---  Рисуем фон через его собственный вьюпорт ---
        backgroundCamera.update();
        backgroundViewport.apply(true);
        spriteBatch.setProjectionMatrix(backgroundCamera.combined);
        spriteBatch.begin();
        // Так как размеры вьюпорта равны размерам текстуры, просто рисуем её с нуля
        spriteBatch.draw(background, 0, 0);
        spriteBatch.end();

        // --- Рисуем игру (ноды, ребра, частицы) через ExtendViewport ---
        camera.update();
        viewport.apply(true);
        spriteBatch.setProjectionMatrix(camera.combined);

        stage.act();
        stage.draw();

        // отрисовка энергии
        shapeRenderer.setProjectionMatrix(spriteBatch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(spriteBatch.getTransformMatrix());
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (UiNode node : nodes) {
            node.drawEnergy(shapeRenderer, 1f);
        }
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        backgroundViewport.update(width, height, true);
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

    private void createGraphUi(GameGraph gameGraph) {
        nodes = new Array<>(gameGraph.getVertices().size);
        for (GameVertex vertex : gameGraph.getVertices().values()) {
            UiNode node = uiObjectFactory.createUiNode(vertex);
            node.addListener(nodeClickListener);

            Vector2 position = vertex.getPosition();
            node.setPosition(position.x, position.y);
            nodes.add(node);
            nodesMap.put(node.getModel().getName(), node);
        }

        Array<GameEdge> edges = gameGraph.getEdges();
        this.edges = new Array<>(edges.size);
        for (GameEdge edge : edges) {
            UiEdge uiEdge = uiObjectFactory.createUiEdge(
                nodesMap.get(edge.getFrom().getName()),
                nodesMap.get(edge.getTo().getName())
            );
            this.edges.add(uiEdge);
            stage.addActor(uiEdge);
        }

        for (UiNode node : nodes) {
            stage.addActor(node);
        }

    }
}
