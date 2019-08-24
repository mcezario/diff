package org.mcezario.diff.domains.exceptions;

import org.springframework.http.HttpStatus;

public abstract class BusinessRuleException extends IdentifiableException {

	private static final long serialVersionUID = -2914039952081189989L;

	protected BusinessRuleException(final ExceptionId id) {
		super(id.withHttpStatus(HttpStatus.UNPROCESSABLE_ENTITY));
	}

}
