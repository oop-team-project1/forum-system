package com.company.web.forum.exceptions;

//TODO HttpStatus.Unauthorized (401)
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
