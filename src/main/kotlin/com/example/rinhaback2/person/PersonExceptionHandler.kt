package com.example.rinhaback2.person

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class PersonExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun missingParameterException() : ResponseEntity<Void> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun messageNotReadable() : ResponseEntity<Void> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
    }

}