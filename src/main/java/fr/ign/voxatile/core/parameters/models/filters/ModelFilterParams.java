package fr.ign.voxatile.core.parameters.models.filters;

import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.models.Model;

/**
 * Abstract parameter class for all model filters parameters.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(ModelFilterNotParams.class),
    @JsonSubTypes.Type(ModelFilterAndParams.class),
    @JsonSubTypes.Type(ModelFilterOrParams.class),
    @JsonSubTypes.Type(ModelFilterMetadataEqualsParams.class),
    @JsonSubTypes.Type(ModelFilterMetadataInParams.class),
    @JsonSubTypes.Type(ModelFilterHasMetadataParams.class),
    @JsonSubTypes.Type(ModelFilterValueEqualsParams.class),
    @JsonSubTypes.Type(ModelFilterValueLowerThanParams.class),
    @JsonSubTypes.Type(ModelFilterValueGreaterThanParams.class),
    @JsonSubTypes.Type(ModelFilterHasValueParams.class),
    @JsonSubTypes.Type(ModelFilterEmptyGeometryParams.class)
})
public abstract class ModelFilterParams {
    /**
     * Validates params.
     */
    public void validate() {}

    /**
     * Creates a {@code Predicate<Model>} out of these params.
     *
     * @param generation the generation context.
     * @return the resulting predicate.
     */
    public abstract Predicate<Model> create(Generation generation);
}
