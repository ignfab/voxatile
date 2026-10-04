package fr.ign.voxatile.core.parameters.providers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.inputs.Provider;
import fr.ign.voxatile.core.inputs.TestingProvider;
import fr.ign.voxatile.core.parameters.processors.ProcessorParams;
import fr.ign.voxatile.core.parameters.processors.TestingProcessorParams;

public class TestingProviderParams extends ProviderParams {
    /**
     * A required field.
     */
    public String requiredField;
    /**
     * An optional field.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String optionalField = "defaultOptionalValue";

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param requiredField the required field.
     */
    @ConstructorProperties({"requiredField"})
    public TestingProviderParams(String requiredField) {
        this.requiredField = requiredField;
    }

    @Override
    public Provider<?> create(Generation generation) {
        return new TestingProvider(generation.crs());
    }

    @Override
    public ProcessorParams defaultProcessor() {
        return new TestingProcessorParams();
    }
}
