package com.zhongyi.lotusprize.service;

import org.springframework.transaction.annotation.Transactional;


public interface ITransactionRunner {

    @Transactional(rollbackFor = Exception.class)
    public Object doInTransaction(ITransactionOperation operation);
}
