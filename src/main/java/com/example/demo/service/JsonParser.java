package com.example.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class JsonParser {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Object> parseJson(String json) throws Exception {
        return mapper.readValue(json, Map.class);
    }
}