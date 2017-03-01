package com.jackis.elasticsearch;

import java.text.DateFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;

public class ObjectMapperFactory {

    private static ObjectMapper INSTANCE;

    public synchronized static ObjectMapper getObjectMapper() {
        if (INSTANCE == null) {
            INSTANCE = new ObjectMapper();

            INSTANCE.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            INSTANCE.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            INSTANCE.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            INSTANCE.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            INSTANCE.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
            INSTANCE.configure(SerializationFeature.INDENT_OUTPUT, true);
        }
        return INSTANCE;

    }

}
