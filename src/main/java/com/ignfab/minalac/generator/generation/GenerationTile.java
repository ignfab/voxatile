package com.ignfab.minalac.generator.generation;

import java.util.HashMap;
import java.util.Map;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapStore;
import com.ignfab.minalac.generator.models.ModelStore;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * A tiled part of the generation.
 */
public class GenerationTile {
    private final Generation generation;
    private final VoxelTile voxels;
    private final HeightmapStore heightmaps;
    private final ModelStore models = new ModelStore();

    private final Map<String, ModelTypeVolume> modelTypeVolumes = new HashMap<>();

    /**
     * Creates a new {@code GenerationTile} for a generation and a volume.
     *
     * @param generation parent generation of this tile
     * @param limits limits of the tile
     */
    public GenerationTile(Generation generation, WorldBBox3d limits) {
        this.generation = generation;

        // Create voxel tile
        voxels = generation.world().newTile(limits);

        // Create heightmap store populated with stored heigthmaps
        heightmaps = new HeightmapStore(generation.heightmaps(), limits.to2d());

        // Set already known margins for model types
        generation.modelTypeMargins().forEach((name, margins) ->
            modelTypeVolumes.put(name, new ModelTypeVolume(limits.enlarged(margins)))
        );
    }

    /**
     * {@return the generation which this tile belongs to}
     */
    public Generation generation() {
        return generation;
    }

    /**
     * {@return the model store}
     */
    public ModelStore models() {
        return models;
    }

    /**
     * {@return the limits of the tile}
     */
    public WorldBBox3d limits() {
        return voxels.limits();
    }

    /**
     * {@return heightmap store for the generation tile}
     */
    public HeightmapStore heightmaps() {
        return heightmaps;
    }

    /**
     * {@return voxel world tile for this generation tile}
     */
    public VoxelTile voxels() {
        return voxels;
    }

    /**
     * Saves tile undelying data to its final destination so tile could be freed.
     *
     * @throws MapWriteException if an error occurs while writing to destination.
     */
    public void save() throws MapWriteException {

        // Heightmaps are discarded, only voxels are saved
        voxels().save();
    }

    /**
     * Returns volume information for model type.
     * <p>
     * A volume information is attached to each model type. By default, volume is the tile volume.
     *
     * @param modelType model type name
     * @return volume information
     *
     * {@see GenerationTile#ModelTypeVolume}
     */
    public ModelTypeVolume modelTypeVolume(String modelType) {
        ModelTypeVolume volume = modelTypeVolumes.get(modelType);
        if (volume == null) {
            volume = new ModelTypeVolume(this.limits());
            modelTypeVolumes.put(modelType, volume);
        }
        return volume;
    }

    /**
     * Model type volume information.
     * <p>
     * Model type volume can be expanded (never shrunk) to include margins
     * or other volumes, until it is read for usage.
     * Aftewards it's locked and can't be modified anymore.
     **/
    public static class ModelTypeVolume {
        private WorldBBox3d volume;
        private boolean locked;

        /**
         * Creates a new {@code ModelTypeVolume}.
         *
         * Margins will be added to base volume only (and not to volumes included later).
         *
         * @param volume base volume
         */
        public ModelTypeVolume(WorldBBox3d volume) {
            this.volume = volume;
            locked = false;
        }

        /**
         * Returns volume value.
         * Volume won't be updatable anymore afterwards.
         *
         * @return model type volume as a bounding box
         */
        public WorldBBox3d get() {
            locked = true;
            return volume;
        }

        private void checkLock() {
            if (locked)
                throw new IllegalStateException("Can't enlarge limits that have already been used");
        }

        /**
         * Include given area.
         * <p>
         * The volume xy-area will grow enough to include both its former area and given area.
         * Volume height is unchanged.
         *
         * @param area area to include
         */
        public void include(WorldBBox2d area) {
            checkLock();
            volume = WorldBBox2d.surrounding(volume.to2d(), area).to3d(volume.minZ(), volume.sizeZ());
        }

        /**
         * Include given volume.
         * <p>
         * The volume will grow enough to include both its former volume and given volume.
         *
         * @param volume volume to include
         *
         */
        public void include(WorldBBox3d volume) {
            checkLock();
            this.volume = WorldBBox3d.surrounding(this.volume, volume);
        }
    }

}
