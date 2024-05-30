package com.ignfab.minalac.generator.generation.temp;

import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dElement;
import com.ignfab.minalac.generator.world.OutOfWorldException;
import com.ignfab.minalac.generator.world.SemanticType;
import com.ignfab.minalac.generator.world.VoxelType;
import com.ignfab.minalac.generator.world.VoxelWorld;

public class GroundRenderer {
    VoxelWorld world;
    HeightMap heightMap;

    public GroundRenderer(VoxelWorld world, HeightMap heightMap) {
        this.world = world;
        this.heightMap = heightMap;
    }

    public void render() {
        VoxelType grassVT = world.getFactory().createVoxelType(SemanticType.Grass);
        for (Chunk2dElement element : heightMap) {
            int x = element.getX();
            int y = element.getY();
            int z = element.getValue();
            try {
                grassVT.place(x, y, z);
            } catch (OutOfWorldException e) {
                //todo 2024-05-30 : throw exception or do something else?
                throw new RuntimeException(e);
            }
        }
    }
}
