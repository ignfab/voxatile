package com.ignfab.minalac.generator.parameters.processors.post;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataValueMappingPostProcessorParamsTest {

    @Test
    public void testDeserialize() throws JacksonException {
        ObjectMapper mapper = ParamsTester.mapperWithParams("remap", MetadataValueMappingPostProcessorParams.class);

        assertDoesNotThrow(() -> mapper.readValue("""
        type: remap
        metadata: tata
        fromTo:
          a: b
        """, MetadataValueMappingPostProcessorParams.class));

        assertDoesNotThrow(() -> mapper.readValue("""
        type: remap
        metadata: tata
        toFrom:
          a: [b]
        """, MetadataValueMappingPostProcessorParams.class));

        assertDoesNotThrow(() -> mapper.readValue("""
        type: remap
        metadata: tata
        ifMissing: removeMetadata
        toFrom:
          a: b
          c: [x, y, z]
        fromTo:
          d: k
          e: j
        default: p
        as: text
        ifNoMatchFound: ignore
        """, MetadataValueMappingPostProcessorParams.class));
    }

    @Test
    public void testValidate() {
        MetadataValueMappingPostProcessorParams params = new MetadataValueMappingPostProcessorParams(" ");
        params.fromTo = new HashMap<>();
        params.toFrom = new HashMap<>();
        assertThrows(
            IllegalArgumentException.class,
            params::validate
        );

        // 'toFrom' and 'fromTo' attributes is null
        assertThrows(
          IllegalArgumentException.class,
          new MetadataValueMappingPostProcessorParams("metadata")::validate
        );

        params = new MetadataValueMappingPostProcessorParams("metadata");
        params.toFrom = Map.of("b", Set.of("a", "c", "d"), "e", Set.of("f", "g", "h"));
        params.fromTo = new HashMap<>();
        assertDoesNotThrow(params::validate);

        params = new MetadataValueMappingPostProcessorParams("metadata");
        params.toFrom = Map.of("b", Set.of("a", "c", "d"), "e", Set.of("f", "g", "h"));
        params.fromTo = new HashMap<>();
        params.defaultValue = "a";
        assertDoesNotThrow(params::validate);

        params = new MetadataValueMappingPostProcessorParams("metadata");
        params.toFrom = Map.of("b", Set.of("a", "c", "d"), "e", Set.of("f", "g", "h"));
        params.fromTo = new HashMap<>();
        params.defaultValue = "a";
        params.ifNoMatchFound = FailurePolicyParams.ERROR;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new MetadataValueMappingPostProcessorParams("metadata");
        params.toFrom = Map.of("b", Set.of("a", "c", "d"), "e", Set.of("f", "g", "h"));
        assertDoesNotThrow(params::validate);

        // for-each loop test
        params = new MetadataValueMappingPostProcessorParams("metadata");
        params.toFrom = Map.of("a", Set.of(), "b", Set.of("c", "d", "e"));
        assertThrows(
            IllegalArgumentException.class,
            params::validate
        );

        params = new MetadataValueMappingPostProcessorParams("metadata");
        params.fromTo = new HashMap<>();
        assertDoesNotThrow(params::validate);
    }

    @Test
    public void testCreate() {
        MetadataValueMappingPostProcessorParams params1 = new MetadataValueMappingPostProcessorParams("metadata");
        params1.fromTo = Map.of("a", "b", "c", "d");
        assertDoesNotThrow(() -> params1.create(TestingGeneration.UNUSED));

        MetadataValueMappingPostProcessorParams params2 = new MetadataValueMappingPostProcessorParams("metadata");
        params2.fromTo = Map.of("a", "b", "c", "d");
        params2.defaultValue = "z";
        assertDoesNotThrow(() -> params2.create(TestingGeneration.UNUSED));

        MetadataValueMappingPostProcessorParams params3 = new MetadataValueMappingPostProcessorParams("metadata");
        params3.fromTo = Map.of("a", "b", "c", "d");
        params3.toFrom = Map.of("b", Set.of("a", "c", "d"), "e", Set.of("f", "g", "h"));
        assertThrows(IllegalArgumentException.class, () -> params3.create(TestingGeneration.UNUSED));

        MetadataValueMappingPostProcessorParams params4 = new MetadataValueMappingPostProcessorParams("metadata");
        params4.toFrom = Map.of("b", Set.of("a", "c", "d"), "e", Set.of("a", "c", "d"));
        assertThrows(IllegalArgumentException.class, () -> params4.create(TestingGeneration.UNUSED));
    }
}
