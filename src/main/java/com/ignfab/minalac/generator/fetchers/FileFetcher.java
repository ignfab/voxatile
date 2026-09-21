package com.ignfab.minalac.generator.fetchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

public class FileFetcher implements Fetcher {
    private final List<File> files;
    private final CoordinateReferenceSystem crs;

    public FileFetcher(List<File> files, CoordinateReferenceSystem crs) {
        this.files = files;
        this.crs = crs;
    }

    @Override
    public FetchResult fetch(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {
        return new FileResult(files.iterator(), crs);
    }

    private record FileResult(Iterator<File> iterator, CoordinateReferenceSystem crs) implements FetchResult {
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
