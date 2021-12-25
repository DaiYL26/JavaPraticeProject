package com.example.login.utils.TProtocol;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.protocol.TProtocol;

public class TProtocolConfigSingleton extends GenericObjectPoolConfig<TProtocol> {

    private static final GenericObjectPoolConfig<TProtocol> config = new GenericObjectPoolConfig<>();

    static {
        config.setTestOnBorrow(true);
        config.setMaxTotal(8);
    }

    private TProtocolConfigSingleton() {

    }

    public static GenericObjectPoolConfig<TProtocol> getInstance() {
        return config;
    }

}
