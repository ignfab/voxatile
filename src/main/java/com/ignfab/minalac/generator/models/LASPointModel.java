package com.ignfab.minalac.generator.models;

import java.util.Collections;

import com.github.mreutegg.laszip4j.LASHeader;
import com.github.mreutegg.laszip4j.LASPoint;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.coordinates.MapCoordinates3d;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.Voxelizer2d;
import com.ignfab.minalac.generator.voxelization.Voxelizer3d;

/**
 * Model containing a single lidar point.
 * @see LASMergedModel
 */
public class LASPointModel extends ModelImpl implements Voxelizable2d, Voxelizable3d {
    private final WorldCoords3d coords;

    /**
     * Creates a new {@code LASPointModel}.
     * @param header the LAS header to decode point data
     * @param point the LAS point
     * @param converter the converter
     * @throws TransformException if point coordinates cannot be converted
     */
    public LASPointModel(LASHeader header, LASPoint point, MapToWorldConverter converter) throws TransformException {
        coords = converter.convert(new MapCoordinates3d(
            header.getXOffset() + point.getX() * header.getXScaleFactor(),
            header.getYOffset() + point.getY() * header.getYScaleFactor(),
            header.getZOffset() + point.getZ() * header.getZScaleFactor()
        ));
        setMetadata("classification", (int) point.getClassification());
        if (point.hasRGB()) {
            int color = (point.getRed() & 0xFF) << 16 | (point.getGreen() & 0xFF) << 8 | (point.getBlue() & 0xFF);
            setMetadata("color", color);
        }
    }

    @Override
    public String salt() {
        return "%d/%d/%d".formatted(coords.x(), coords.y(), coords.z());
    }

    @Override
    public Voxelizer2d voxelize2d(WorldBBox2d bbox) {
        WorldCoords2d coords = this.coords.to2d();
        return () -> bbox.contains(coords) ? Iterators.singleton(coords) : Collections.emptyIterator();
    }

    @Override
    public Voxelizer3d voxelize3d(WorldBBox3d bbox) {
        return () -> bbox.contains(coords) ? Iterators.singleton(coords) : Collections.emptyIterator();
    }
}
