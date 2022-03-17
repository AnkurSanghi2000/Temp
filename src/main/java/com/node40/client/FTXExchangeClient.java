package com.node40.client;


import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.inject.Inject;
import com.node40.api.AccountResponse;
import com.node40.core.DataStoreClient;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class FTXExchangeClient implements DataStoreClient {
    @Override
    public AccountResponse getAccountList(String apikey, String secretKey) {
        this.apikey = apikey;
        this.secretKey = secretKey;

        this.getAccountListFromExchange();
        //Map/Translate FTX Account response to Node40 account response
        AccountResponse accountResponse = new AccountResponse("test");
        return accountResponse;
    }

    /* This is the account response definition from FTX */
    private class Account {

        String accountName;
        String accountID;

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public String getAccountID() {
            return accountID;
        }

        public void setAccountID(String accountID) {
            this.accountID = accountID;
        }
    }

    /* This is the Transaction response definition from FTX */
    private class Transaction {

        String trasnactionId;
        double amount;

        public String getTrasnactionId() {
            return trasnactionId;
        }

        public void setTrasnactionId(String trasnactionId) {

            this.trasnactionId = trasnactionId;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {

            this.amount = amount;
        }
    }

    ArrayList<Account> accountList = new ArrayList<Account>();
    ArrayList<Transaction> transactionList = new ArrayList<Transaction>();

    private String apikey;
    private String secretKey;
    private String baseURL;
    private static final Logger log = LoggerFactory.getLogger(FTXExchangeClient.class);
    private static final String HMAC_SHA256 = "HmacSHA256";
    @Inject
    private static HttpRequestFactory httpRequestFactory;

    @Inject
    public FTXExchangeClient(String baseURL, HttpRequestFactory httpRequestFactory) {
        //this.baseURL = baseURL;
        this.baseURL = "https://ftx.com/";
        this.httpRequestFactory = httpRequestFactory;
    }

    public static String generateSignatureFTX(String secretKey, String uriPath,long timeMillis) throws IllegalArgumentException {
        // API-Sign = Message signature HMAC-SHA512 using API secret of following 4 fields as hex string
        // Request Time stamp
        // uppercase HTTP Method
        // request path w/o host name e.g. /account
        // POST data

        // Concatenate Time, method and uripath
        String signaturePayload = String.valueOf(timeMillis).concat(HttpMethods.GET).concat(uriPath);
        byte[] hmacMessage = signaturePayload.getBytes(StandardCharsets.UTF_8);

        // decode the API secret, it's the HMAC key
        byte[] hmacKey = secretKey.getBytes(StandardCharsets.UTF_8);

        // create the HMAC-SHA256 digest, encode it and set it as the request signature
        return HexEncode(hmacSha256(hmacKey, hmacMessage));
    }
    private static String HexEncode(byte[] data) {
        return (new BigInteger(1, data).toString(16));
    }

    private static byte[] hmacSha256(byte[] key, byte[] message) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(key, HMAC_SHA256));
            return mac.doFinal(message);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void generateSign() {

        // API-Sign = Message signature HMAC-SHA512 using API secret of following 4 fields as hex string
        // Request Time stamp
        // uppercase HTTP Method
        // request path w/o host name e.g. /account
        // POST data

        try {

            String resource_url = String.format("%s%s", baseURL, "api/account");
            GenericUrl url = new GenericUrl(resource_url);
            long requestTimeStamp = DateTime.now().getMillis();
            String method = HttpMethods.GET;
            String endpoint = "/api/account";
            String sigPayLoad = generateSignatureFTX(secretKey,endpoint,requestTimeStamp);

            HttpResponse response = httpRequestFactory.buildGetRequest(url)
                    .setHeaders(
                            new HttpHeaders()
                                    .set("FTX-KEY", this.apikey)
                                    .set("FTX-SIGN", sigPayLoad)
                                    .set("FTX-TS", String.valueOf(requestTimeStamp))
                    ).setConnectTimeout(30000)
                    .setReadTimeout(120 * 1000)
                    .execute();
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    private ArrayList<Account> getAccountListFromExchange() {
        generateSign();
        //getmarketNoAuthentication();
        return accountList;
    }

    private void getmarketNoAuthentication(){
        try {
            String resource_url = String.format("%s%s", baseURL, "api/markets");
            GenericUrl url = new GenericUrl(resource_url);
            HttpResponse response = httpRequestFactory.buildGetRequest(url)
                    .setConnectTimeout(30000)
                    .setReadTimeout(120 * 1000)
                    .execute();
            log.info(response.toString());
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    private ArrayList<Transaction> getTransactionListFromExchange() {
        return transactionList;
    }

}
