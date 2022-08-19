package com.rodeotech.rodeotechapi.users.exceptions;

import java.rmi.AccessException;

public class UserAccessException extends AccessException {
    public UserAccessException(String username) {
        super(String.format("user %s does not have access to the resource", username));
    }
}
