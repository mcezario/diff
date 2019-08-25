package org.mcezario.diff.usecases.exceptions;

import org.mcezario.diff.domains.exceptions.BusinessRuleException;
import org.mcezario.diff.domains.exceptions.ExceptionId;
import org.springframework.http.HttpStatus;

public class DiffNotFoundException extends BusinessRuleException { //NOSONAR: Ignore number of parents

    private static final String CODE = "001";
    private static final String MESSAGE = "Invalid Id.";

    public DiffNotFoundException() {
        super(ExceptionId.create(CODE, MESSAGE));
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.NOT_FOUND;
    }

}