package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity handle(RuntimeException e) {
        // FIXME: for some reason if I add body to ResponseEntity I get following exception:
        //  com.fasterxml.jackson.core.JsonParseException: Unrecognized token <body content here>: was expecting (JSON String, Number, ...
        //  should be fixed latter.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }

}
