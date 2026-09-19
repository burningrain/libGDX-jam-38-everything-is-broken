package com.github.br.libgdx.jam38.game.model.vertex;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.github.br.libgdx.jam38.game.model.GameVertexState;
import com.github.br.libgdx.jam38.game.model.VertexDelta;

public class CalculateVertexProxy implements GameVertex {

    private final GameVertexData target;
    private final Array<GameVertex> neighbours = new Array<>();

    private float outEnergy = 0f;
    private float inEnergy = 0f;
    private float diffEnergy = 0f;
    private Array<AddedEnergy> outEnergyArray = null;

    public CalculateVertexProxy(GameVertexData target) {
        this.target = target;
    }

    @Override
    public void calculateCurrent() {
        if (GameVertexState.EMITTER == target.getState()) {
            outEnergy = target.getEnergy() * target.getEmissionPercent();
            Array<GameVertex> neighbours = getNeighbours();

            float sum = 0f;
            for (GameVertex neighbour : neighbours) {
                sum += neighbour.getEnergy();
            }

            outEnergyArray = new Array<>(neighbours.size);
            for (GameVertex neighbour : neighbours) {
                float addedEnergy = outEnergy * neighbour.getEnergy() / sum;
                neighbour.addEnergy(addedEnergy);
                outEnergyArray.add(new AddedEnergy(neighbour, addedEnergy));
            }
        }
    }

    @Override
    public VertexDelta calculateFinal() {
        diffEnergy = inEnergy - outEnergy;
        target.changeEnergy(diffEnergy);

        VertexDelta vertexDelta = new VertexDelta(target, diffEnergy, inEnergy, outEnergy, outEnergyArray);
        inEnergy = 0f;
        outEnergy = 0f;
        outEnergyArray = null;

        return vertexDelta;
    }

    @Override
    public Array<GameVertex> getNeighbours() {
        return neighbours;
    }

    @Override
    public void addNeighbour(GameVertex to) {
        neighbours.add(to);
    }

    @Override
    public void removeNeighbour(GameVertex to) {
        neighbours.removeValue(to, true);
    }

    @Override
    public void setState(GameVertexState gameVertexState) {
        target.setState(gameVertexState);
    }

    @Override
    public GameVertexState getState() {
        return target.getState();
    }

    @Override
    public String getName() {
        return target.getName();
    }

    @Override
    public float getEnergy() {
        return target.getEnergy();
    }

    @Override
    public void addEnergy(float addedEnergy) {
        inEnergy += addedEnergy;
    }

    @Override
    public void changeEnergy(float diff) {
        target.changeEnergy(diff);
    }

    @Override
    public Vector2 getPosition() {
        return target.getPosition();
    }

    @Override
    public float getDiffEnergy() {
        return diffEnergy;
    }

    @Override
    public boolean isFreeze() {
        return target.isFreeze();
    }

    @Override
    public void setFreeze(boolean isFreeze) {
        target.setFreeze(isFreeze);
    }

}
