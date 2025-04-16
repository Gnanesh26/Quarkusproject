package org.sample.Resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.mindrot.jbcrypt.BCrypt;
import org.sample.DTO.UserDto;
import org.sample.entity.User;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @POST
    @Path("/register")
    public Response register(UserDto userDto) {
        // Validate input: username and password must not be null
        if (userDto.getUserName() == null || userDto.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Username and password required")
                    .build();
        }

        // Check if a user with the same username already exists in the database
        if (User.find("username", userDto.getUserName()).firstResult() != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("User already exists")
                    .build();
        }

        // Hash the plain password using BCrypt
        String hashedPassword = BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt());

        // Create a new User entity and set its fields from the DTO
        User user = new User();
        user.userName = userDto.getUserName();
        user.password = hashedPassword;  // Store hashed password

        // Persist the new user entity to MongoDB
        user.persist();

        // Return HTTP 201 Created response
        return Response.status(Response.Status.CREATED)
                .entity("User registered")
                .build();
    }



    @POST
    @Path("/login")
    public Response login(UserDto userDto) {
        if (userDto.getUserName() == null || userDto.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Username and password required")
                    .build();
        }

        // Query using entity field name (adjust if you use @BsonProperty)
        User user = User.find("userName", userDto.getUserName()).firstResult();

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid username")
                    .build();
        }

        // Verify password with BCrypt
        boolean passwordMatch = BCrypt.checkpw(userDto.getPassword(), user.password);
        if (!passwordMatch) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid password")
                    .build();
        }

        return Response.ok("Login successful").build();
    }

}
