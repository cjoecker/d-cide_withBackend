package com.dcide.dcide.security

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.security.Key

object SecurityConstants {
//TODO check where to save this safely
    const val SIGN_UP_URLS = "/api/users/**"
    const val LOGIN_URLS = "/api/sessions/**"
    const val H2_URL = "h2-console/**"
    val SECRET_KEY: Key = Keys.secretKeyFor(SignatureAlgorithm.HS512)
    const val TOKEN_PREFIX = "Bearer " //Space needs to be at the end!
    const val HEADER_STRING = "Authorization"
    const val EXPIRATION_TIME_IN_DAYS: Long = 3650
}

