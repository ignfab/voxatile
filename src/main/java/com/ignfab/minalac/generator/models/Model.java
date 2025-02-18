package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.utils.random.Salting;

/**
 * Models are objects that can be rendered.
 * They optionally can have some metadata attached to them.
 */
public interface Model extends Salting {
    /**
     * Tells if a metadata exists.
     *
     * @param name Metadata name
     * @return {@code true} if metadata exists with this name
     */
    boolean hasMetadata(String name);

    /**
     * Returns the metadata value associated to the given name.
     *
     * @param name Metadata name
     * @return Metadata value or {@code null} if it does not exist
     * @param <T> Wanted type of return value
     * @throws ClassCastException If value can not be cast to wanted type
     */
    <T> T getMetadata(String name);

    /**
     * Creates or replaces metadata with the given name.
     *
     * @param name  Metadata name to create or update
     * @param value Value to set (existing value is replaced) or {@code null} to
     *              delete metadata
     */
    void setMetadata(String name, Object value);

    /**
     * This method should provide a salt specific to the item represented
     * by this model. It should depend only on the item (its geometry, its
     * attributes), so if fetched again, same item, should always give the
     * same salt.
     *
     * @return a unique but constant salt for this model
     *
     * See docs/development/RandomNumbers.md for more detailed information.
     */
    String salt();
}
