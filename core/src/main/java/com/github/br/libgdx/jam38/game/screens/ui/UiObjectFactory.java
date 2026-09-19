package com.github.br.libgdx.jam38.game.screens.ui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.github.br.libgdx.jam38.game.Resources;
import com.github.br.libgdx.jam38.game.model.vertex.GameVertex;
import com.github.br.libgdx.jam38.structure.ui.AnimatedImage;

public class UiObjectFactory {

    private final AssetManager assetManager;

    private final TextureAtlas textureAtlas;
    private final Array<TextureAtlas.AtlasRegion> targetNode;
    private final Array<TextureAtlas.AtlasRegion> emitterNode;
    private final Array<TextureAtlas.AtlasRegion> emptyNode;
    private final Array<TextureAtlas.AtlasRegion> freezeNode;
    private final Array<TextureAtlas.AtlasRegion> selectedNode;
    private final Array<TextureAtlas.AtlasRegion> emitFrom;
    private final Array<TextureAtlas.AtlasRegion> emitTo;
    private final Array<TextureAtlas.AtlasRegion> emitToFromTime;

    public UiObjectFactory(AssetManager assetManager) {
        this.assetManager = assetManager;

        textureAtlas = assetManager.get(Resources.GRAPH_GAME_ATLAS, TextureAtlas.class);
        targetNode = textureAtlas.findRegions(Resources.GraphGame.Node.TARGET_NODE);
        emitterNode = textureAtlas.findRegions(Resources.GraphGame.Node.EMITTER_NODE);
        emptyNode = textureAtlas.findRegions(Resources.GraphGame.Node.EMPTY_NODE);
        freezeNode = textureAtlas.findRegions(Resources.GraphGame.Node.FREEZE_NODE);
        selectedNode = textureAtlas.findRegions(Resources.GraphGame.Node.SELECTED_EFFECT);

        emitFrom = textureAtlas.findRegions(Resources.GraphGame.EmitterEffect.EMITTER_FROM);
        emitTo = textureAtlas.findRegions(Resources.GraphGame.EmitterEffect.EMITTER_TO);
        emitToFromTime = textureAtlas.findRegions(Resources.GraphGame.EmitterEffect.EMITTER_FROM_TIME);
    }


    public UiNode createUiNode(GameVertex vertex) {
        TextureAtlas.AtlasRegion icon = textureAtlas.findRegion(vertex.getName());

        return new UiNode(
            vertex,
            icon, // Texture
            createAnimatedImage(emitFrom), // AnimatedImage
            createAnimatedImage(emitToFromTime), // AnimatedImage
            createAnimatedImage(emitTo), // AnimatedImage
            createAnimatedImage(selectedNode), // AnimatedImage
            createAnimatedImage(freezeNode), // AnimatedImage
            createAnimatedImage(emitterNode), // AnimatedImage
            createAnimatedImage(emptyNode), // AnimatedImage
            createAnimatedImage(targetNode) // AnimatedImage
        );
    }

    private AnimatedImage createAnimatedImage(Array<TextureAtlas.AtlasRegion> targetNode) {
        AnimatedImage animatedImage = new AnimatedImage(new Animation<>(1 / 14f, targetNode));
        animatedImage.setLooping(true);
        animatedImage.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        return animatedImage;
    }


}
