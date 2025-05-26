package com.ignfab.minalac.generator.parameters.processors.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.processors.post.MetadataTruncatePostProcessorParams.TruncationMethodParams;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataTruncatePostProcessorParamsTest {
    @Test
    public void testCreate() {
        assertDoesNotThrow(new MetadataTruncatePostProcessorParams("metadata", TruncationMethodParams.ROUND)::create);
    }

    @Test
    public void testValidate() {
        assertDoesNotThrow(
            new MetadataTruncatePostProcessorParams("metadata", TruncationMethodParams.ROUND)::validate
        );

        assertThrows(
            IllegalArgumentException.class,
            new MetadataTruncatePostProcessorParams("", TruncationMethodParams.ROUND)::validate
        );
    }

    @Test
    public void testDeserialize() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(MetadataTruncatePostProcessorParams.class, "truncate"));

        MetadataTruncatePostProcessorParams params = assertDoesNotThrow(() -> mapper.readValue(
            """
                type: truncate
                metadata: tata
                method: round
                ifMissing: discardModel
                ifTruncationFail: removeMetadata
            """,
            MetadataTruncatePostProcessorParams.class
        ));
        assertEquals("tata", params.metadata);
        assertEquals(TruncationMethodParams.ROUND, params.method);
        assertEquals(FailurePolicyParams.DISCARD_MODEL, params.ifMissing);
        assertEquals(FailurePolicyParams.REMOVE_METADATA, params.ifTruncationFail);

        params = assertDoesNotThrow(() -> mapper.readValue(
            """
                type: truncate
                metadata: tata
                method: round
            """,
            MetadataTruncatePostProcessorParams.class
        ));
        assertEquals("tata", params.metadata);
        assertEquals(TruncationMethodParams.ROUND, params.method);
    }
}
