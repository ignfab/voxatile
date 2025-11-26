package com.ignfab.minalac.generator.parameters.tasks;

import java.awt.Color;
import java.beans.ConstructorProperties;
import java.io.File;
import java.nio.file.Path;
import javax.imageio.ImageIO;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.tasks.NoOperationTask;
import com.ignfab.minalac.generator.tasks.SaveMinimapTask;
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * Parameters for {@link SaveMinimapTask}.
 */
public class SaveMinimapTaskParams extends TaskParams {

    /**
     * Name of the minimap to save (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String minimap;

    /**
     * Relative path where the rendered minimap image will be saved (required).
     *
     * <p>
     * This path is relative to the world's root directory.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public File destination;

    /**
     * Format of the minimap to render (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String format;

    /**
     * Default background color for the minimap (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public Color background;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param minimap minimap to render
     * @param destination image destination relative to the world directory
     * @param format image format to save
     */
    @ConstructorProperties({ "minimap", "destination", "format" })
    public SaveMinimapTaskParams(String minimap, File destination, String format) {
        this.minimap = minimap;
        this.destination = destination;
        this.format = format;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (minimap.isBlank())
            throw new IllegalArgumentException("Minimap name cannot be empty or blank");

        if (format.isBlank())
            throw new IllegalArgumentException("Format cannot be empty or blank");
        if (!ImageIO.getImageWritersByFormatName(format).hasNext())
            throw new IllegalArgumentException("The format '" + format + "' is not supported");
        if (!destination.getName().toLowerCase().contains(format.toLowerCase()))
            throw new IllegalArgumentException("Destination must contain the format '" + format + "' extension");

        if (destination.getName().isBlank())
            throw new IllegalArgumentException("Destination name cannot be empty or blank");
        Path destinationPath = destination.toPath();
        if (destinationPath.isAbsolute())
            throw new IllegalArgumentException("Destination must be a relative path");
    }

    @Override
    public Task create(Generation generation) {
        // If the world has no destination, means the save is disabled.
        if (generation.world().destination() == null)
            return NoOperationTask.INSTANCE;

        File destination = generation.world().destination().toPath().resolve(this.destination.toPath()).toFile();
        return new SaveMinimapTask(generation.minimaps().get(minimap), destination, format, background);
    }
}
