package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * A renderer capable of rendering something in an area.
 */
public interface Renderer {
    /**
     * Performs rendering.
     *
     * @param bbox the limits of the rendering area.
     */
    void render(WorldBBox3d bbox);
}
