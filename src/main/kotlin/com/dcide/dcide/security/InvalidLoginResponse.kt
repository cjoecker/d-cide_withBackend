package com.dcide.dcide.security

data class InvalidLoginResponse(
        val username: String =  "Invalid username or password",
        val password: String  =  "Invalid username or password"
)