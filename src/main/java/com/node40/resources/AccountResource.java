package com.node40.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.node40.api.AccountRequest;
import com.node40.api.AccountResponse;
import com.node40.core.DataStoreClient;
import com.node40.core.DataStoreFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value = "ping")
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String NO_CACHE = "must-revalidate,no-cache,no-store";
    DataStoreFactory dataStoreFactory;

    @Inject
    public AccountResource(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    @GET
    @Timed
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "pong", response = String.class)
    })
    public Response getAccounts(@Valid AccountRequest accountRequest) {
        //DataStoreFactory dataStoreFactory = new DataStoreFactory(accountRequest.getApiKey(), accountRequest.getSecretKey(), "https://ftx.com/");
        DataStoreClient dataStoreClient = dataStoreFactory.getClient(accountRequest.getDataStoreName());
        AccountResponse accountResponse = dataStoreClient.getAccountList(accountRequest.getApiKey(), accountRequest.getSecretKey());
        return Response.ok(accountResponse).build();
    }
}