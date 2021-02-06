package Controller;

import Service.Service;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/movies")
public class ScrappingController {

    @Inject
    Service service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/testing/{name}")
    public String greeting(@PathParam("name") String name) {
        return service.greeting(name);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/setInfo/{name}")
    public List setInfo(@PathParam("name") String name) {
        return service.setInfo(name);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getInfo/{name}/{rating}")
    public List getInfo(@PathParam("name") String name, @PathParam("rating") String rating) {
        return service.getInfo(name,rating);
    }





}