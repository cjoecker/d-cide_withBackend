package com.dcide.dcide.security

data class InvalidLoginResponse(
        val error: String =  "Invalid username or password"
)