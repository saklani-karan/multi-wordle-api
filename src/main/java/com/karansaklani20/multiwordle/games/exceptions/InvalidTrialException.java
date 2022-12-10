package com.karansaklani20.multiwordle.games.exceptions;

import javax.management.relation.InvalidRoleValueException;

public class InvalidTrialException extends InvalidRoleValueException {
    public InvalidTrialException(String trial) {
        super(String.format("Trial '%s' not supported by round", trial));
    }
}
