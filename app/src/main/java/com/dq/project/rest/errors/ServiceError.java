package com.dq.project.rest.errors;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Nullable;


@EqualsAndHashCode
public class ServiceError extends ResponseStatusException {

    public ServiceError(HttpStatus status, @Nullable String reason, @Nullable Throwable cause) {
        super(status, reason, cause);
    }

    public ServiceError(HttpStatus status, @Nullable String reason) {
        super(status, reason);
    }

    /**
     * Overriding the not readable default message of ResponseStatusException
     * @return a string with the error reason followed by the inner exception
     */
    @Override
    public String getMessage() {
        String msg = getReason();
        Throwable cause = getCause();

        if (cause == null) {
            return msg;
        } else {
            return String.format("%s; exception is %s", msg, cause);
        }
    }
}
