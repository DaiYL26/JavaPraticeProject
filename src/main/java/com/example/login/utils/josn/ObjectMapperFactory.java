package com.example.login.utils.josn;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class ObjectMapperFactory extends BasePooledObjectFactory<ObjectMapper> {
    @Override
    public ObjectMapper create() throws Exception {
        return new ObjectMapper();
    }

    @Override
    public PooledObject<ObjectMapper> wrap(ObjectMapper objectMapper) {
        return new DefaultPooledObject<>(objectMapper);
    }

    @Override
    public boolean validateObject(PooledObject<ObjectMapper> p) {
        return p != null;
    }
}
