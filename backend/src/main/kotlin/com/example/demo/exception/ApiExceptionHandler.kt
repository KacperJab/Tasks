package com.example.demo.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ApiExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [(ApiRequestException::class)])
    fun handleApiRequestException(e: ApiRequestException): ResponseEntity<Any> {
        val apiException = ApiException(e.message, e.status)
        return ResponseEntity(apiException, e.status)
    }
}
