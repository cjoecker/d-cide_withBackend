package com.dcide.dcide.control

import com.dcide.dcide.model.User
import com.dcide.dcide.model.UserRepository
import com.dcide.dcide.security.*
import com.dcide.dcide.security.SecurityConstants.TOKEN_PREFIX
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import javax.validation.Valid
import com.dcide.dcide.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/sessions")
class SessionsController {

    @Autowired
    lateinit var mapValidationErrorService: MapValidationErrorService

    @Autowired
    lateinit var userService: UserService


    //opid-post-sessions
    @PostMapping("/")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest, result: BindingResult): ResponseEntity<*> {
        val errorMap = mapValidationErrorService.MapValidationService(result)
        if (errorMap != null) return errorMap

        val jwt = userService.getJWTToken(userService.authenticateUser(loginRequest.username, loginRequest.password))

        return if (jwt != null) {
            ResponseEntity<Any>(JwtLoginSucessResponse(true, jwt), HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }
    }


    //opid-get-sessions-unregistered
    @PostMapping("/unregistered")
    fun createUnregisteredUsers(): ResponseEntity<*> {

        val jwt = userService.createUnregisteredUser()

        return if (jwt != null) {
            ResponseEntity<Any>(JwtLoginSucessResponse(true, jwt), HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }
    }

}