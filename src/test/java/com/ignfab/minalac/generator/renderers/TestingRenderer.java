package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.world.VoxelWorldTile;

/**
 * A renderers that does nothing (just for deserialization testing).
 */
public class TestingRenderer implements Renderer {
    @Override
    public void render(VoxelWorldTile tile) {}
}
