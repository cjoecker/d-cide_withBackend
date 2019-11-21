package com.dcide.dcide.security

data class JwtLoginSucessResponse(
        var success: Boolean,
        var token: String
){
    fun JWTLoginSucessReponse(success: Boolean, token: String){
        this.success = success
        this.token = token
    }

    fun isSuccess(): Boolean {
        return success
    }

    override fun toString(): String {
        return "JWTLoginSuccessResponse{success=${this.success}, token='${this.token}\\'}"
    }
}