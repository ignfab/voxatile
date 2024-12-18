package com.ignfab.minalac.generator.parameters.processors.post;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ignfab.minalac.generator.parameters.ValueParser;

public class MetadataParsePostProcessorParamsTest {
    private static ObjectMapper mapper;

    @BeforeAll
    public static void init() {
        mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(MetadataParsePostProcessorParams.class, "parse"));
    }

    @Test
    public void testValidate() {
        assertThrows(IllegalArgumentException.class, new MetadataParsePostProcessorParams("", ValueParser.INTEGER)::validate);
        assertDoesNotThrow(new MetadataParsePostProcessorParams("toto", ValueParser.INTEGER)::validate);
    }

    @Test
    public void testCreate() throws JsonProcessingException {
        String parseYaml = """
        type: parse
        metadata: tata
        as: decimal
        ifMissing: discardModel
        ifNotParsable: removeMetadata
        """;

        MetadataParsePostProcessorParams params = mapper.readValue(parseYaml, MetadataParsePostProcessorParams.class);
        assertDoesNotThrow(params::create);

        parseYaml = """
        type: parse
        metadata: tata
        as: decimal
        ifMissing: discardModel
        """;

        params = mapper.readValue(parseYaml, MetadataParsePostProcessorParams.class);
        assertDoesNotThrow(params::create);

        parseYaml = """
        type: parse
        metadata: tata
        as: decimal
        ifNotParsable: removeMetadata
        """;

        params = mapper.readValue(parseYaml, MetadataParsePostProcessorParams.class);
        assertDoesNotThrow(params::create);
    }
}
