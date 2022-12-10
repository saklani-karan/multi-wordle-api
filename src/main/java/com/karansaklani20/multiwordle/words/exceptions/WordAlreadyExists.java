package com.karansaklani20.multiwordle.words.exceptions;

import javax.persistence.EntityExistsException;

public class WordAlreadyExists extends EntityExistsException {
    public WordAlreadyExists(String value, Long id) {
        super(String.format("Word with value=%s already exists with id=%d", value, id));
    }
}
