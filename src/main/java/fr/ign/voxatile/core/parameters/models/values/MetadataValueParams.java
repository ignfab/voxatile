package fr.ign.voxatile.core.parameters.models.values;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.models.values.MetadataValue;
import fr.ign.voxatile.core.models.values.ModelValue;

/**
 * Parameters for a {@link MetadataValue}.
 */
public class MetadataValueParams extends ModelValueParams {
    /**
     * The name of the metadata.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String metadata;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     * @param metadata the name of the metadata.
     */
    @ConstructorProperties("metadata")
    public MetadataValueParams(String metadata) {
        this.metadata = metadata;
    }

    @Override
    public void validate() {
        if (metadata.isBlank())
            throw new IllegalArgumentException("The 'metadata' field cannot be empty or contain only whitespace.");
    }

    @Override
    public ModelValue create(Generation generation) {
        return new MetadataValue(metadata);
    }
}
