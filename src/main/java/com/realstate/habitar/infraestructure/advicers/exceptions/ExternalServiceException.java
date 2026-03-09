package com.realstate.habitar.infraestructure.advicers.exceptions;

public class ExternalServiceException extends RuntimeException{

    public ExternalServiceException(String message) {
        super(message);
    }
}
