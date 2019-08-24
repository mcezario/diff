package org.mcezario.diff.domains.exceptions;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public abstract class IdentifiableException extends RuntimeException {

	private static final long serialVersionUID = 2319108117813027799L;

	private final ExceptionId id;
	private final Map<String, String> headers;

	protected IdentifiableException(final ExceptionId id) {
		super(id.toString());
		this.id = id;
		this.headers = new HashMap<>();
	}

	protected IdentifiableException(final ExceptionId id, final Throwable cause) {
		super(id.toString(), cause);
		this.id = id;
		this.headers = new HashMap<>();
	}

	public ExceptionId id() {
      return id;
    }
    
	public HttpStatus httpStatus() {
      return HttpStatus.valueOf(id.httpStatusCode());
    }
	
	public int httpStatusCode() {
		return id.httpStatusCode();
	}

	public Map<String, String> headers() {
		return headers;
	}

	public IdentifiableException withHeaders(final Map<String, String> headers) {
		this.headers.putAll(headers);

		return this;
	}

}
