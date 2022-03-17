package com.node40.client;


import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.inject.Inject;
import com.node40.api.AccountResponse;
import com.node40.core.DataStoreClient;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class FTXExchangeClient implements DataStoreClient {
    @Override
    public AccountResponse getAccountList(String apiKey, String secretKey) {
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
    private static final String SHA256 = "SHA-256";
    private static final String HMAC_SHA512 = "HmacSHA512";
    @Inject
    private static HttpRequestFactory httpRequestFactory;

    @Inject
    public FTXExchangeClient(String baseURL, HttpRequestFactory httpRequestFactory) {
        //this.baseURL = baseURL;
        this.baseURL = "https://ftx.com/";
        this.httpRequestFactory = httpRequestFactory;
    }

    public static String generateSignature(String secretKey, String uriPath, String body, long timeMillis) throws IllegalArgumentException {
        // API-Sign = Message signature HMAC-SHA512 using API secret of following 4 fields as hex string
        // Request Time stamp
        // uppercase HTTP Method
        // request path w/o host name e.g. /account
        // POST data

        // API-Sign = Message signature using HMAC-SHA512 of (URI path + SHA256(nonce + POST data)) and base64 decoded secret API key
        // create SHA-256 hash of the nonce and the POST data
        byte[] sha256 = sha256(timeMillis + body);

        // set the API method and retrieve the path
        byte[] path = uriPath.getBytes();

        // decode the API secret, it's the HMAC key
        byte[] hmacKey = base64Decode(secretKey);

        // create the HMAC message from the path and the previous hash
        byte[] hmacMessage = concatArrays(path, sha256);

        // create the HMAC-SHA512 digest, encode it and set it as the request signature
        return base64Encode(hmacSha512(hmacKey, hmacMessage));
    }

    private static byte[] base64Decode(String input) {
        return Base64.decodeBase64(input);
    }

    private static String base64Encode(byte[] data) {
        return Base64.encodeBase64String(data);
    }

    private static byte[] HexDecode(String input) {
        return HexDecode(input);
    }
    private static String HexEncode(byte[] data) {
        return HexEncode(data);
    }

    private static byte[] concatArrays(byte[] a, byte[] b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        byte[] concat = new byte[a.length + b.length];
        for (int i = 0; i < concat.length; i++) {
            concat[i] = i < a.length ? a[i] : b[i - a.length];
        }

        return concat;
    }

    private static byte[] hmacSha512(byte[] key, byte[] message) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA512);
            mac.init(new SecretKeySpec(key, HMAC_SHA512));
            return mac.doFinal(message);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static byte[] sha256(String message) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(SHA256);
            return md.digest(stringToBytes(message));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static byte[] stringToBytes(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        return input.getBytes(StandardCharsets.UTF_8);
    }

    public static String encode(String apiSecretKey, String data) throws IllegalArgumentException {
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec localSecretKey = new SecretKeySpec(apiSecretKey.getBytes("UTF-16"), "HmacSHA256");
            sha256HMAC.init(localSecretKey);
            return Hex.encodeHexString(sha256HMAC.doFinal(data.getBytes("UTF-16")));
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
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
            String sigPayLoad = String.valueOf(requestTimeStamp).concat(method.toUpperCase()).concat(endpoint);
            //String signature = encode(secretKey, sigPayLoad);
            //byte[] s = stringToBytes(signature.toLowerCase());
            byte[] s = hmacSha512(HexDecode(this.secretKey),HexDecode(sigPayLoad));
            //HttpResponse response = httpRequestFactory.buildPostRequest(url, new UrlEncodedContent(params))
            HttpResponse response = httpRequestFactory.buildGetRequest(url)
                    .setHeaders(
                            new HttpHeaders()
                                    .set("FTX-KEY", this.apikey)
                                    .set("FTX-SIGN", s)
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
        //getmarket();
        return accountList;
    }

    private void getmarket(){
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
