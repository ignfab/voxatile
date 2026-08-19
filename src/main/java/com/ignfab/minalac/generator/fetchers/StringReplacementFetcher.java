package com.ignfab.minalac.generator.fetchers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

public class StringReplacementFetcher implements Fetcher {
    private final Fetcher fetcher;
    private final Map<String, String> replacements;

    public StringReplacementFetcher(Fetcher fetcher, Map<String, String> replacements) {
        this.fetcher = fetcher;
        this.replacements = replacements;
    }

    @Override
    public FetchResult fetch(WorldBBox3d bbox) throws RetryableException, GenerationFailedException {
        return new ReplacingResult(fetcher.fetch(bbox));
    }

    private class ReplacingResult extends ModifyingResult {
        public ReplacingResult(FetchResult delegate) {
            super(delegate);
        }

        @Override
        protected InputStream modify(InputStream stream) throws RetryableException {
            // This code is a workaround and no attention is given to its (bad) performance
            byte[] bytes;
            try {
                bytes = stream.readAllBytes();
                stream.close();
            } catch (IOException e) {
                throw new RetryableException("Error reading stream data", e);
            }

            String string = new String(bytes, StandardCharsets.UTF_8);
            for (Map.Entry<String, String> entry : replacements.entrySet())
                string = string.replace(entry.getKey(), entry.getValue());
            return new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
        }
    }
}
