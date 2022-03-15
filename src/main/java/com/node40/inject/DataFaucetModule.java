package com.node40.inject;

import com.google.inject.AbstractModule;
import com.node40.DataFaucetConfiguration;
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
    }
}
