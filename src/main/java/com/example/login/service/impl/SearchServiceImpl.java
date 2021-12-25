package com.example.login.service.impl;

import com.example.login.rpcservice.QueryHandler;
import com.example.login.service.SearchService;
import com.example.login.utils.TProtocolUtils;
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
        TProtocol protocol = null;
        String res = null;
        try {
            protocol = TProtocolUtils.borrowTProtocol();
            QueryHandler.Client client = new QueryHandler.Client(protocol);
            // 按英文查
            res = client.query(word);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (protocol != null) {
                TProtocolUtils.returnObject(protocol);
            }
        }
        return res;
    }

    @Override
    public String queryForZh(String mean, Integer limit) throws TException {
        TProtocol protocol = null;
        String res = null;
        try {
            protocol = TProtocolUtils.borrowTProtocol();
            QueryHandler.Client client = new QueryHandler.Client(protocol);
            // 按英文查
            res = client.queryZH(mean, limit);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (protocol != null) {
                TProtocolUtils.returnObject(protocol);
            }
        }
        return res;
    }
}
