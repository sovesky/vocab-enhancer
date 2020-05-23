package com.sovesky.vocabenhancer.exceptionhandling

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime
import java.util.*
import javax.validation.ConstraintViolationException


@RestControllerAdvice
class ControllerAdvice() {

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolation(ex: ConstraintViolationException,
                                  request: WebRequest): MutableMap<String, Any> {
        val details: List<String> = ex.constraintViolations
                .map { e -> e.message }

        val body: MutableMap<String, Any> = HashMap()
        body["timestamp"] = LocalDateTime.now().toString()
        body["message"] = details

        return body
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException,
                                  request: WebRequest): MutableMap<String, Any> {
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.getDefaultMessage()
            errors[fieldName] = errorMessage
        }

        val body: MutableMap<String, Any> = HashMap()
        body["timestamp"] = LocalDateTime.now().toString()
        body["errors"] = errors

        return body
    }

}