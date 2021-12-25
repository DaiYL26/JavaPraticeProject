package com.example.login.utils.TProtocol;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.protocol.TProtocol;

public class TProtocolPool extends GenericObjectPool<TProtocol> {

    public TProtocolPool(PooledObjectFactory<TProtocol> factory, GenericObjectPoolConfig<TProtocol> config) {
        super(factory, config);
    }

    @Override
    public void returnObject(TProtocol obj) {
        super.returnObject(obj);
        if (getNumIdle() >= getNumActive()) {
            clear();
        }
    }
}
