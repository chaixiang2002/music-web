package com.example.yin.exception;

public class UserNotLoginExcption extends RuntimeException{
    private String errMessage;



    public UserNotLoginExcption() {
        super();
    }

    public UserNotLoginExcption(String errMessage) {
        super(errMessage);
    }

    public String getErrMessage() {
        return errMessage;
    }

    public static void cast(String errMessage) {
        throw new UserNotLoginExcption(errMessage);
    }
}
