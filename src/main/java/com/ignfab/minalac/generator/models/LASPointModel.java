package com.ignfab.minalac.generator.models;

import com.github.mreutegg.laszip4j.LASHeader;
import com.github.mreutegg.laszip4j.LASPoint;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.coordinates.MapCoordinates;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.Voxelizer2d;
import com.ignfab.minalac.generator.voxelization.Voxelizer3d;

import java.util.Collections;

public class LASPointModel extends Model implements Voxelizable2d, Voxelizable3d {
    private final WorldCoords2d coords;
    private final int z;

    public LASPointModel(LASHeader header, LASPoint point, MapToWorldConverter converter) throws TransformException {
        coords = converter.convert(new MapCoordinates(
            header.getXOffset() + point.getX() * header.getXScaleFactor(),
            header.getYOffset() + point.getY() * header.getYScaleFactor()
        ));
        z = (int) Math.round(header.getZOffset() + point.getZ() * header.getZScaleFactor());
        setMetadata("classification", Short.toString(point.getClassification()));
        if (point.hasRGB()) {
            int color = (point.getRed() & 0xFF) << 16 | (point.getGreen() & 0xFF) << 8 | (point.getBlue() & 0xFF);
            setMetadata("color", color);
        }
    }

    @Override
    public String salt() {
        return "%d/%d/%d".formatted(coords.x(), coords.y(), z);
    }

    @Override
    public Voxelizer2d voxelize2d(WorldBBox2d bbox) {
        return () -> bbox.contains(coords) ? Iterators.singleton(coords) : Collections.emptyIterator();
    }

    @Override
    public Voxelizer3d voxelize3d(WorldBBox3d bbox) {
        WorldCoords3d coords = this.coords.to3d(z);
        return () -> bbox.contains(coords) ? Iterators.singleton(coords) : Collections.emptyIterator();
    }
}
