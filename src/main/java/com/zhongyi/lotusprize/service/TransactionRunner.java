package com.zhongyi.lotusprize.service;

import org.springframework.stereotype.Service;


@Service("transactionRunner")
public class TransactionRunner implements ITransactionRunner {


    public Object doInTransaction(ITransactionOperation operation) {
        return operation.run();
    }


}

