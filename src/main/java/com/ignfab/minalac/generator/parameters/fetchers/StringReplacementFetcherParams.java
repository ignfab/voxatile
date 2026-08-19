package com.ignfab.minalac.generator.parameters.fetchers;

import java.beans.ConstructorProperties;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.fetchers.Fetcher;
import com.ignfab.minalac.generator.fetchers.StringReplacementFetcher;
import com.ignfab.minalac.generator.generation.Generation;

public class StringReplacementFetcherParams extends FetcherParams {
    @JsonSetter(nulls = Nulls.FAIL)
    public FetcherParams fetcher;

    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    public Map<String, String> replacements;

    @ConstructorProperties({ "fetcher", "replacements" })
    public StringReplacementFetcherParams(FetcherParams fetcher, Map<String, String> replacements) {
        this.fetcher = fetcher;
        this.replacements = replacements;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        fetcher.validate();
        if (replacements.isEmpty())
            throw new IllegalArgumentException("'replacements' must not be empty");
    }

    @Override
    public Fetcher create(Generation generation) {
        return new StringReplacementFetcher(fetcher.create(generation), replacements);
    }
}
