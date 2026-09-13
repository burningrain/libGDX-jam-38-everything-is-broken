package com.github.br.libgdx.jam38.screens;

import com.github.br.libgdx.jam38.structure.screen.statemachine.GameScreenState;

public interface Screens {

    GameScreenState MENU = new GameScreenState(new MenuScreen(), new MenuAssetLoader());

}
