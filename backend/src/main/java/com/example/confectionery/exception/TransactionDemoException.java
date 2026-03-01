package com.example.confectionery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TransactionDemoException extends RuntimeException {
    public TransactionDemoException(String message) {
        super(message);
    }
}