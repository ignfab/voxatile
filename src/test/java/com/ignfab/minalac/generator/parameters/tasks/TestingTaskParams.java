package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.TestingModel;
import com.ignfab.minalac.generator.tasks.ModelTask;
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * A TileTaskParams class for testing purposes.
 */
public class TestingTaskParams extends ModelTaskParams {

    /**
     * A valid task params for tests.
     */
    public static final TestingTaskParams VALID = new TestingTaskParams();

    /**
     * An invalid task params for tests.
     */
    public static final TestingTaskParams INVALID = new TestingTaskParams(null);

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
     * <p>
     * A default model selection is set.
     *
     * @param requiredField the required field.
     */
    @ConstructorProperties({"requiredField"})
    public TestingTaskParams(String requiredField) {
        this.requiredField = requiredField;
        models.type = "dummy";
    }

    /**
     * Constructs a valid {@code TestingTaskParams} with no arguments.
     */
    public TestingTaskParams() {
        this.requiredField = "dummy";
    }

    public void validate() {
        super.validate();

        if (requiredField == null)
            throw new IllegalArgumentException();
    }

    @Override
    public Task create(Generation generation) {
        return new DummyTask(models.create(generation));
    }

    /**
     * Dummy task for tests.
     */
    private static class DummyTask extends ModelTask<TestingModel> {

        protected DummyTask(ModelSelection selection) {
            super(TestingModel.class, selection);
        }

        @Override
        public void run(TestingModel model, GenerationTile tile) {}
    }
}
