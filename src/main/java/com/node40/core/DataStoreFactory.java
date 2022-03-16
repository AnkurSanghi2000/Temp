package com.node40.core;

import com.google.inject.Inject;
import com.node40.inject.FTXExchange;

public class DataStoreFactory {

    private DataStoreClient ftxExchangeClient;

    @Inject
    public DataStoreFactory(@FTXExchange DataStoreClient ftxExchangeClient) {
        this.ftxExchangeClient = ftxExchangeClient;
    }

    public DataStoreClient getClient(DataStoreList storeName) throws IllegalArgumentException {

        switch (storeName) {
            case FTXExchange:
                return ftxExchangeClient;
            default:
                throw new IllegalArgumentException("Exchange Not Supported");
        }
    }
}
