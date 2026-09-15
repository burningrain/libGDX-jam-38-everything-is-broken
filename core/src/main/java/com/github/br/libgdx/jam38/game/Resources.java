package com.github.br.libgdx.jam38.game;

public interface Resources {

    String SKIN_ATLAS = "skin/export/export.atlas";
    String SKIN = "skin/export/export.json";

    interface Tiled {
        String MENU = "tiled-packed/menu.tmx";
        String GAME = "tiled-packed/game.tmx";
    }

    String ANIMATION_ATLAS = "animation/jam38_animation.atlas";

    interface Animation {


    }

    interface Particles {
        String NODE = "particles/node.p";
        String EDGE = "particles/edge.p";
    }

}
