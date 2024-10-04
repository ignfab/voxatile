package com.ignfab.minalac.generator.parameters.renderers;

import java.beans.ConstructorProperties;
import java.util.LinkedHashMap;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.renderers.GroundRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;
import com.ignfab.minalac.generator.world.SemanticType;
import com.ignfab.minalac.generator.world.SimpleVoxelPattern;
import com.ignfab.minalac.generator.world.VoxelType;

/**
 * Parameters for a {@link GroundRenderer}.
 *
 * Until voxel structures are serializable, this perform a basic voxel structure creation
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class GroundRendererParams extends RendererParams {
    /**
     * The name of the heightmap to use (required).
     */
    public String heightmap;
    /**
     * List of voxel types and thickness (temporary, to be replaced with structures).
     * THIS WILL NOT SUPPORT DUPLICATE TYPES
     */
    public LinkedHashMap<SemanticType, Integer> voxels;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param heightmap the name of the heightmap to use
     * @param voxels voxels to place as a pair of voxel type and number to place all the way down
     */
    @ConstructorProperties({"heightmap", "voxels"})
    public GroundRendererParams(String heightmap, LinkedHashMap<SemanticType, Integer> voxels) {
        this.heightmap = heightmap;
        this.voxels = voxels;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (heightmap.isEmpty())
            throw new IllegalArgumentException("The field heightmap cannot be empty");
    }

    @Override
    public Renderer create(Generation generation) {
        SimpleVoxelPattern pattern = new SimpleVoxelPattern();
        int z = 0;
        for (SemanticType st : voxels.keySet()) {
            VoxelType vt = generation.world().getFactory().createVoxelType(st);
            int count = voxels.get(st);
            while (count > 0) {
                count--;
                pattern.set(0, 0, z, vt);
                z--;
            }
        }
        return new GroundRenderer(generation.heightmaps().get(heightmap), pattern);
    }
}
