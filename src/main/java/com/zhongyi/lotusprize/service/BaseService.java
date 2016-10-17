package com.zhongyi.lotusprize.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ITransactionRunner transactionRunner;


    protected Object transact(ITransactionOperation txOperation) {
        return transactionRunner.doInTransaction(txOperation);
    }

}

