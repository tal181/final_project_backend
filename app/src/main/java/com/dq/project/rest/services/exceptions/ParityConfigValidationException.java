package com.dq.project.rest.services.exceptions;

import lombok.Getter;

@Getter
public class ParityConfigValidationException extends RuntimeException {

    private final String source;

    public ParityConfigValidationException(String message) {
        super(message);
        this.source = null;
    }

    public ParityConfigValidationException(String message, String source) {
        super(message);
        this.source = source;
    }
}
