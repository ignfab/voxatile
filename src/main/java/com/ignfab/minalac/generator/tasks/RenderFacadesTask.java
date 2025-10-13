package com.ignfab.minalac.generator.tasks;

import java.util.ArrayList;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable2d;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.placeables.SegmentCapability;
import com.ignfab.minalac.generator.placeables.gettable2d.Container;
import com.ignfab.minalac.generator.placeables.gettable2d.Gettable2d;
import com.ignfab.minalac.generator.placeables.gettable2d.Layout;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Polygon2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * A {@link TileTask} rendering a {@link ModelSelection} as building facades.
 * <p>
 * It uses two {@link PlaceableStructure} one to render the ground floor and the other for the upper floors.
 * The two patterns are arranged in a way that they are repeated at their respective sizes and extending where necessary.
 * If the building isn't tall enough to hold two floors the ground floor pattern will extend along the z-axis.
 */
public class RenderFacadesTask extends ModelTask<ShapesVoxelizable2d> {
    private final ReadableHeightmapSpec heightmapSpec;
    private final String heightMetadata;
    private final PlaceableStructure groundFloorPattern;
    private final PlaceableStructure upperFloorPattern;

    /**
     * Creates a new {@code RenderFacadesTask}.
     *
     * @param selection building models selection
     * @param heightmapSpec heightmap of the ground (on which features will be placed)
     * @param heightMetadata name of the metadata for facade height
     * @param groundFloorPattern extendable {@link PlaceableStructure} for the ground floor. It must be extendable along z-axis and x-axis.
     * @param upperFloorPattern extendable {@link PlaceableStructure} for the upper floors. It must be extendable along x-axis.
     */
    public RenderFacadesTask(
        ModelSelection selection,
        ReadableHeightmapSpec heightmapSpec,
        String heightMetadata,
        PlaceableStructure groundFloorPattern,
        PlaceableStructure upperFloorPattern
    ) {
        super(ShapesVoxelizable2d.class, selection);
        this.heightmapSpec = heightmapSpec;
        this.heightMetadata = heightMetadata;
        this.groundFloorPattern = groundFloorPattern;
        this.upperFloorPattern = upperFloorPattern;
    }

    @Override
    protected void run(ShapesVoxelizable2d model, GenerationTile tile) {
        ReadableHeightmap heightmap = tile.heightmaps().get(heightmapSpec);
        // TODO-PR: Probably implement a not found policy for the metadata
        // TODO: Implement a post-processor for value rounding to rollback this change
        int height = (int) Math.round(
            // Casting to Number is needed to avoid a cast exception in RenderFacadesTask
            ((Number) model.getMetadata(heightMetadata)).doubleValue()
        );

        ShapesVoxelizer2d voxelizer = model.voxelize2d(tile.limits().to2d());

        Gettable2d layout;

        for (Polygon2d polygon : getPolygonFromVoxelizerTemporary(voxelizer, tile.limits().to2d())) {
            for (Line2d line : polygon.lines()) {
                int lineLength = line.maxIndex() + 1;

                // lineLength and height must be greater or equal respectively to the length of both motif along x-axis and the length of ground motif along z-axis.
                // That case is, for now, handle by StructureExtenderXZ's create() method. TODO-PR: See if there is a better way to handle
                // The height won't be smaller that motif length as it is assumed a model filter is used.
                // lineLength may be smaller than motifs length along x-axis -> a line merger should be done
                // TODO-PR: There is a need for a "line merger". This line merger would merge successive lines so there is a index continuity based on the angle formed. PR #113 is needed.
                layout = createSimpleLayoutFromStructure(lineLength, height, groundFloorPattern, upperFloorPattern);

                for (int iX = 0; iX < lineLength; iX++) {
                    WorldCoords2d c = line.atIndex(iX);
                    int minZ = heightmap.get(c) + 1;

                    for (int iZ = 0; iZ < height; iZ++) {
                        layout.get(iX, iZ).place(tile.voxels(), c.x(), c.y(), minZ + iZ);
                    }
                }
            }
        }
    }

    private static Gettable2d createSimpleLayoutFromStructure(int lineLength, int height, PlaceableStructure groundFloorPattern, PlaceableStructure upperFloorPattern) {
        checkExtendability(groundFloorPattern.axisX(), "groundFloorPattern x-axis");
        checkExtendability(upperFloorPattern.axisX(), "upperFloorPattern x-axis");
        checkExtendability(groundFloorPattern.axisZ(), "groundFloorPattern z-axis");

        Container layout = new Container();
        int heightGround = height;

        int floorMinHeight = upperFloorPattern.axisZ().minimalLength();
        int groundMinHeight = groundFloorPattern.axisZ().minimalLength();

        if (height >= (floorMinHeight + groundMinHeight)) {
            int heightFloors = (height - groundMinHeight) - ((height - groundMinHeight) % floorMinHeight);
            heightGround = heightGround - heightFloors;

            Layout forFloor = new Layout.RepeatXZ(new WorldBBox2d(0, heightGround, lineLength, heightFloors), upperFloorPattern);
            layout.add(forFloor);
        }
        Layout forGround = new Layout.RepeatX(new WorldBBox2d(0, 0, lineLength, heightGround), groundFloorPattern);
        layout.add(forGround);

        return layout;
    }

    private static void checkExtendability(SegmentCapability axis, String axisName) {
        if (!axis.isExpendable())
            throw new UnsupportedOperationException(String.format("%s must be extendable", axisName));
    }

    // TODO-PR: Temporary. To be removed when this PR is based on PR #113.
    private ArrayList<Polygon2d> getPolygonFromVoxelizerTemporary(ShapesVoxelizer2d voxelizer, WorldBBox2d bbox) {
        ArrayList<Polygon2d> polygon = new ArrayList<>();
        for (Shape2d shape : voxelizer.getShapesTemporary()) {
            if (shape instanceof Polygon2d) {
                Polygon2d p = (Polygon2d) shape;
                boolean allLinesIn = true;
                for (Line2d line : p.lines()) {
                    if (!bbox.contains(line.bbox())) {
                        allLinesIn = false;
                        break;
                    }
                }
                if (allLinesIn)
                    polygon.add(p);
            }
        }
        return polygon;
    }
}
