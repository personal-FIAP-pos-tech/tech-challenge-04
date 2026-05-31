package com.viniciuspadovam.tcquatro.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public final class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private JsonUtil() {}

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("JSON invalido.", exception);
        }
    }

    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Falha ao gerar JSON.", exception);
        }
    }

    public static String error(String message) {
        return toJson(Map.of("message", message));
    }
}
