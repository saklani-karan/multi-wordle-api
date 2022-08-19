package com.rodeotech.rodeotechapi.institutions.exceptions;

import javax.persistence.EntityExistsException;

public class InstitutionExistsException extends EntityExistsException {
    public InstitutionExistsException(String name) {
        super(String.format("Institution with name %s already exists", name));
    }

    public InstitutionExistsException(Long id) {
        super(String.format("Institution with id %d already exists", id));
    }

}
