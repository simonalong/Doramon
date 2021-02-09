package com.simon.ocean.exception;

/**
 * @author shizi
 * @since 2021-01-14 22:17:54
 */
public class ValueChangeException extends RuntimeException{

    public ValueChangeException(Throwable e) {
        super(e);
    }

    public ValueChangeException(String message) {
        super(message);
    }

    public ValueChangeException(String message, Throwable e) {
        super(message, e);
    }
}
