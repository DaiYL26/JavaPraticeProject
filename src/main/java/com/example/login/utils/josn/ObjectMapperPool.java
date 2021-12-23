package com.example.login.utils.josn;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class ObjectMapperPool extends GenericObjectPool<ObjectMapper> {

    public ObjectMapperPool(PooledObjectFactory<ObjectMapper> factory) {
        super(factory);
    }
}
