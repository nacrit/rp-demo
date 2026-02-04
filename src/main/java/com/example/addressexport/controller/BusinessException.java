package com.example.addressexport.controller;

public class BusinessException extends RuntimeException{

    private static final long serialVersionUID = -4495539371656587567L;

    public BusinessException(String message) {
        super(message);
    }
}
