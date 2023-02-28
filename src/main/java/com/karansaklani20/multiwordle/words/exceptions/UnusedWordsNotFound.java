package com.karansaklani20.multiwordle.words.exceptions;

import javax.persistence.EntityNotFoundException;

public class UnusedWordsNotFound extends EntityNotFoundException {
    public UnusedWordsNotFound() {
        super("No unused words found");
    }
}
