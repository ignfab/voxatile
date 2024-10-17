package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.ReadableHeightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.selection.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable2d;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;
import com.ignfab.minalac.generator.world.MultilineTextEntityVerticalAnchor;
import com.ignfab.minalac.generator.world.VoxelTypeFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Metadata renderer displays the metadata from models as floating text above them.
 */
public class MetadataRenderer extends ModelRenderer {
    private final ReadableHeightmap heightmap;
    private final VoxelTypeFactory factory;
    private final List<String> metadataNames;

    /**
     * Special value to render all metadata.
     */
    public static final List<String> ALL_METADATA = Collections.emptyList();

    /**
     * Creates a new {@code MetadataRenderer}.
     *
     * @param models the models those metadata should be rendered
     * @param heightmap the heightmap to place metadata
     * @param factory the world's factory to create texts
     * @param metadataNames the list of metadata to render
     */
    public MetadataRenderer(ModelSelection models, ReadableHeightmap heightmap, VoxelTypeFactory factory, List<String> metadataNames) {
        super(models);
        this.heightmap = heightmap;
        this.factory = factory;
        this.metadataNames = metadataNames;
    }

    @Override
    protected void render(Model model, WorldBBox3d bbox) {
        if (!(model instanceof ShapesVoxelizable2d voxelizable)) {
            // TODO: Better warning about not possible to render a non voxelizable model
            System.err.println("Ignoring non shapesvoxelizable model. Type: " + model.getClass());
            return;
        }
        ShapesVoxelizer2d voxelizer = voxelizable.voxelize2d(bbox.to2d());

        double xMean = 0;
        double yMean = 0;
        int n = 0;
        for (Positioned2d voxel : voxelizer) {
            xMean += voxel.coords().x();
            yMean += voxel.coords().y();
            n++;
        }
        if (n == 0) // Empty feature
            return;
        xMean /= n;
        yMean /= n;
        StringBuilder s = new StringBuilder();
        Collection<String> names = metadataNames.isEmpty() ? model.listMetadata() : metadataNames;
        for (String name : names)
            if (model.hasMetadata(name))
                s.append(name).append(": ").append((Object) model.getMetadata(name)).append('\n');
        factory.createText(s.toString(), MultilineTextEntityVerticalAnchor.BOTTOM)
            .place(xMean, yMean, heightmap.get((int) Math.floor(xMean), (int) Math.floor(yMean)) + 1.5);
    }
}
