package com.dcide.dcide.security

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder





@Service
class UserService {

    @Autowired
    lateinit  var userRepository: UserRepository


    @Autowired
    lateinit  var bCryptPasswordEncoder: BCryptPasswordEncoder

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
}