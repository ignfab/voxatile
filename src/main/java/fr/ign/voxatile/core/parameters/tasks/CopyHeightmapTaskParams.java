package fr.ign.voxatile.core.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.parameters.heightmaps.ReadableHeightmapParams;
import fr.ign.voxatile.core.parameters.heightmaps.WritableHeightmapParams;
import fr.ign.voxatile.core.tasks.CopyHeightmapTask;
import fr.ign.voxatile.core.tasks.TileTask;

/**
 * Parameters for creating a {@link CopyHeightmapTask}.
 */
public class CopyHeightmapTaskParams extends ModelTaskParams {
    /**
     * The copied heightmap (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams from;

    /**
     * The name of the heightmap receiving the values (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public WritableHeightmapParams to;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param from the copied heightmap
     * @param to the name of the heightmap receiving the values.
     */
    @ConstructorProperties({"from", "to"})
    public CopyHeightmapTaskParams(ReadableHeightmapParams from, WritableHeightmapParams to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void validate() {
        super.validate();
        from.validate();
        to.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new CopyHeightmapTask(
            models.create(generation),
            from.create(generation.heightmaps()),
            to.create(generation.heightmaps())
        );
    }
}
