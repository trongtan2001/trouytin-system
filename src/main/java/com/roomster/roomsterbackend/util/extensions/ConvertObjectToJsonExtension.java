package com.roomster.roomsterbackend.util.extensions;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Extension to convert object to json string
 * **/
public class ConvertObjectToJsonExtension {
    public static String convertToJson(Object request) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(request);
    }
}
