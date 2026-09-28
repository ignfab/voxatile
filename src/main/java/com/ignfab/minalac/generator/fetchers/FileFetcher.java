package com.ignfab.minalac.generator.fetchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

public abstract class FileFetcher implements Fetcher {
    private final CoordinateReferenceSystem crsOverride;

    public FileFetcher(CoordinateReferenceSystem crsOverride) {
        this.crsOverride = crsOverride;
    }

    protected abstract Iterator<File> files(WorldBBox3d bbox);

    @Override
    public FetchResult fetch(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {
        return new FileResult(files(bbox), crsOverride);
    }

    private record FileResult(Iterator<File> iterator, CoordinateReferenceSystem crsHint) implements FetchResult {
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public InputStream next() throws GenerationFailedException {
            File file = iterator.next();
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new GenerationFailedException("File not found: " + file, e);
            }
        }
    }
}
