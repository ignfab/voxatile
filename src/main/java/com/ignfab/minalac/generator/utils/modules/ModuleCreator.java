package com.ignfab.minalac.generator.utils.modules;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;

/**
 * Interface for all classes capable of creating a module.
 */
public interface ModuleCreator {
    /**
     * Creates a module.
     *
     * @return created module
     */
    Module create() throws GenerationFailedException;
}
