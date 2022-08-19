package com.rodeotech.rodeotechapi.users.exceptions;

import javax.persistence.EntityExistsException;

public class UserExistsException extends EntityExistsException {
    public UserExistsException(Long userId) {
        super(String.format("User with id %d already exists", userId));
    }
}
