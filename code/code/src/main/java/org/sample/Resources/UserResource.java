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
    @Path("/sign-up")
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
        user.firstName = userDto.getFirstName();
        user.lastName = userDto.getLastName();
        user.email = userDto.getEmail();

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
        // Validate input: username and password must not be null
        if (userDto.getUserName() == null || userDto.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Username and password required")
                    .build();
        }

        String loginInput = userDto.getUserName();

        // Try to find user by username
        User user = User.find("userName", loginInput).firstResult();

        // If not found by username, try email
        if (user == null) {
            user = User.find("email", loginInput).firstResult();
        }

        // If still not found, unauthorized
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid username/email or password")
                    .build();
        }

        // Verify password using BCrypt
        boolean passwordMatch = BCrypt.checkpw(userDto.getPassword(), user.password);
        if (!passwordMatch) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid username/email or password")
                    .build();
        }

        // Successful login
        return Response.ok("Login successful").build();
    }
}
