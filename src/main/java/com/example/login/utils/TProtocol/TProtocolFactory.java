package com.example.login.utils.TProtocol;


import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class TProtocolFactory extends BasePooledObjectFactory<TProtocol> {

    private String host = "localhost";
    private int port = 9090;
    private boolean keepAlive = false;


    @Override
    public TProtocol create() throws Exception {
        TTransport transport = new TSocket(host, port);
        transport.open();
        return new TBinaryProtocol(transport);
    }

    @Override
    public PooledObject<TProtocol> wrap(TProtocol protocol) {
        return new DefaultPooledObject<>(protocol);
    }

    @Override
    public void activateObject(PooledObject<TProtocol> p) throws Exception {
        if (!p.getObject().getTransport().isOpen()) {
            p.getObject().getTransport().open();
        }
    }

    @Override
    public void passivateObject(PooledObject<TProtocol> p) throws Exception {
        if (!keepAlive) {
            p.getObject().getTransport().flush();
            p.getObject().getTransport().close();
        }
    }

    @Override
    public void destroyObject(PooledObject<TProtocol> p) throws Exception {
        passivateObject(p);
        p.markAbandoned();
    }

    @Override
    public boolean validateObject(PooledObject<TProtocol> p) {
        if (p.getObject() != null) {
            if (p.getObject().getTransport().isOpen()) {
                return true;
            }
            try {
                p.getObject().getTransport().open();
                return true;
            } catch (TTransportException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
