package com.ignfab.minalac.generator.mcserver;

import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.VoxelTile;

public class MCServerTile extends VoxelTile {
    private GenerationUnit unit;

    public MCServerTile(WorldBBox3d limits) {
        super(limits);
    }

    public void setUnit(GenerationUnit unit) {
        this.unit = unit;
    }

    public synchronized void setBlock(int x, int y, int z, Block block) {
        if (!this.limits().contains(x, y, z)) return;
        // X/Y/Z => X/Z/-Y
        unit.modifier().setBlock(x, z, -y - 1, block);
    }

    @Override
    public void save() {}

    @Override
    public Placeable getVoxel(int x, int y, int z) {
        return null;
    }
}
