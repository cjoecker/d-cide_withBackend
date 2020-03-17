package com.dcide.dcide.security

data class InvalidLoginResponse(
        val password: String =  "Invalid username or password"
)