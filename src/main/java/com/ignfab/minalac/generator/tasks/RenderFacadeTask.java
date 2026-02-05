package com.ignfab.minalac.generator.tasks;

import java.util.LinkedList;
import java.util.List;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
import com.ignfab.minalac.generator.placeables.LayoutStructure;
import com.ignfab.minalac.generator.placeables.Structure;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.utils.axis.Axis;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.IndexedPosition2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.ThickLinearIndexedVoxelizer2d;

/**
 * A {@link ModelTask} rendering facades from 2d shapes, using {@link LayoutBuilder}s.
 */
public class RenderFacadeTask  extends ModelTask<Shape2dConvertibleModel> {

    private final List<LayoutBuilder> builders;
    private final String heightMetadata;
    private final String baseAltitudeMetadata;
    private final ThickLinearIndexedVoxelizer2d voxelizer;

    /**
     * Creates a new {@code RenderFacadeTask}.
     *
     * @param selection Selection of models to render
     * @param builders Layout builder to use to render facades
     * @param heightMetadata Name of metadata holding building height
     * @param baseAltitudeMetadata Name of metadata holding building base altitude (altitude of walls bottom)
     */
    public RenderFacadeTask(
        ModelSelection selection,
        List<LayoutBuilder> builders,
        String heightMetadata,
        String baseAltitudeMetadata
    ) {
        super(Shape2dConvertibleModel.class, selection);
        this.builders = builders;
        this.heightMetadata = heightMetadata;
        this.baseAltitudeMetadata = baseAltitudeMetadata;

        int thickness = 0;
        for (LayoutBuilder builder : builders)
            thickness = Math.max(thickness, builder.yAxis().minimumSize());

        voxelizer = new ThickLinearIndexedVoxelizer2d(thickness);
    }

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) {

        // Process metadata
        Integer height = model.getMetadata(heightMetadata);
        Integer baseAltitude = model.getMetadata(baseAltitudeMetadata);
        if (height == null || baseAltitude == null || height < 0)
            return;

        height = (int) Math.round(height / tile.generation().getVerticalScale());

        // Process each lineString (actually linearRing) separately
        for (LineString2d lineString : model.toShape2d().lineStrings()) {

            // Determine facade parts lengths
            List<Integer> lengths = new LinkedList<>();
            double remain = 0.0;

            Segment2d previousSegment = null;
            for (Segment2d segment : lineString.segments()) {

                if (segment.length() > 2.0 && previousSegment != null
                    && previousSegment.direction().dot(segment.direction()) < 0.5
                ) {
                    int length = (int) Math.ceil(remain);
                    remain -= length;
                    lengths.add(length);
                }
                remain += segment.length();
                previousSegment = segment;
            }

            // Add last remaining part
            if (remain > 0.0)
                lengths.add((int) Math.ceil(remain));

            // Prepare final structure
            List<Structure> structures = new LinkedList<>();
            for (int length : lengths) {
                Structure structure = null;

                for (LayoutBuilder builder : builders) {
                    try {
                        structure = builder.build(
                            (int) Math.ceil(length),
                            builder.yAxis().minimumSize(),
                            height);
                        break;
                    } catch (UnbuildableException e) {}
                }

                if (structure == null) {
                    // TODO: Throw exception ?
                    System.out.println("Could not build facade structure");
                    return;
                }
                structures.add(structure);
            }

            Structure structure = LayoutStructure.concatenate(structures, Axis.X);

            // Draw structure along linestring
            int x;
            int y;

            for (IndexedPosition2d index : voxelizer.voxelize(lineString)) {
                x = (int) Math.round(index.index());
                y = (int) Math.round(index.distance());
                for (int z = 0; z < height; z++)
                    if (structure.limits().contains(x, y, z))
                        structure.get(x, y, z).place(tile.voxels(), index.coords().x(), index.coords().y(), z + baseAltitude);
            }
        }
    }
}
