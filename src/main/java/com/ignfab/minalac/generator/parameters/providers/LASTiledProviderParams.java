package com.ignfab.minalac.generator.parameters.providers;

import java.beans.ConstructorProperties;
import java.io.File;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.LASPointAndHeader;
import com.ignfab.minalac.generator.inputs.LASTiledDataProvider;
import com.ignfab.minalac.generator.inputs.Provider;

/**
 * Parameters for LAS tiled providers.
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
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
     * Coordinate reference system of points inside file (required).
     */
    public String crs;

    /**
     * Creates a new LASProviderParams with mandatory fields.
     *
     * @param tilesPath Path to LAS/LAZ tiles directory (absolute, or relative to current execution context)
     * @param tilesFilenameTemplate Template of tiles filename (will be formatted with x and y of the tile)
     * @param tileSize Size of tiles
     * @param crs Coordinate reference system of the points inside the file
     */
    @ConstructorProperties({"tilesPath", "tilesFilenameTemplate", "tileSize", "crs"})
    public LASTiledProviderParams(String tilesPath, String tilesFilenameTemplate, double tileSize, String crs) {
        this.tilesPath = tilesPath;
        this.tilesFilenameTemplate = tilesFilenameTemplate;
        this.tileSize = tileSize;
        this.crs = crs;
    }

    @Override
    public Provider<LASPointAndHeader> create(Generation generation) {
        CoordinateReferenceSystem layerCrs;
        try {
            layerCrs = CRS.decode(crs);
        } catch (FactoryException e) {
            throw new IllegalArgumentException("CRS code \"%s\" is invalid".formatted(crs), e);
        }

        ReferencedEnvelope envelope;
        try {
            envelope = generation.getEnvelopeForCRS(layerCrs);
        } catch (FactoryException | TransformException e) {
            throw new IllegalArgumentException("Unable to compute envelope for given CRS", e);
        }

        File tiles = new File(tilesPath);
        if (!tiles.isDirectory())
            throw new IllegalArgumentException("Directory \"%s\" does not exist".formatted(tiles.getAbsolutePath()));

        return new LASTiledDataProvider(tiles, tilesFilenameTemplate, tileSize, tileOffsetX, tileOffsetY, envelope);
    }
}
