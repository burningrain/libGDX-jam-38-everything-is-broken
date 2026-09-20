package com.github.br.libgdx.jam38.game.screens;

import com.github.br.libgdx.jam38.game.Resources;
import com.github.br.libgdx.jam38.structure.screen.statemachine.GameScreenState;

public interface Screens {

    GameScreenState MENU = new GameScreenState(new MenuScreen(
        Resources.SKIN, Resources.Tiled.MENU, true
    ), new MenuAssetLoader());
    GameScreenState GAME = new GameScreenState(new GameScreen(
        Resources.SKIN, Resources.Tiled.GAME, true
    ), new GameAssetLoader());

    GameScreenState LEVEL_1 = new GameScreenState(
        new GraphGameScreen("graphs/graph_1.json", GAME),
        new GraphGameAssetLoader()
    );

}
