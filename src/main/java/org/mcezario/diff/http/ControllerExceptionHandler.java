package org.mcezario.diff.http;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.mcezario.diff.domains.exceptions.BusinessRuleException;
import org.mcezario.diff.domains.exceptions.ExceptionId;
import org.mcezario.diff.domains.exceptions.IdentifiableException;
import org.mcezario.diff.http.json.ExceptionJson;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ControllerExceptionHandler {

	private static final String MISSING_PARAM = "missingParam";
	private static final String BODY_INCORRECT_VALUE = "body.incorrectValue";

	@ExceptionHandler({ BusinessRuleException.class })
	@ResponseBody
	public ResponseEntity<Set<ExceptionJson>> gatewayException(final IdentifiableException e, final HttpServletResponse response) {

		log(e.id().code(), e.id().message());

		final Set<ExceptionJson> json = Collections.singleton(new ExceptionJson(e));

		MapUtils.emptyIfNull(e.headers()).entrySet().forEach(h -> response.addHeader(h.getKey(), h.getValue()));

		return new ResponseEntity<>(json, new HttpHeaders(), e.httpStatus());
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Throwable.class)
	@ResponseBody
	public Set<ExceptionJson> genericError(final Throwable e) {
		final ExceptionId id = ExceptionId.internalError();

		log(id.code(), id.message());
		log.error("Error: {}", e.getMessage(), e);

		return Collections.singleton(new ExceptionJson(id.code(), id.message()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Set<ExceptionJson> handleHttpMessageNotReadableException(final HttpMessageNotReadableException ex) {

		if (StringUtils.contains(ex.getMessage(), ";")) {

			final Matcher matcher = Pattern.compile("(JSON parse error: Cannot deserialize value of type (.*)) from ((.*))") //
					.matcher(StringUtils.split(ex.getMessage(), ";")[0]);

			if (matcher.matches()) {
				return Collections.singleton(new ExceptionJson(BODY_INCORRECT_VALUE, matcher.group(3)));
			}
		}

		return Collections.singleton(new ExceptionJson(BODY_INCORRECT_VALUE, ex.getLocalizedMessage()));

	}

	@ExceptionHandler(ServletRequestBindingException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Set<ExceptionJson> handleRequiredParameters(final ServletRequestBindingException ex) {

		return Collections.singleton(new ExceptionJson(MISSING_PARAM, ex.getMessage()));

	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseBody
	public SortedSet<ExceptionJson> handlerMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {

		final SortedSet<ExceptionJson> errors = new TreeSet<>();

		errors.add(new ExceptionJson(e.getName(), "Failed to convert " + e.getValue()));

		return errors;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public SortedSet<ExceptionJson> validationException(final MethodArgumentNotValidException e) {

		final SortedSet<ExceptionJson> errors = new TreeSet<>();

		for (final FieldError field : e.getBindingResult().getFieldErrors()) {

			errors.add(new ExceptionJson(field.getField(), field.getDefaultMessage()));
		}

		return errors;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseBody
	public SortedSet<ExceptionJson> violationException(final ConstraintViolationException e) {

		final SortedSet<ExceptionJson> errors = new TreeSet<>();

		e.getConstraintViolations().forEach(
				constraintViolation -> errors.add(new ExceptionJson(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage())));

		return errors;
	}

	private static void log(final String code, final String message) {
		log.error("Error. Code: {}, Message: {}", code, message);
	}

}
