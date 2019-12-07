package com.dcide.dcide.security

object SecurityConstants {

    const val SIGN_UP_URLS = "/api/users/**"
    const val LOGIN_URLS = "/api/sessions/**"
    const val H2_URL = "h2-console/**"
    const val SECRET = "SecretKeyToGenJWTs"
    const val TOKEN_PREFIX = "Bearer " //Space needs to be at the end!
    const val HEADER_STRING = "Authorization"
    const val EXPIRATION_TIME: Long = 300000000 //30 seconds
}

