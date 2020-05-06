package com.dcide.dcide.security.responses

data class InvalidLoginResponse(
        val password: String =  "Invalid username or password"
)