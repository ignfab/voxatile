package fr.ign.voxatile.core.parameters.tasks;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.parameters.heightmaps.ReadableHeightmapParams;
import fr.ign.voxatile.core.parameters.placeables.PlaceableParams;
import fr.ign.voxatile.core.parameters.placeables.structures.PlaceableStructureParams;
import fr.ign.voxatile.core.placeables.PlaceableStructure;
import fr.ign.voxatile.core.tasks.RenderLines3dTask;
import fr.ign.voxatile.core.tasks.TileTask;
import fr.ign.voxatile.core.utils.world3d.WorldBBox3d;

/**
 * Parameters for {@link RenderLines3dTask}.
 */
public class RenderLines3dTaskParams extends ModelTaskParams {
    /**
     * Width of lines to render (optional, default 1).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public int width = 1;

    /**
     * What to place on line (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public PlaceableParams place = null;

    /**
     * Structure to place and repeat along lines (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public PlaceableStructureParams structure = null;

    /**
     * Render only when above this heightmap (optional, default always render).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ReadableHeightmapParams renderOnlyWhenAbove = null;

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();

        if (width < 1)
            throw new IllegalArgumentException("Width should be greater than 0");

        if (place != null && structure != null)
            throw new IllegalArgumentException("Can not have both place and structure fields");

        if (place == null && structure == null)
            throw new IllegalArgumentException("There must be a place or a structure field");

        if (structure != null)
            structure.validate();

        if (place != null)
            place.validate();

        if (renderOnlyWhenAbove != null)
            renderOnlyWhenAbove.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        PlaceableStructure drawnStructure;

        if (place != null) {
            PlaceableStructure.Builder builder = PlaceableStructure.builder();
            builder.set(new WorldBBox3d(0, 0, 0, 1, width, 1), place.create(generation.seed()));
            drawnStructure = builder.build();
        } else {
            drawnStructure = structure.create(generation.seed());
        }

        return new RenderLines3dTask(
            models.create(generation),
            drawnStructure,
            renderOnlyWhenAbove == null ? null : renderOnlyWhenAbove.create(generation.heightmaps())
        );
    }
}

