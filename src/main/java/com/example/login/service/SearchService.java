package com.example.login.service;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

public interface SearchService {
    String queryForEn(String word) throws TException;

    String queryForZh(String mean, Integer limit) throws TException;
}
