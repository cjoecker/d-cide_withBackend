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
@RequestMapping("/api/users")
class UsersController {

    @Autowired
    lateinit var mapValidationErrorService: MapValidationErrorService

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userValidator: UserValidator


    //opid-post-users
    @PostMapping("/")
    fun createUser(@Valid @RequestBody user: User, result: BindingResult): ResponseEntity<*> {

        userValidator.validate(user,result)

        val errorMap = mapValidationErrorService.MapValidationService(result)
        if (errorMap != null) return errorMap

        val newUser = userService.saveUser(user)

        return ResponseEntity(newUser, HttpStatus.CREATED)
    }



}