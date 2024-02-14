package com.company.web.forum.helpers;

import com.company.web.forum.exceptions.AuthenticationException;
import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.models.User;
import com.company.web.forum.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;


@Component
public class AuthenticationHelper {
    private static final String INVALID_AUTHENTICATION_ERROR = "Invalid authentication.";

    private UserService userService;

    @Autowired
    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
    }

    public AuthenticationHelper() {
    }

    /**
     * Method to decode a Base64 string of username and password
     * separated by ":" and return the authenticated user.
     * <p>
     * From Postman:
     * In the request Headers, the Authorization header
     * passes the API a Base64 encoded string
     * representing your username and password values,
     * appended to the text Basic as follows:
     * Basic <Base64 encoded username and password>
     * <p></p>
     *
     * @param encodedString Base64 encoded string with
     *                      user credentials separated by ":"
     * @return user with the given credentials
     */
    public User tryGetUser(String encodedString) {
        encodedString = encodedString.replace("Basic ", "");
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String decodedString = new String(decodedBytes);
        if (!decodedString.contains(":")) throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
        String username = decodedString.split(":")[0];
        String password = decodedString.split(":")[1];

        try {
            User user = userService.getByUsername(username);
            if (!user.getPassword().equals(password)) {
                throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
            }
            return user;
        } catch (EntityNotFoundException e) {
            throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
        }
    }

    public User tryGetUser(HttpSession session)
    {
        String currentUser = (String) session.getAttribute("currentUser");
        if (currentUser == null)
        {
            throw new AuthenticationException("No user logged in.");
        }

        return userService.getByUsername(currentUser);
    }

    public User verifyAuthentication(String email, String password) {
        try {
            User user = userService.getByEmail(email);
            if(!user.getPassword().equals(password)) {
                throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
            }
            return user;
        } catch (EntityNotFoundException e) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }
    }
}
