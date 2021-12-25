package com.example.login.utils;

import com.example.login.utils.TProtocol.TProtocolConfigSingleton;
import com.example.login.utils.TProtocol.TProtocolFactory;
import com.example.login.utils.TProtocol.TProtocolPool;
import org.apache.thrift.protocol.TProtocol;

public class TProtocolUtils {

    private static final TProtocolPool pool = new TProtocolPool(new TProtocolFactory(), TProtocolConfigSingleton.getInstance());

    public static TProtocol borrowTProtocol() throws Exception {
        return pool.borrowObject();
    }

    public static void returnObject(TProtocol tProtocol) {
        pool.returnObject(tProtocol);
    }

}
