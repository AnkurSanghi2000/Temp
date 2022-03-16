package com.node40.api;

import com.node40.core.DataStoreList;

public class AccountRequest {

    private DataStoreList dataStoreName;
    private String apiKey;
    private String secretKey;

    public DataStoreList getDataStoreName() {
        return dataStoreName;
    }

    public void setDataStoreName(DataStoreList dataStoreName) {
        this.dataStoreName = dataStoreName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
