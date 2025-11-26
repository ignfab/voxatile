package com.ignfab.minalac.generator.outputs.minetest;

import java.io.File;
import java.util.Collection;

import com.ignfab.minalac.generator.generation.Minimap;
import com.ignfab.minalac.generator.generation.SquareUnitsTileGenerator;
import com.ignfab.minalac.generator.outputs.minetest.utils.SQLiteMapWriter;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.FileHelpers;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelWorld;
import com.ignfab.minalac.generator.world.VoxelWorldMetadata;

/**
 * Implementation of {@link VoxelWorld} that creates a playable world specifically for Minetest.
 */
public class MTVoxelWorld extends VoxelWorld {
    // See MAX_MAP_GENERATION_LIMIT constant on Minetest
    // https://github.com/minetest/minetest/blob/master/src/constants.h#L69
    private static final WorldBBox3d MAX_LIMIT = new WorldBBox3d(
        new WorldCoords3d(-31_007, -31_007, -31_007),
        new WorldCoords3d(31_007, 31_007, 31_007)
    );

    private final File destination;
    private SQLiteMapWriter mapWriter;

    /**
     * Constructs a new {@code MTVoxelWorld}.
     * The limits of the world have to be set using {@link #setLimits(WorldBBox3d)}
     *
     * @param destination Directory where to save data to. If null nothing is saved.
     */
    public MTVoxelWorld(File destination) {
        super(new VoxelWorldMetadata());
        this.destination = destination;
    }

    @Override
    public WorldBBox3d maxLimits() {
        return MAX_LIMIT;
    }

    @Override
    public MTVoxelTile newTile(WorldBBox3d limits) {
        return new MTVoxelTile(mapWriter, limits);
    }

    @Override
    public void initialize() throws MapWriteException {
        if (destination == null)
            return;

        if (!destination.exists() || !destination.isDirectory())
            throw new MapWriteException("Directory %s can not be accessed".formatted(destination));

        mapWriter = new SQLiteMapWriter(new File(destination, "map.sqlite"));
        mapWriter.createDatabase();

        minimap = new Minimap(limits());
    }

    /**
     * {@inheritDoc}
     * The world is exported in a format for Minetest.
     */
    @Override
    public void finalizeAndSave() throws MapWriteException {
        if (destination == null)
            return; // Save disabled if null destination

        try {
            mapWriter.close();

            FileHelpers.write(new File(destination, "world.mt"), """
                world_name = %s
                enable_damage = true
                creative_mode = true
                auth_backend = sqlite3
                player_backend = sqlite3
                gameid = minetest
                """.formatted(metadata.getWorldName()));
            FileHelpers.write(new File(destination, "map_meta.txt"), """
                mapgen_limit = 31000
                mg_name = singlenode
                seed = 0
                [end_of_params]
                """);
            FileHelpers.write(new File(destination, "worldmods/ign_spawn/init.lua"), """
                minetest.setting_set("static_spawnpoint", "%d, %d, %d")
                core.register_on_joinplayer(function(player) player:set_sky{ clouds = false } end)
                """.formatted(metadata.getSpawn().x(), metadata.getSpawn().z(), metadata.getSpawn().y())); // XYZ => XZY
        } catch (Exception e) {
            throw new MapWriteException(e);
        }
    }

    @Override
    public Collection<WorldBBox2d> tiles(int maxTileSize) {
        SquareUnitsTileGenerator tileGenerator = new SquareUnitsTileGenerator(Block.SIZE, limits().to2d());
        return tileGenerator.getTiles(maxTileSize);
    }

    @Override
    public Placeable defaultVoxel() {
        return MTVoxel.DEFAULT_VOXEL;
    }
}
