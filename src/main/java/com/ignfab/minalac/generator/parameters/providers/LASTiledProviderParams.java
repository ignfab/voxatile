package com.ignfab.minalac.generator.parameters.providers;

import java.beans.ConstructorProperties;
import java.io.File;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.LASPointAndHeader;
import com.ignfab.minalac.generator.inputs.LASTiledDataProvider;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.parameters.processors.ProcessorParams;

/**
 * Parameters for LAS tiled providers.
 */
public class LASTiledProviderParams extends ProviderParams {
    /**
     * Path to LAS/LAZ tiles directory (required).
     */
    public String tilesPath;

    /**
     * Template of each tile's filename (required).
     */
    public String tilesFilenameTemplate;

    /**
     * Size of each tile (required).
     */
    public double tileSize;

    /**
     * Offset of tiles along the X-axis (optional, defaults to 0).
     */
    public double tileOffsetX;

    /**
     * Offset of tiles along the Y-axis (optional, defaults to 0).
     */
    public double tileOffsetY;

    /**
     * Coordinate reference system to use when reading data (optional, default: none).
     * By default, the CRS is read from any LAS/LAZ file found. You should only use this
     * parameter if the CRS is invalid or missing from the files.
     * This DOES NOT reproject data!
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String crsOverride;

    /**
     * Creates a new LASProviderParams with mandatory fields.
     *
     * @param tilesPath Path to LAS/LAZ tiles directory (absolute, or relative to current execution context)
     * @param tilesFilenameTemplate Template of tiles filename (will be formatted with x and y of the tile)
     * @param tileSize Size of tiles
     */
    @ConstructorProperties({"tilesPath", "tilesFilenameTemplate", "tileSize"})
    public LASTiledProviderParams(String tilesPath, String tilesFilenameTemplate, double tileSize) {
        this.tilesPath = tilesPath;
        this.tilesFilenameTemplate = tilesFilenameTemplate;
        this.tileSize = tileSize;
    }

    @Override
    public Provider<LASPointAndHeader> create(Generation generation) {
        CoordinateReferenceSystem crsOverride;
        if (this.crsOverride != null)
            try {
                crsOverride = CRS.decode(this.crsOverride);
            } catch (FactoryException e) {
                throw new IllegalArgumentException("CRS code \"%s\" is invalid".formatted(this.crsOverride), e);
            }
        else
            crsOverride = null;

        File tiles = new File(tilesPath);
        if (!tiles.isDirectory())
            throw new IllegalArgumentException("Directory \"%s\" does not exist".formatted(tiles.getAbsolutePath()));

        return new LASTiledDataProvider(tiles, tilesFilenameTemplate, tileSize, tileOffsetX, tileOffsetY, crsOverride, generation::getEnvelopeForCRS);
    }

    @Override
    public ProcessorParams defaultProcessor() {
        return null;
    }
}
