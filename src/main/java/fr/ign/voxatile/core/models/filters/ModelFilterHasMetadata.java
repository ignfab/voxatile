package fr.ign.voxatile.core.models.filters;

import java.util.function.Predicate;

import fr.ign.voxatile.core.models.Model;

/**
 * A model filter testing presence of metadata.
 */
public class ModelFilterHasMetadata implements Predicate<Model> {

    private final String name;

    /**
     * Creates a new {@code ModelFilterHasMetadata}.
     *
     * @param name name of metadata to check.
     */
    public ModelFilterHasMetadata(String name) {
        this.name = name;
    }

    @Override
    public boolean test(Model model) {
        return model.hasMetadata(name);
    }
}
