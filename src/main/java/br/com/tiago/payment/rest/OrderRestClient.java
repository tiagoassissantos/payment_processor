package br.com.tiago.payment.rest;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "order-service")
public interface OrderRestClient {
    @POST
    @Path("/orders/{orderId}/status")
    @Consumes(MediaType.TEXT_PLAIN)
    void updateOrderStatus(@PathParam("orderId") Long orderId, String status);
}

