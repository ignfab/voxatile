package com.ignfab.minalac.generator.generation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapStore;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.WritableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.WritableHeightmapSpec;
import com.ignfab.minalac.generator.models.BBoxModel;
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

    private final Map<String, WorldBBox2d> modelTypeBBox = new HashMap<>();

    private static GenerationTile currentTile = null;

    /**
     * @return current tile.
     */
    public static GenerationTile current() {
        if (currentTile == null)
            throw new IllegalStateException("No current tile!");
        return currentTile;
    }

    /**
     * Sets current tile (or null for none).
     *
     * @param tile tile to set
     */
    /* package private */ static void setCurrent(GenerationTile tile) {
        currentTile = tile;
    }

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

        // Create the "tile" model
        // TODO: mark some model type as readonly to avoid adding models from these types (world, tile)
        models.add("tile",
            Collections.singletonList(new BBoxModel(limits.to2d()))
        );
    }

    /**
     * {@return the generation which this tile belongs to}
     */
    public Generation generation() {
        return generation;
    }

    /**
     * {@return the limits of the tile}
     */
    public WorldBBox3d limits() {
        return voxels.limits();
    }

    /**
     * {@return the model store}
     */
    public ModelStore models() {
        return models;
    }

    //TODO: Javadoc
    public WorldBBox2d modelTypeArea(String modelType) {
        WorldBBox2d bbox = modelTypeBBox.get(modelType);
        return bbox == null? limits().to2d() : bbox;
    }

    public WorldBBox3d modelTypeVolume(String modelType) {
        WorldBBox2d bbox = modelTypeBBox.get(modelType);
        return bbox == null? limits() : bbox.to3d(limits().minZ(), limits().maxZ());
    }

    //TODO: Javadoc
    public void includeBBoxForModelType(String modelType, WorldBBox2d bbox) {
        modelTypeBBox.put(modelType,  WorldBBox2d.surrounding(modelTypeArea(modelType), bbox));
    }

    /**
     * {@return heightmap store for the generation tile}
     */
    public HeightmapStore heightmaps() {
        return heightmaps;
    }

    /**
     * Retrieves or creates a {@link ReadableHeightmap} from {@link #heightmaps() the store}, using the given spec if not null.
     * @param spec Specification of heightmap to get, can be null
     * @return The resulting heightmap, or {@code null} if the spec was {@code null}
     * @see HeightmapStore#get(ReadableHeightmapSpec)
     */
    public ReadableHeightmap heightmap(ReadableHeightmapSpec spec) {
        return heightmap(spec, null);
    }

    /**
     * Retrieves or creates a {@link ReadableHeightmap} from {@link #heightmaps() the store}, using the given spec if not null, with default otherwise.
     * @param spec Specification of heightmap to get, can be null
     * @param defaultHeightmap Default heightmap returned when specification is null
     * @return The resulting heightmap, or {@code defaultHeightmap} if the spec was {@code null}
     * @see HeightmapStore#get(ReadableHeightmapSpec)
     */
    public ReadableHeightmap heightmap(ReadableHeightmapSpec spec, ReadableHeightmap defaultHeightmap) {
        return spec == null ? defaultHeightmap : heightmaps().get(spec);
    }

    /**
     * Retrieves a {@link WritableHeightmap}  from {@link #heightmaps() the store}, using the given spec.
     * @param spec Specification of stored heightmap to get
     * @return The resulting heightmap
     * @see HeightmapStore#get(WritableHeightmapSpec)
     */
    public WritableHeightmap heightmap(WritableHeightmapSpec spec) {
        return heightmaps().get(spec);
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
}
