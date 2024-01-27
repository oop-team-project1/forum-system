package com.company.web.forum.exceptions;

public class BlockedUnblockedUserException extends RuntimeException
{
    public BlockedUnblockedUserException(int attribute, String value) {
        super(String.format("User with %s is already %s.", String.valueOf(attribute), value));
    }

}
