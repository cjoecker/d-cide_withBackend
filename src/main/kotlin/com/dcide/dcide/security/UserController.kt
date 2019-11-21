package com.dcide.dcide.security

import com.dcide.dcide.security.User
import com.dcide.dcide.security.MapValidationErrorService
import com.dcide.dcide.security.SecurityConstants.TOKEN_PREFIX
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import javax.validation.Valid
import com.dcide.dcide.security.UserService
import org.springframework.beans.factory.annotation.Autowired
import com.dcide.dcide.security.UserValidator
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/users")
class UserController {

    @Autowired
    lateinit var mapValidationErrorService: MapValidationErrorService

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userValidator: UserValidator

    @Autowired
    lateinit var tokenProvider: JwtTokenProvider

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var userRepository: UserRepository

    @PostMapping("/login")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest, result: BindingResult): ResponseEntity<*> {
        val errorMap = mapValidationErrorService.MapValidationService(result)
        if (errorMap != null) return errorMap

        val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        loginRequest.username,
                        loginRequest.password
                )
        )

        SecurityContextHolder.getContext().authentication = authentication
        val jwt = TOKEN_PREFIX + tokenProvider.generateToken(authentication)

        return ResponseEntity.ok<Any>(JwtLoginSucessResponse(true, jwt))
    }


    @PostMapping("/signUp")
    fun signUpUser(@Valid @RequestBody user: User, result: BindingResult): ResponseEntity<*> {

        userValidator.validate(user,result)

        val errorMap = mapValidationErrorService.MapValidationService(result)
        if (errorMap != null) return errorMap

        val newUser = userService.saveUser(user)

        return ResponseEntity(newUser, HttpStatus.CREATED)
    }

    @GetMapping("/unregistered")
    fun getUnregisteredUsers(): ResponseEntity<*> {

        val unregisteredUsers = userRepository.findAll().filter {
            !it.registeredUser
        }.count()

        return ResponseEntity(unregisteredUsers, HttpStatus.OK)
    }

}