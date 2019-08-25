package org.mcezario.diff.domains.exceptions;

import org.springframework.http.HttpStatus;

public abstract class IdentifiableException extends RuntimeException {

	private static final long serialVersionUID = 2319108117813027799L;

	private final ExceptionId id;

	protected IdentifiableException(final ExceptionId id) {
		super(id.toString());
		this.id = id;
	}

	public ExceptionId id() {
      return id;
    }
    
	public HttpStatus httpStatus() {
      return HttpStatus.valueOf(id.httpStatusCode());
    }

}
