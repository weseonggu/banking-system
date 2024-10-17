package com.msa.banking.common.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventSerializer {

    private static final Logger logger = LoggerFactory.getLogger(EventSerializer.class);
    private static final ObjectMapper objectMapper;

    static {
        // ObjectMapper에 JavaTimeModule 등록
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Java 8 날짜/시간 처리를 위한 모듈 등록
    }

    // 직렬화 (byte -> JSON 바이트 배열)
    public static <T> byte[] serialize(T object) {
        try {
            return objectMapper.writeValueAsBytes(object); // JSON 문자열을 바이트 배열로 직렬화
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize object: {}", object, e);
            throw new RuntimeException("Serialization error", e);
        }
    }

    // 역직렬화 (JSON 문자열 -> 객체)
    public static <T> T deserialize(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz); // JSON 문자열을 객체로 역직렬화
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize JSON: {}", json, e);
            throw new RuntimeException("Deserialization error", e);
        }
    }
}
