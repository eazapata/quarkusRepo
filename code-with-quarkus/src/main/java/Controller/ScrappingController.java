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

    @PUT
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getTitle/{name}")
    public List getTitle(@PathParam("name") String name) {
        return service.getTitle(name);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getRating/{rating}")
    public List getRating(@PathParam("rating") double rating) {
        return service.getRating(rating);
    }


}