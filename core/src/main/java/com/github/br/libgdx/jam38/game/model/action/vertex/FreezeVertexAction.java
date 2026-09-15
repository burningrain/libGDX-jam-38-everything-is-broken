package com.github.br.libgdx.jam38.game.model.action.vertex;

import com.github.br.libgdx.jam38.game.model.action.GameVertexAction;
import com.github.br.libgdx.jam38.game.model.vertex.GameVertex;
import com.github.br.libgdx.jam38.game.model.GameVertexState;

public class FreezeVertexAction extends GameVertexAction {

    public FreezeVertexAction(String target) {
        super(target);
    }

    @Override
    public void execute(GameVertex gameVertex) {
        gameVertex.setState(GameVertexState.FREEZE);
    }

    @Override
    public int getTimeCost() {
        return 0;
    }

}
