package com.dcide.dcide.service

import com.dcide.dcide.model.Decision
import com.dcide.dcide.model.User
import com.dcide.dcide.model.UserRepository
import com.dcide.dcide.security.JwtTokenProvider

import com.dcide.dcide.security.SecurityConstants
import com.dcide.dcide.security.UsernameAlreadyExistsException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*


@Service
class UserService {

    @Autowired
    lateinit var decisionService: DecisionService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder

    @Autowired
    lateinit var tokenProvider: JwtTokenProvider


    fun saveUser(newUser: User): User {

        try {
            newUser.password = bCryptPasswordEncoder.encode(newUser.password)

            //User has to be unique (throw exception)
            newUser.username = newUser.username

            //Don't give back the password
            newUser.confirmPassword = ""

            return userRepository.save(newUser)

        } catch (e: Exception) {
            throw UsernameAlreadyExistsException("Username ${newUser.username} already exists!")

        }

    }

    fun createUnregisteredUser(): String? {

        val unregisteredUsersNum = userRepository.findAll().filter {
            !it.registeredUser
        }.count() + 1

        val password = UUID.randomUUID().toString().substring(0, 15)

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

        createDecisionForUnregisteredUser(newUser)

        return getJWTToken(authenticateUser(newUser.username, password))
    }

    fun createDecisionForUnregisteredUser(newUser: User) {
        val newDecision = Decision(
                null,
                "New Decision",
                Date(),
                Date(),
                null
        )

        val createdDecision = decisionService.saveDecision(newUser.username, newDecision)

        if (createdDecision != null) {
            decisionService.createExampleData(newUser.username, createdDecision)
        }
    }

    fun authenticateUser(username: String, password: String): Authentication {

        return authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        )
    }

    fun getJWTToken(authentication: Authentication): String? {

        SecurityContextHolder.getContext().authentication = authentication

        return SecurityConstants.TOKEN_PREFIX + tokenProvider.generateToken(authentication)
    }

}