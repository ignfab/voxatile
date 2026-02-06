package com.ignfab.minalac.generator.outputs.hytale;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.PrefabBuffer;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.PrefabBufferBlockEntry;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.util.PrefabUtil;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.VoxelTile;

public class HytaleVoxelTile extends VoxelTile {
    private final World world;
    private final Map<WorldCoords2d, Map<Integer, HytaleVoxel>> voxels = new HashMap<>();
    private transient int waterFluidId = -1;

    private static final HytaleVoxel DEFAULT_VOXEL = new HytaleVoxel("Empty");

    public HytaleVoxelTile(WorldBBox3d limits, World world) {
        super(limits);
        this.world = world;
    }

    public void setBlock(int x, int y, int z, HytaleVoxel voxel) {
        if (!this.limits().contains(x, y, z)) return;
        // X/Y/Z => X/Z/-Y
        //world.setBlock(x, z, -y - 1, voxel.blockTypeKey());
        voxels.computeIfAbsent(new WorldCoords2d(x, y), _ -> new HashMap<>()).put(z, voxel);
        updateHeightmaps(x, y, z);
    }

    @Override
    public void save() {
        PrefabBuffer.Builder prefab = PrefabBuffer.newBuilder();
        voxels.forEach((c, col) -> {
            Set<PrefabBufferBlockEntry> column = new TreeSet<>(Comparator.comparing(e -> e.y));
            for (Map.Entry<Integer, HytaleVoxel> entry : col.entrySet()) {
                HytaleVoxel voxel = entry.getValue();
                PrefabBufferBlockEntry block = new PrefabBufferBlockEntry(entry.getKey(), voxel.blockId(), voxel.blockTypeKey());
                if (voxel.blockTypeKey().equals("Fluid_Water")) {
                    if (waterFluidId == -1)
                        waterFluidId = Fluid.getAssetMap().getIndex("Water");
                    block.blockId = 0;
                    block.blockTypeKey = "Empty";
                    block.fluidId = waterFluidId;
                    block.fluidLevel = 8;
                }
                column.add(block);
            }
            // X/Y/Z => X/Z/-Y
            prefab.addColumn(c.x(), -c.y() - 1, column.toArray(PrefabBufferBlockEntry[]::new), null);
        });
        world.execute(() -> PrefabUtil.paste(prefab.build().newAccess(), world, Vector3i.ZERO, Rotation.None, true, new Random(), world.getEntityStore().getStore()));
    }

    @Override
    public Placeable getVoxel(int x, int y, int z) {
        HytaleVoxel voxel = voxels.getOrDefault(new WorldCoords2d(x, y), Map.of()).get(z);
        if (voxel != null)
            return voxel;
        /*BlockType block = world.getBlockType(x, -y - 1, z);
        return block == null ? DEFAULT_VOXEL : new HytaleVoxel(block.getId());*/
        return DEFAULT_VOXEL;
    }
}
