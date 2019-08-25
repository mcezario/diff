package org.mcezario.diff.usecases.exceptions;

import org.mcezario.diff.domains.exceptions.BusinessRuleException;
import org.mcezario.diff.domains.exceptions.ExceptionId;

public class RequiredSidesException extends BusinessRuleException { //NOSONAR: Ignore number of parents

    private static final String CODE = "003";
    private static final String MESSAGE = "The left and right sides must be filled in to compare the difference.";

    public RequiredSidesException() {
        super(ExceptionId.create(CODE, MESSAGE));
    }

}