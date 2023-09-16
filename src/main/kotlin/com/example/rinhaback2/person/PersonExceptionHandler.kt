package com.example.rinhaback2.person

import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class PersonExceptionHandler {

    companion object {
        val listErr : MutableList<String> = mutableListOf();
    }

    @ExceptionHandler(Exception::class)
    fun getExceptions(ex: Exception) {
        listErr.add("[ " + ex.message + " ]"  + " : [ " + ex.javaClass + " ] \n ")
    }

}