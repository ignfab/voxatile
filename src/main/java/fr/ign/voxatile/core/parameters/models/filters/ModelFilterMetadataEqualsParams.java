package fr.ign.voxatile.core.parameters.models.filters;

import java.beans.ConstructorProperties;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.models.Model;
import fr.ign.voxatile.core.models.filters.ModelFilterOnMetadataValue;
import fr.ign.voxatile.core.parameters.ValueParser;

/**
 * Parameters for an "equals" operator on metadata value.
 */
// TODO once model values can handle any type of values, this won't be necessary anymore
public class ModelFilterMetadataEqualsParams extends ModelFilterParams {

    /**
     * Name of the metadata to test (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String metadata;

    /**
     * Value of metadata to test (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public Object equals;

    /**
     * Type of that value (optional, default "string").
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ValueParser<?> as = ValueParser.STRING;

    /**
     * Creates a new {@code ModelFilterMetadataEqualsParams}.
     *
     * @param metadata name of the metadata to test.
     * @param equals value of metadata to test.
     */
    @ConstructorProperties({"metadata", "equals"})
    public ModelFilterMetadataEqualsParams(String metadata, Object equals) {
        this.metadata = metadata;
        this.equals = equals;
    }

    @Override
    public void validate() {
        if (metadata.isBlank())
            throw new IllegalArgumentException("Metadata name cannot be empty or blank");
    }

    @Override
    public Predicate<Model> create(Generation generation) {
        Object equals = as.parse(this.equals);
        return new ModelFilterOnMetadataValue<>(as.type(), metadata, equals::equals);
    }
}
