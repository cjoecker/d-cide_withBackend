package com.dcide.dcide.security.exceptions

import com.dcide.dcide.security.responses.UsernameAlreadyExistsResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity



@RestController
@ControllerAdvice
class CustomResponseEntityExceptionHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler
    fun handleUsernameAlreadyExists(ex: UsernameAlreadyExistsException, request: WebRequest): ResponseEntity<UsernameAlreadyExistsResponse> {
        val exceptionResponse = UsernameAlreadyExistsResponse(ex.message)
        return ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST)
    }



}