package com.rodeotech.rodeotechapi.users.exceptions;

import javax.persistence.EntityNotFoundException;

public class InvalidRoleException extends EntityNotFoundException {

    public InvalidRoleException(String role) {
        super(String.format("Role with name %s was not found", role));
    }
}
