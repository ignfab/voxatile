package com.ignfab.minalac.generator.parameters.tasks;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.dataformat.yaml.YAMLMapper;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.processors.ProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.TestingProcessorParams;
import com.ignfab.minalac.generator.parameters.providers.ProviderParams;

import static org.junit.jupiter.api.Assertions.*;

public class FetchDataTaskParamsTest {

    @Test
    public void testValidate() {
        FetchDataTaskParams params;

        params = new FetchDataTaskParams("dummy", new TestingProviderWithoutDefaultParams());
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new FetchDataTaskParams("dummy", new TestingProviderWithoutDefaultParams());
        params.processor = TestingProcessorParams.VALID;
        assertDoesNotThrow(params::validate);

        params = new FetchDataTaskParams("dummy", new TestingProviderWithDefaultParams());
        assertDoesNotThrow(params::validate);

        params = new FetchDataTaskParams(" ", new TestingProviderWithDefaultParams());
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new FetchDataTaskParams("dummy", new TestingProviderWithoutDefaultParams());
        params.processor = TestingProcessorParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new FetchDataTaskParams("dummy", TestingProviderWithoutDefaultParams.INVALID);
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testDefaultProcessor() {
        MapperBuilder<?, ?> builder = YAMLMapper.builder()
            .registerSubtypes(new NamedType(FetchDataTaskParams.class, "fetchData"))
            .registerSubtypes(new NamedType(TestingProviderWithoutDefaultParams.class, "testProviderWithoutDefault"))
            .registerSubtypes(new NamedType(TestingProviderWithDefaultParams.class, "testProviderWithDefault"))
            .registerSubtypes(new NamedType(TestingProcessorParams.class, "testProcessor"));

        FetchDataTaskParams params;

        params = assertDoesNotThrow(() -> ParamsTester.deserialize(FetchDataTaskParams.class, """
            type: fetchData
            modelType: test
            provider:
                type: testProviderWithoutDefault
        """, builder));

        assertNotNull(params.provider);
        assertNull(params.processor);

        params = assertDoesNotThrow(() -> ParamsTester.deserialize(FetchDataTaskParams.class, """
            type: fetchData
            modelType: test
            provider:
                type: testProviderWithoutDefault
            processor:
                type: testProcessor
        """, builder));

        assertNotNull(params.provider);
        assertNotNull(params.processor);

        params = assertDoesNotThrow(() -> ParamsTester.deserialize(FetchDataTaskParams.class, """
            type: fetchData
            modelType: test
            provider:
                type: testProviderWithDefault
        """, builder));

        assertNotNull(params.provider);
        assertEquals(TestingProviderWithDefaultParams.DEFAULT_PROCESSOR, params.processor);

        params = assertDoesNotThrow(() -> ParamsTester.deserialize(FetchDataTaskParams.class, """
            type: fetchData
            modelType: test
            provider:
                type: testProviderWithDefault
            processor:
                type: testProcessor
        """, builder));

        assertNotNull(params.provider);
        assertNotNull(params.processor);
        assertNotEquals(TestingProviderWithDefaultParams.DEFAULT_PROCESSOR, params.processor);
    }

    // Params for a test provider without default processor. Could be valid (default) or invalid.
    private static class TestingProviderWithoutDefaultParams extends ProviderParams {
        private static final TestingProviderWithoutDefaultParams INVALID = new TestingProviderWithoutDefaultParams(false);

        private final boolean valid;

        private TestingProviderWithoutDefaultParams(boolean valid) {
            this.valid = valid;
        }

        TestingProviderWithoutDefaultParams() {
            this(true);
        }

        @Override
        public Provider<?> create(Generation generation) {
            throw new UnsupportedOperationException("Unimplemented method 'create'");
        }

        @Override
        public ProcessorParams defaultProcessor() {
            return null;
        }

        @Override
        public void validate() {
            if (!valid)
                throw new IllegalArgumentException("Invalid test processor params");
        }
    }

    // Params for a test provider with default processor. Always valid.
    private static final class TestingProviderWithDefaultParams extends TestingProviderWithoutDefaultParams {
        private static final TestingProcessorParams DEFAULT_PROCESSOR = new TestingProcessorParams();

        @Override
        public ProcessorParams defaultProcessor() {
            return DEFAULT_PROCESSOR;
        }
    }
}
