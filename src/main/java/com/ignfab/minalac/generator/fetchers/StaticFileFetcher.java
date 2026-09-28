package com.ignfab.minalac.generator.fetchers;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

public class StaticFileFetcher extends FileFetcher {
    private final List<File> files;

    public StaticFileFetcher(List<File> files, CoordinateReferenceSystem crs) {
        super(crs);
        this.files = files;
    }

    @Override
    protected Iterator<File> files(WorldBBox3d bbox) {
        return files.iterator();
    }
}
