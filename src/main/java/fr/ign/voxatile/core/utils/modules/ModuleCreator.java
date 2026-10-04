package fr.ign.voxatile.core.utils.modules;

import fr.ign.voxatile.core.exceptions.GenerationFailedException;

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
