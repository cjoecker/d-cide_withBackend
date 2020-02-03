package com.dcide.dcide.service

import com.dcide.dcide.model.Decision
import com.dcide.dcide.model.User
import com.dcide.dcide.model.UserRepository
import com.dcide.dcide.security.*
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*


@Service
class UserService {

    @Autowired
    lateinit var decisionService: DecisionService

    @Autowired
    lateinit  var userRepository: UserRepository

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var mapValidationErrorService: MapValidationErrorService

    @Autowired
    lateinit  var bCryptPasswordEncoder: BCryptPasswordEncoder

    @Autowired
    lateinit var tokenProvider: JwtTokenProvider



    fun saveUser(newUser: User): User {


        try {
            newUser.password = bCryptPasswordEncoder.encode(newUser.password)

            //User has to be unique (exception)
            newUser.username = newUser.username

            //Don't give back the password
            newUser.confirmPassword = ""

            return userRepository.save(newUser)

        }catch (e: Exception){
            throw UsernameAlreadyExistsException("Username ${newUser.username} already exists!")

        }

    }

    fun createUnregisteredUser():String?{

        //Get number of unregistered users
        val unregisteredUsersNum = userRepository.findAll().filter {
            !it.registeredUser
        }.count() + 1

        //create new unregistered user
        val password = UUID.randomUUID().toString().substring(0,15)

        val newUser = User(
                0,
                false,
                "$unregisteredUsersNum@UnregisteredUser.com",
                "Unregistered User $unregisteredUsersNum",
                password,
                password,
                Date(),
                Date()
        )

        saveUser(newUser)

        //create new decision
        val newDecision = Decision(
                null,
                "Unregistered Decision",
                Date(),
                Date(),
                null
        )

        val createdDecision = decisionService.saveDecision(newUser.username, newDecision)

        //create example data
        if (createdDecision != null) {
            decisionService.createExampleData(newUser.username, createdDecision)
        }

        //authenticate user
        return getJWTToken(authenticateUser(newUser.username, password))
    }


    fun authenticateUser(username: String, password: String): Authentication {

        return authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        )
    }


    fun getJWTToken(authentication : Authentication) : String?{

        SecurityContextHolder.getContext().authentication = authentication

        return SecurityConstants.TOKEN_PREFIX + tokenProvider.generateToken(authentication)

    }

}