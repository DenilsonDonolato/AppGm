package com.ads.appgm.mock_api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

public class MockApiHelper {

    private static ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <C> C fromJson(String string, Class<C> clazz) {
        try {
            return mapper.readValue(string, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <C> C fromJson(String string, CollectionType clazz) {
        try {
            return mapper.readValue(string, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <C> CollectionType collectionTypeFor(Class<C> clazz) {
        return mapper.getTypeFactory().constructCollectionType(List.class, clazz);
    }

    private static final SimpleDateFormat timestampf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static String timestampOf(ZonedDateTime dateTime) {
        return timestampf.format(Date.from(dateTime.toInstant()));
    }
}
