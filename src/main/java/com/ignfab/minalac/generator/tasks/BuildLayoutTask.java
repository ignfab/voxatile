package com.ignfab.minalac.generator.tasks;

import java.util.List;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.placeables.Structure;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.placeables.layouts.UnbuildableLayoutException;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * A task building a structure from a layout builder and placing it in the world.
 * <p>
 * It is not intended to be used to create worlds from geographical data but rather to visualize how layout builders behave.
 */
public class BuildLayoutTask implements TileTask {
    private final List<LayoutBuilder> builders;
    private final WorldCoords3d position;
    private final Integer sizeX;
    private final Integer sizeY;
    private final Integer sizeZ;

    /**
     * Creates a new {@code DebugLayoutTask}.
     * @param builders list of builders to use for construction (first succeeding will be used)
     * @param position where to place built structure in world
     * @param sizeX x-axis component of wanted resulting size or null
     * @param sizeY y-axis component of wanted resulting size or null
     * @param sizeZ z-axis component of wanted resulting size or null
     */
    public BuildLayoutTask(List<LayoutBuilder> builders, WorldCoords3d position, Integer sizeX, Integer sizeY, Integer sizeZ) throws UnbuildableLayoutException {
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

        String prefix = String.format("(%d, %d, %d)", position.x(), position.y(), position.z());

        for (LayoutBuilder builder : builders) {
            number++;
            int x = sizeX != null ? sizeX : builder.xAxis().minimumSize();
            int y = sizeY != null ? sizeY : builder.yAxis().minimumSize();
            int z = sizeZ != null ? sizeZ : builder.zAxis().minimumSize();
            System.out.printf("%s: Try builder #%d with size (x=%d, y=%d, z=%d)%n", prefix, number, x, y, z);
            System.out.printf("%s: On x-axis: minimum=%d max-under=%d%n", prefix, builder.xAxis().minimumSize(), builder.xAxis().maxSizeUnder(x));
            System.out.printf("%s: On y-axis: minimum=%d max-under=%d%n", prefix, builder.yAxis().minimumSize(), builder.yAxis().maxSizeUnder(y));
            System.out.printf("%s: On z-axis: minimum=%d max-under=%d%n", prefix, builder.zAxis().minimumSize(), builder.zAxis().maxSizeUnder(z));
            try {
                structure = builder.build(x, y, z);
                break;
            } catch (UnbuildableLayoutException e) {
                System.out.printf("%s: Failed:%n", prefix);
                e.printStackTrace();
            }
        }

        if (structure == null) {
            System.out.println("%s: No builder worked".formatted(prefix));
            return;
        }
        System.out.printf("%s: Resulting structure size: (x=%d, y=%d, z=%d)%d", prefix, structure.limits().size().x(), structure.limits().size().y(), structure.limits().size().z());
        structure.place(tile.voxels(), position.x(), position.y(), position.z());
    }
}
