package com.example.demo.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(AccessDeniedException.class)
    public final void exceptionHandler(AccessDeniedException ex) {
        System.out.println("Error message:" + ex.getMessage());
        System.out.println("Error cause:" + ex.getCause());
    }
}
