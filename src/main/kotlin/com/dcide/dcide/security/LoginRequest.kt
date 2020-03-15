package com.dcide.dcide.security

import javax.validation.constraints.NotBlank

data class LoginRequest(
        var username: String,
        var password: String
)