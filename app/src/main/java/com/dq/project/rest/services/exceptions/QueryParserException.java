package com.dq.project.rest.services.exceptions;

public class QueryParserException extends RuntimeException {

    public QueryParserException(String sql, Throwable cause) {
        super(String.format("Failed to parse query: '%s', cause: %s", sql, cause.getMessage()));
    }

}
