package com.karansaklani20.multiwordle.users.exceptions;

import javax.persistence.EntityNotFoundException;

public class AuthContextNotFound extends EntityNotFoundException {
    public AuthContextNotFound() {
        super("Authentication context has missing email");
    }
}
