package com.github.br.libgdx.jam38.game.model.action.vertex;

import com.github.br.libgdx.jam38.game.model.action.GameVertexAction;
import com.github.br.libgdx.jam38.game.model.vertex.GameVertex;

public class ApplyTimeStoneToVertexAction extends GameVertexAction {

    public ApplyTimeStoneToVertexAction(String target) {
        super(target);
    }

    @Override
    public void execute(GameVertex gameVertex) {
        if (gameVertex.isFreeze()) {
            gameVertex.setFreeze(false);
        } else {
            gameVertex.addEnergy(10);
        }
    }

    @Override
    public int getTimeCost() {
        return 10;
    }

}
