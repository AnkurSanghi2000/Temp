package com.node40;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DataFaucetApplication extends Application<DataFaucetConfiguration> {

    public static void main(final String[] args) throws Exception {
        new DataFaucetApplication().run(args);
    }

    @Override
    public String getName() {
        return "datafaucet";
    }

    @Override
    public void initialize(final Bootstrap<DataFaucetConfiguration> bootstrap) {

    }

    @Override
    public void run(final DataFaucetConfiguration configuration,
                    final Environment environment) {

    }

}
