package com.dcide.dcide.security.userValidation

import com.dcide.dcide.model.User
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class UserValidator : Validator {



    override fun supports(aClass: Class<*>): Boolean {
        return User::class.java == aClass
    }

    override fun validate(`object`: Any, errors: Errors) {

        val user = `object` as User

        if (user.password.length < 6) errors.rejectValue("password", "Length", "Password must have at least 6 characters")

        if (user.password != user.confirmPassword) {
            errors.rejectValue("confirmPassword", "Match", "Passwords must match")
        }
    }
}