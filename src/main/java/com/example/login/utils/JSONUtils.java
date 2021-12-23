package com.example.login.utils;

import com.example.login.utils.josn.ObjectMapperFactory;
import com.example.login.utils.josn.ObjectMapperPool;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {

    private static final ObjectMapperPool objectMapperPool = new ObjectMapperPool(new ObjectMapperFactory());

    public static ObjectMapper getObjectMapper() throws Exception {
        return objectMapperPool.borrowObject();
    }

    public static void returnObjectMapper(ObjectMapper objectMapper) {
        objectMapperPool.returnObject(objectMapper);
    }

}
