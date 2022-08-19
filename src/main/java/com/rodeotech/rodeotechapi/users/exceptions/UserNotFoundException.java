package com.rodeotech.rodeotechapi.users.exceptions;

import javax.persistence.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(Long id) {
        super(String.format("User not found with id %d", id));
    }

    public UserNotFoundException(String username) {
        super(String.format("User not found with username %s", username));
    }
}
