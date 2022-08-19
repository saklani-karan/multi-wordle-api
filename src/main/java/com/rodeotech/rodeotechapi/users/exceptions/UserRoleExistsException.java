package com.rodeotech.rodeotechapi.users.exceptions;

import javax.persistence.EntityExistsException;

public class UserRoleExistsException extends EntityExistsException{
    public UserRoleExistsException(String role, String username){
        super(String.format("Role %s already exists on user %s",role, username));
    }
}
