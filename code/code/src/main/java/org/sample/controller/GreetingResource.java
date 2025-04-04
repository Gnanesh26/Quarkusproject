package org.sample.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.mindrot.jbcrypt.BCrypt;
import org.sample.entity.User;

import java.util.List;

@Path("/hello")
public class GreetingResource {

    @GET
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }


    @GET
   @RolesAllowed("/ADMIN")
    @Path("/admin")
    @Produces(MediaType.TEXT_PLAIN)
    public String adminResource() {
        return "admin";
    }


    @GET
//    @RolesAllowed("user")
    @Path("/me")
    public String me(@Context SecurityContext securityContext) {
        return securityContext.getUserPrincipal().getName();
    }

//    @GET
//    @Path("/users")
//    public List<User> getAllUsers() {
//        return User.listAll(); // Fetch all users from the database
//    }




    // Used to Add users in database


    @POST
    @PermitAll
    @Path("/users")
//    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
        public Response addUser(User user) {
            // Encode the password before persisting
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);
            user.persist(); // Save the User entity into the database
            return Response.status(Response.Status.CREATED).entity(user).build();
        }


    @GET
    @RolesAllowed({"admin", "user"})
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getAllUsers() {
        List<User> users = User.listAll(); // Fetch all User entities from the database
        return Response.ok(users).build(); // Return the list of users in JSON format
    }
    }