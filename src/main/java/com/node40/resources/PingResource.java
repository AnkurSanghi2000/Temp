package com.node40.resources;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value = "ping")
@Path("/ping")
@Produces(MediaType.TEXT_PLAIN)
public class PingResource {

    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String NO_CACHE = "must-revalidate,no-cache,no-store";

    @GET
    @Timed
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "pong", response = String.class)
    })
    public Response ping() {
        return Response.ok().entity("pongRes").header(CACHE_CONTROL, NO_CACHE).build();
    }
}