package com.node40.inject;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.node40.DataFaucetConfiguration;
import com.node40.client.FTXExchangeClient;
import com.node40.core.DataStoreClient;
import com.node40.core.DataStoreFactory;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataFaucetModule extends AbstractModule {
    private static final Logger log = LoggerFactory.getLogger(DataFaucetModule.class);
    private final Environment environment;
    private final DataFaucetConfiguration config;


    public DataFaucetModule(Environment environment, DataFaucetConfiguration config) {
        this.environment = environment;
        this.config = config;
    }

    @Override
    protected void configure() {
        //bind(String.class).annotatedWith(Names.named("environment")).toInstance(config.getEnvironment());
        bind(HttpTransport.class).to(NetHttpTransport.class);
        bind(JsonFactory.class).to(JacksonFactory.class);
        bind(DataStoreClient.class).annotatedWith(FTXExchange.class).to(FTXExchangeClient.class);
    }

    @Provides
    public HttpRequestFactory provideRequestFactory(HttpTransport httpTransport, JsonFactory jsonFactory) {
        return httpTransport.createRequestFactory(request -> request.setParser(new JsonObjectParser(jsonFactory)));
    }
}
