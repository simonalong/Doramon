package com.simon.ocean.exception;

/**
 * @author shizi
 * @since 2021-01-14 22:19:40
 */
public class ValueCheckException extends RuntimeException{


    public ValueCheckException(Throwable e) {
        super(e);
    }

    public ValueCheckException(String message) {
        super(message);
    }

    public ValueCheckException(String message, Throwable e) {
        super(message, e);
    }
}
