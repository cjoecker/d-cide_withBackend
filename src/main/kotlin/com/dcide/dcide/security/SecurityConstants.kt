package com.dcide.dcide.security

object SecurityConstants {
//TODO check where to save this safely
    const val SIGN_UP_URLS = "/api/users/**"
    const val LOGIN_URLS = "/api/sessions/**"
    const val H2_URL = "h2-console/**"
    const val SECRET = "SecretKeyToGenJWTs"
    const val TOKEN_PREFIX = "Bearer " //Space needs to be at the end!
    const val HEADER_STRING = "Authorization"
    const val EXPIRATION_TIME_IN_DAYS: Long = 3650
}

