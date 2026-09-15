package com.github.br.libgdx.jam38.game.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.github.br.libgdx.jam38.game.Resources;
import com.github.br.libgdx.jam38.structure.screen.loading.AssetsLoader;

public class TempAssetLoader implements AssetsLoader {
    @Override
    public void loadAssets(AssetManager assetManager) {
        assetManager.load(Resources.Particles.NODE, ParticleEffect.class);
        assetManager.load(Resources.Particles.EDGE, ParticleEffect.class);
    }

    @Override
    public void unloadAssets(AssetManager assetManager) {
        assetManager.unload(Resources.Particles.NODE);
        assetManager.unload(Resources.Particles.EDGE);
    }

}
