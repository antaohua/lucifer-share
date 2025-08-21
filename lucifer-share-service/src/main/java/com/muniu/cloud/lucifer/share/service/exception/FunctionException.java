package com.muniu.cloud.lucifer.share.service.exception;

import java.io.Serial;

public class FunctionException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    private String functionName;

    public FunctionException(String message, Throwable cause, String functionName) {
        super(message, cause);
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public FunctionException(String message, String functionName) {
        super(message);
        this.functionName = functionName;
    }
}
