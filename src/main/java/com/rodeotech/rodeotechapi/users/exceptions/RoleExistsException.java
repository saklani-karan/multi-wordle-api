package com.rodeotech.rodeotechapi.users.exceptions;

import javax.persistence.EntityExistsException;

public class RoleExistsException extends EntityExistsException {
    public RoleExistsException(Long roleId) {
        super(String.format("Role with id %d already exists", roleId));
    }

    public RoleExistsException(String role) {
        super(String.format("Role %s already exists", role));
    }
}
