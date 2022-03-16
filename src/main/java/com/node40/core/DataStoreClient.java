package com.node40.core;

import com.node40.api.AccountResponse;


public interface DataStoreClient {
    AccountResponse getAccountList(String apiKey, String secretKey);
}
