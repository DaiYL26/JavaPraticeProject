package com.example.login.service.impl;

import com.example.login.rpcservice.QueryHandler;
import com.example.login.service.SearchService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {

    @Override
    public String queryForEn(String word) throws TException {
        TTransport transport = null;
        String res = null;
        try {
            transport = new TSocket("localhost", 9090);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            QueryHandler.Client client = new QueryHandler.Client(protocol);
            // 按英文查
            res = client.query(word);
        } catch (TException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (transport != null) {
                transport.close();
            }
        }
        return res;
    }

    @Override
    public String queryForZh(String mean, Integer limit) throws TException {
        TTransport transport = null;
        String res = null;
        try {
            transport = new TSocket("localhost", 9090);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            QueryHandler.Client client = new QueryHandler.Client(protocol);
            // 按英文查
            res = client.queryZH(mean, limit);
        } catch (TException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (transport != null) {
                transport.close();
            }
        }
        return res;
    }
}
