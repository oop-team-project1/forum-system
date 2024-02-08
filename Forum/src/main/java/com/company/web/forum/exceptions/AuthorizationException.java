package com.company.web.forum.exceptions;

//TODO HttpsStatus.FORBIDDEN (403)
public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message) {
        super(message);
    }
}
