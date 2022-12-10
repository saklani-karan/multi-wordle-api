package com.karansaklani20.multiwordle.users.exceptions;

import javax.persistence.EntityNotFoundException;

public class UserNotFound extends EntityNotFoundException {
    public UserNotFound(String email) {
        super(String.format("User not found with email '%s'", email));
    }

    public UserNotFound(Long id) {
        super(String.format("User not found with id '%d'", id));
    }
}
