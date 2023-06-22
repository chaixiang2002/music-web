package com.example.yin.exception;

import com.example.yin.common.R;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author lh
 * @version 1.0
 * @description 全局异常处理器
 * @date 2022/9/6 11:29
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(UserLoginExpireException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) //响应的状态码是401
    public R authException2(UserLoginExpireException e) {
        return R.error(40001, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(UserNotLoginExcption.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)//响应的状态码是401
    public R authException3(UserNotLoginExcption e) {
        return R.error(40002, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(UserDownException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)//响应的状态码是401
    public R authException1(UserDownException e) {
        return R.error(40003, e.getMessage());
    }


    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS) //451
    public R doMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        // 由于用户输入的内容可能存在多处错误，所以我们要将所有错误信息都提示给用户
        BindingResult bindingResult = exception.getBindingResult();//验证的错误会放在BingResult里面
        // 获取错误集合
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        // 拼接字符串
        StringBuffer stringBuffer = new StringBuffer();
        fieldErrors.forEach(fieldError -> stringBuffer.append(fieldError.getDefaultMessage()).append(","));
        // 记录日志
        log.error(stringBuffer.toString());

        // 响应给用户
        return R.error(40004, stringBuffer.toString());
    }



    @ResponseBody
    @ExceptionHandler({Exception.class, NoSuchElementException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R exception(Exception e) {
        log.error("【系统异常】{}", e.getMessage(), e);
        return R.fatal(e.getMessage());

    }

}