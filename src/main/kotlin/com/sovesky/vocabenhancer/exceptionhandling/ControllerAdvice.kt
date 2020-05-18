package com.sovesky.vocabenhancer.exceptionhandling

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime
import java.util.stream.Collectors
import javax.validation.ConstraintViolationException


@RestControllerAdvice
class ControllerAdvice: ResponseEntityExceptionHandler() {

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolation(ex: ConstraintViolationException,
                                  request: WebRequest): ResponseEntity<Any> {
        val details: List<String> = ex.constraintViolations
                .map { e -> e.message }

        val body: MutableMap<String, Any> = HashMap()
        body["timestamp"] = LocalDateTime.now()
        body["message"] = details

        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }
}