package com.ignfab.minalac.generator.tasks;

import java.util.List;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.placeables.Structure;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * A task helping working out layout builders.
 * <p>
 * This task places a structure resulting from builders with a desired size.
 * It is not intended to be used to create worlds from geographical data but rather to visualize how layout builders behave.
 */
public class DebugLayoutTask implements TileTask {
    private final List<LayoutBuilder> builders;
    private final WorldCoords3d position;
    private final Integer sizeX;
    private final Integer sizeY;
    private final Integer sizeZ;

    /**
     * Creates an new {@code DebugStructureBuilderTask}.
     * @param builders list of builders to use for construction (first succeeding will be used)
     * @param position where to place built structure in world
     * @param sizeX x-axis component of wanted resuling size or null
     * @param sizeY y-axis component of wanted resuling size or null
     * @param sizeZ z-axis component of wanted resuling size or null
     */
    public DebugLayoutTask(List<LayoutBuilder> builders, WorldCoords3d position, Integer sizeX, Integer sizeY, Integer sizeZ) throws UnbuildableException {
        this.builders = builders;
        this.position = position;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    @Override
    public void run(GenerationTile tile) {
        Structure structure = null;
        int number = 0;

        for (LayoutBuilder builder : builders) {
            number++;
            int x = this.sizeX != null ? this.sizeX : builder.xAxis().minimumSize();
            int y = this.sizeY != null ? this.sizeY : builder.yAxis().minimumSize();
            int z = this.sizeZ != null ? this.sizeZ : builder.zAxis().minimumSize();
            System.out.println("Builder #%d buid(%d, %d, %d)".formatted(number,x, y, z));
            try {
                structure = builder.build(x, y, z);
                break;
            } catch (UnbuildableException e) {
                System.out.println("Failed:");
                e.printStackTrace();
            }
        }

        if (structure == null) {
            // TODO: Throw exception ?
            System.out.println("Could not build facade structure");
            return;
        }
System.out.println(structure.limits());
        structure.place(tile.voxels(), position.x(), position.y(), position.z());
    }
}
