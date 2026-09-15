package com.github.br.libgdx.jam38.game.model.vertex;

import com.badlogic.gdx.utils.Array;
import com.github.br.libgdx.jam38.game.model.GameVertexState;
import com.github.br.libgdx.jam38.game.model.VertexDelta;

public interface GameVertex {

    class AddedEnergy {
        private final GameVertex neighbour;
        private final float addedEnergy;

        public AddedEnergy(GameVertex neighbour, float addedEnergy) {
            this.neighbour = neighbour;
            this.addedEnergy = addedEnergy;
        }
    }

    Array<GameVertex> getNeighbours();

    void addNeighbour(GameVertex to);

    void removeNeighbour(GameVertex to);

    void setState(GameVertexState gameVertexState);

    GameVertexState getState();

    void calculateCurrent();

    VertexDelta calculateFinal();

    String getName();

    float getEnergy();

    void addEnergy(float addedEnergy);

    void changeEnergy(float diff);

}
