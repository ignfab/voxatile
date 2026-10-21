package fr.ign.voxatile.core.tasks;

import java.util.List;
import java.util.Optional;

import fr.ign.voxatile.core.generation.GenerationTile;
import fr.ign.voxatile.core.generation.heightmaps.ReadableHeightmap;
import fr.ign.voxatile.core.generation.heightmaps.ReadableHeightmapSpec;
import fr.ign.voxatile.core.models.ModelSelection;
import fr.ign.voxatile.core.models.Shape2dConvertibleModel;
import fr.ign.voxatile.core.models.values.ModelValue;
import fr.ign.voxatile.core.placeables.Structure;
import fr.ign.voxatile.core.placeables.layouts.LayoutBuilder;
import fr.ign.voxatile.core.placeables.layouts.UnbuildableLayoutException;
import fr.ign.voxatile.core.voxelization.shape2d.LineString2d;
import fr.ign.voxatile.core.voxelization.shape2d.iterator.IndexedPosition2d;
import fr.ign.voxatile.core.voxelization.shape2d.voxelizer.ThickLinearIndexedVoxelizer2d;

/**
 * A task rendering lines by placing structure along them.
 */
public class RenderLines2dTask extends ModelTask<Shape2dConvertibleModel> {
    // In layout, X is for linear direction (starting at 0, looping over bbox max x), Y for both orthogonal directions (0 is central axis).
    private final List<LayoutBuilder> builders;
    private final ReadableHeightmapSpec heightmapSpec;
    private final ModelValue widthValue;

    /**
     * Creates a new {@code RenderLinesTask}.
     * <p>
     * Structure will be used as template and will be repeated along lines.
     * <p>
     * In structure x-axis will be along lines. Y-axis will be on sides, and z-axis will be used for height.
     *
     * @param selection selection of models to render
     * @param builders Layout to build along the lines
     * @param heightmapSpec heightmap on which draw lines
     */
    public RenderLines2dTask(
        ModelSelection selection,
        List<LayoutBuilder> builders,
        ReadableHeightmapSpec heightmapSpec,
        ModelValue widthValue
    ) {
        super(Shape2dConvertibleModel.class, selection);

        this.builders = builders;
        this.heightmapSpec = heightmapSpec;
        this.widthValue = widthValue;
    }

    /*
    TODO: Should we check root builders policies ?
    They must be : x: ADJUST, y: KEEP/ADJUST (depending on widthValue null or not), z: KEEP
    For now, params takes care of that.
     */

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) {
        ReadableHeightmap heightmap = tile.heightmap(heightmapSpec);

        // TODO: Check builders list has at least one builder.
        // TODO: Create a "FallbackBuilders" class with common code

        // Compute builders size on Y and Z axes.
        // Y axis is line width, it will be in keep policy
        // Z axis is vertical, it will be in keep policy
        int sizeY = 0;
        int originY = Integer.MIN_VALUE;
        int sizeZ = 0;
        int originZ = Integer.MIN_VALUE;

        for (LayoutBuilder builder: builders) {
            sizeY = Math.max(sizeY, builder.yAxis().minimumSize());
            originY = Math.max(originY, builder.yAxis().origin());
            sizeZ = Math.max(sizeZ, builder.zAxis().minimumSize());
            originZ = Math.max(originZ, builder.zAxis().origin());
        }

        // If a width model value is given, use this value instead of layout width.
        if (widthValue != null) {
            Optional<Integer> width = widthValue.getAsInt(model);

            if (width.isEmpty())
                return;

            sizeY = width.get();
            originY = 0;

        }

        // TODO: Make it possible to build per segment instead of per linestring

        for (LineString2d lineString: model.toShape2d().lineStrings()) {
            int sizeX = (int) Math.ceil(lineString.length());

            // Don't draw single point line strings (no length, only empty builder would fit)
            if (sizeX == 0)
                return;

            Structure structure = null;

            for (LayoutBuilder builder : builders) {
                try {
                    // Trying to build, we don't realy care of x axis that will be stretched anyway
                    // TODO: Maybe limit that stretch to a certain ratio
                    structure = builder.build(builder.xAxis().maxSizeFittingUnder(sizeX), sizeY, sizeZ);
                    break;
                } catch (UnbuildableLayoutException ignored) {}
            }

            if (structure == null) {
                System.out.println("Could not build linear structure");
                return;
            }

            // Stretch structure along x axis
            double xRatio = structure.limits().maxX() / sizeX;

            // Adapt voxelizer to structure size
            ThickLinearIndexedVoxelizer2d voxelizer = new ThickLinearIndexedVoxelizer2d(structure.limits().sizeY());

            // Always align layout center to the line axis.
            double yOffset = 0.5 * sizeY + originY;

            for (IndexedPosition2d pos : tile.limits().to2d().filterInside(voxelizer.voxelize(lineString))) {
                int x = (int) Math.floor(pos.index() * xRatio);
                int y = (int) Math.floor(pos.distance() + yOffset);
                for (int z = structure.limits().minZ(); z <= structure.limits().maxZ(); z++)
                    structure.get(x, y, z).place(tile.voxels(), pos.coords().x(), pos.coords().y(), heightmap.get(pos.coords()) + z);
            }
        }
    }
}
