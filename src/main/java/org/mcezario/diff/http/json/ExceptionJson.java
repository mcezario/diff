package org.mcezario.diff.http.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.mcezario.diff.domains.exceptions.IdentifiableException;

import java.util.Objects;

public final class ExceptionJson implements Comparable<ExceptionJson> {

    private final String code;
    private final String message;

    @JsonCreator
    public ExceptionJson(@JsonProperty("code") final String code,
                         @JsonProperty("message") final String message) {
        this.code = code;
        this.message = message;
    }

    public ExceptionJson(final IdentifiableException e) {
        this(e.id().code(), e.id().message());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE).append("code", code).build();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ExceptionJson)) {
            return false;
        }

        final ExceptionJson other = (ExceptionJson) o;
        return Objects.equals(code, other.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public int compareTo(final ExceptionJson other) {
        Objects.requireNonNull(other, "other");
        return new CompareToBuilder() //
                .append(code, other.code) //
                .build();
    }

    public final String getCode() {
        return code;
    }

    public final String getMessage() {
        return message;
    }

}
