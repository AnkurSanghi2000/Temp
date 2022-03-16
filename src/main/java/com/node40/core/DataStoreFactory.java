package com.node40.core;

import com.node40.client.FTXExchangeClient;

public class DataStoreFactory {

    private String apiKey;
    private String secretKey;
    private String baseURI;

    public DataStoreFactory(String apiKey, String secretKey, String baseURI) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.baseURI = baseURI;
    }

    public DataStoreClient nameLookup(DataStoreList storeName) throws IllegalArgumentException {

        switch (storeName) {
            case FTXExchange:
                return new FTXExchangeClient(apiKey, secretKey, baseURI);
            default:
                throw new IllegalArgumentException("Exchange Not Supported");
        }
    }
}
