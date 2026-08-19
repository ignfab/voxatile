package com.ignfab.minalac.generator.parameters.fetchers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;

import com.ignfab.minalac.generator.fetchers.Fetcher;
import com.ignfab.minalac.generator.fetchers.FileFetcher;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.utils.FileHelpers;

public class FileFetcherParams extends FetcherParams {
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<String> files;

    /**
     * Coordinate reference system (optional, default: automatically computed by provider).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String crsOverride;

    @Override
    public void validate() throws IllegalArgumentException {
        if (files.isEmpty())
            throw new IllegalArgumentException("'files' must not be empty");
    }

    @Override
    public Fetcher create(Generation generation) {
        List<File> files = new ArrayList<>(this.files.size());
        for (String filePath : this.files) {
            File file = new File(filePath);
            if (!FileHelpers.isReadableRegularFile(file))
                throw new IllegalArgumentException("File \"%s\" does not exist".formatted(file.getAbsolutePath()));
            files.add(file);
        }

        CoordinateReferenceSystem crs;
        if (crsOverride == null)
            crs = null;
        else {
            try {
                crs = CRS.decode(crsOverride);
            } catch (FactoryException e) {
                throw new IllegalArgumentException("CRS code \"%s\" is invalid".formatted(crsOverride), e);
            }
        }

        return new FileFetcher(files, crs);
    }
}
