package com.ignfab.minalac.generator.outputs.lego;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ignfab.minalac.generator.generation.SquareUnitsTileGenerator;
import com.ignfab.minalac.generator.utils.FileHelpers;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelTile;
import com.ignfab.minalac.generator.world.VoxelWorld;
import com.ignfab.minalac.generator.world.VoxelWorldMetadata;

/**
 * Implementation of {@link VoxelWorld} that creates a Lego model.
 */
public class LegoWorld extends VoxelWorld {
    private final File destination;
    private final File tiles;
    private final Map<String, WorldCoords2d> tileOffsets = new HashMap<>();

    /**
     * Constructs a new {@code LegoWorld}.
     * The limits of the world have to be set using {@link #setLimits(WorldBBox3d)}
     *
     * @param destination Directory where to save data to. If null nothing is saved.
     */
    public LegoWorld(File destination) {
        super(new VoxelWorldMetadata());
        this.destination = destination;
        this.tiles = new File(destination, "tiles");
    }

    @Override
    public WorldBBox3d maxLimits() {
        return new WorldBBox3d(
            new WorldCoords3d(-50000, -50000, -50000),
            new WorldCoords3d(+50000, +50000, +50000)
        );
    }

    @Override
    public VoxelTile newTile(WorldBBox3d limits) {
        String name = "t.%d.%d.ldr".formatted(limits.minX(), limits.minY());
        tileOffsets.put(name, limits.min().to2d());
        return new LegoTile(new File(tiles, name), limits);
    }

    @Override
    public void initialize() throws MapWriteException {
        if (destination == null)
            return;
        tiles.mkdir();
    }

    @Override
    public void finalizeAndSave() throws MapWriteException {
        if (destination == null)
            return;
        StringBuilder out = new StringBuilder();
        tileOffsets.forEach((name, offset) -> {
            // XYZ => X/-Z/Y
            int x = offset.x() * 40;
            int z = offset.y() * 40;
            out.append("1 16 ").append(x).append(" 0 ").append(z).append(" 1 0 0 0 1 0 0 0 1 tiles/").append(name).append("\r\n");
        });
        try {
            FileHelpers.write(new File(destination, "minalac.ldr"), out.toString());
        } catch (IOException e) {
            throw new MapWriteException(e);
        }
    }

    @Override
    public Collection<WorldBBox2d> tiles(int maxTileSize) {
        // No reason to define a specific minimum unit size,
        // tiles can have any size
        return new SquareUnitsTileGenerator(1, limits().to2d()).getTiles(maxTileSize);
    }
}
