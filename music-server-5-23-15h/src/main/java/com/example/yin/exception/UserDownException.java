package com.example.yin.exception;

public class UserDownException extends RuntimeException{
    private String errMessage;


    public UserDownException() {
        super();
    }

    public UserDownException(String errMessage) {
        super(errMessage);
    }

    public String getErrMessage() {
        return errMessage;
    }

    public static void cast(String errMessage) {
        throw new UserDownException(errMessage);
    }
}
