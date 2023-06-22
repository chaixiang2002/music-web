package com.example.yin.exception;

public class UserLoginExpireException extends RuntimeException{
    private String errMessage;



    public UserLoginExpireException() {
        super();
    }

    public UserLoginExpireException(String errMessage) {
        super(errMessage);
    }

    public String getErrMessage() {
        return errMessage;
    }

    public static void cast(String errMessage) {
        throw new UserLoginExpireException(errMessage);
    }
}
