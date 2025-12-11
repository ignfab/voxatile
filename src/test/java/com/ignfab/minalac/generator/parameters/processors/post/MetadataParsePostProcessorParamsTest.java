package com.ignfab.minalac.generator.parameters.processors.post;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.ValueParser;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataParsePostProcessorParamsTest {
    @Test
    public void testValidate() {
        assertThrows(IllegalArgumentException.class, new MetadataParsePostProcessorParams("", ValueParser.INTEGER)::validate);
        assertDoesNotThrow(new MetadataParsePostProcessorParams("toto", ValueParser.INTEGER)::validate);
    }

    @Test
    public void testCreate() throws JacksonException {
        ObjectMapper mapper = ParamsTester.mapperWithParams("parse", MetadataParsePostProcessorParams.class);

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
