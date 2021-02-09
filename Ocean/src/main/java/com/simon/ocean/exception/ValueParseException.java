package com.simon.ocean.exception;

/**
 * @author shizi
 * @since 2021-01-14 11:30:07
 */
public class ValueParseException extends ConfigServerException{

    public ValueParseException(Throwable e) {
        super(e);
    }

    public ValueParseException(String errMsg) {
        super("值解析错误" + errMsg);
    }

    public ValueParseException(String errMsg, Throwable e) {
        super("值解析错误" + errMsg, e);
    }
}
