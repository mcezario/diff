package org.mcezario.diff.usecases.exceptions;

import org.mcezario.diff.domains.exceptions.BusinessRuleException;
import org.mcezario.diff.domains.exceptions.ExceptionId;

public class CalculateDifferenceException extends BusinessRuleException { //NOSONAR: Ignore number of parents

    private static final String CODE = "002";
    private static final String MESSAGE = "Error to compare the difference between left and right side.";

    public CalculateDifferenceException() {
        super(ExceptionId.create(CODE, MESSAGE));
    }

}