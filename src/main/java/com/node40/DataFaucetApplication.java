package com.node40;

import com.node40.resources.PingResource;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
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
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor()
                )
        );
    }

    @Override
    public void run(final DataFaucetConfiguration configuration,
                    final Environment environment) {

        final PingResource pingResource = new PingResource();
        environment.jersey().register(pingResource);
//
//        final AccountResource accountResource = new AccountResource();
//        environment.jersey().register(accountResource);


    }

}
