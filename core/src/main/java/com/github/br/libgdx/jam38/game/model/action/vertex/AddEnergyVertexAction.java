package com.github.br.libgdx.jam38.game.model.action.vertex;

import com.github.br.libgdx.jam38.game.model.action.GameVertexAction;
import com.github.br.libgdx.jam38.game.model.vertex.GameVertex;

public class AddEnergyVertexAction extends GameVertexAction {

    public AddEnergyVertexAction(String target) {
        super(target);
    }

    @Override
    public void execute(GameVertex gameVertex) {
        gameVertex.addEnergy(10);
    }

    @Override
    public int getTimeCost() {
        return 10;
    }

}
