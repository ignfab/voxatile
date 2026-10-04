package fr.ign.voxatile.core.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.parameters.heightmaps.ReadableHeightmapParams;
import fr.ign.voxatile.core.parameters.placeables.structures.PlaceableStructureParams;
import fr.ign.voxatile.core.tasks.RenderLinesTask;
import fr.ign.voxatile.core.tasks.TileTask;

/**
 * Parameters for {@link RenderLinesTask}.
 */
public class RenderLinesTaskParams extends ModelTaskParams {
    /**
     * Structure to place and repeat along lines (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableStructureParams structure;

    /**
     * Render only when above this heightmap (optional, default always render).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ReadableHeightmapParams renderOnlyWhenAbove;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param structure structure to place along lines
     */
    @ConstructorProperties({"structure"})
    public RenderLinesTaskParams(PlaceableStructureParams structure) {
        this.structure = structure;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        structure.validate();
        if (renderOnlyWhenAbove != null)
            renderOnlyWhenAbove.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new RenderLinesTask(
            models.create(generation),
            structure.create(generation.seed()),
            renderOnlyWhenAbove == null ? null : renderOnlyWhenAbove.create(generation.heightmaps())
        );
    }
}

