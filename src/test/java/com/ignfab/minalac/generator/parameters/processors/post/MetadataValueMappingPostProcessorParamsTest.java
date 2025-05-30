package com.ignfab.minalac.generator.parameters.processors.post;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataValueMappingPostProcessorParamsTest {

    @Test
    public void testDeserialize() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(MetadataValueMappingPostProcessorParams.class, "remap"));

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
        MetadataValueMappingPostProcessorParams params;
        params = new MetadataValueMappingPostProcessorParams("metadata");
        params.fromTo = Map.of("a", "b", "c", "d");
        assertDoesNotThrow(params::create);

        params = new MetadataValueMappingPostProcessorParams("metadata");
        params.fromTo = Map.of("a", "b", "c", "d");
        params.toFrom = Map.of("b", Set.of("a", "c", "d"), "e", Set.of("f", "g", "h"));
        assertThrows(IllegalArgumentException.class, params::create);

        params = new MetadataValueMappingPostProcessorParams("metadata");
        params.toFrom = Map.of("b", Set.of("a", "c", "d"), "e", Set.of("a", "c", "d"));
        assertThrows(IllegalArgumentException.class, params::create);
    }
}
