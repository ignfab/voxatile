package fr.ign.voxatile.core.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.parameters.heightmaps.WritableHeightmapParams;
import fr.ign.voxatile.core.tasks.PopulateHeightmapTask;
import fr.ign.voxatile.core.tasks.TileTask;

/**
 * Parameters for creating a {@link PopulateHeightmapTask}.
 */
public class PopulateHeightmapTaskParams extends ModelTaskParams {
    /**
     * The name of the heightmap to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public WritableHeightmapParams heightmap;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param heightmap the name of the heightmap to use.
     */
    @ConstructorProperties({"heightmap"})
    public PopulateHeightmapTaskParams(WritableHeightmapParams heightmap) {
        this.heightmap = heightmap;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        heightmap.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new PopulateHeightmapTask(
            models.create(generation),
            heightmap.create(generation.heightmaps()),
            generation.getVerticalScale()
        );
    }
}
