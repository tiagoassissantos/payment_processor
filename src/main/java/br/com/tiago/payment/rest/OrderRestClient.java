package br.com.tiago.payment.rest;

import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "order-service")
public interface OrderRestClient {
    @PUT
    @Path("/orders/{orderId}/status/{status}")
    @Consumes(MediaType.APPLICATION_JSON)
    void updateOrderStatus(@PathParam("orderId") Long orderId, @PathParam("status") String status);
}

