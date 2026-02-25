package com.ignfab.minalac.generator.parameters;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.tasks.generic.NoOperationTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.generic.ScheduleTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.generic.SequenceTaskParams;

import static org.junit.jupiter.api.Assertions.*;

public class TileScheduleParamsTest {
    @Test
    void testTaskNames() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(NoOperationTaskParams.class, "task"));
        mapper.registerSubtypes(new NamedType(ScheduleTaskParams.class, "schedule"));
        mapper.registerSubtypes(new NamedType(SequenceTaskParams.class, "sequence"));

        // Check normal situation is ok
        assertDoesNotThrow(() -> ParamsTester.deserialize(TileScheduleParams.class, """
            task:
                type: task
            schedule:
                type: schedule
                do:
                    subtask:
                        type: task
        """, mapper));

        // Invalid root task name
        assertThrows(JsonMappingException.class, () -> ParamsTester.deserialize(TileScheduleParams.class, """
            tas:k:
                type: task
        """, mapper));
/*
        // Invalid schedule subtask name
        assertThrows(JsonMappingException.class, () -> ParamsTester.deserialize(TileScheduleParams.class, """
            schedule:
                type: schedule
                do:
                    sub:task:
                        type: task
        """, mapper));
        */


    }
}
