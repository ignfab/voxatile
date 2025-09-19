package com.ignfab.minalac.generator.utils;

import java.util.function.Function;

import org.geotools.data.postgis.HStore;
import org.postgresql.util.HStoreConverter;

import com.ignfab.minalac.generator.parameters.ValueParser;

/**
 * {@link ValueParser} for HStore formatted string.
 * @see <a href="https://www.postgresql.org/docs/current/hstore.html">HStore format</a>
 */
public final class HStoreValueParser implements Function<Object, HStore> {
    /**
     * Simple instance ready to be registered.
     */
    public static final ValueParser<HStore> INSTANCE = new ValueParser<>(HStore.class, new HStoreValueParser());

    /**
     * @see #INSTANCE
     */
    private HStoreValueParser() {}

    @Override
    public HStore apply(Object obj) {
        return new HStore(HStoreConverter.fromString(obj.toString()));
    }
}
