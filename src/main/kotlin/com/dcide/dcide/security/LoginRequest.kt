package com.dcide.dcide.security

import javax.validation.constraints.NotBlank

data class LoginRequest(

        @get: NotBlank(message = "Username name is required")
        var username: String,

        @get: NotBlank(message = "Password name is required")
        var password: String

)