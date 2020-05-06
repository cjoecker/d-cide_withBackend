package com.dcide.dcide.security.jwt

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import javax.crypto.SecretKey

object SecurityConstants {
//TODO check where to save this safely
    const val SIGN_UP_URLS = "/api/users/**"
    const val LOGIN_URLS = "/api/sessions/**"
    const val H2_URL = "h2-console/**"
    val SECRET_KEY : SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode("30HheLOYSOWo1e4Yaf6GqYJhApKcaFikOgeBdqpmXS3FkYCToRnwF2Varx/Cbxdf69ktwIpzb7/M2AYAO5E6LQ=="))
    const val TOKEN_PREFIX = "Bearer " //Space needs to be at the end!
    const val HEADER_STRING = "Authorization"
    const val EXPIRATION_TIME_IN_DAYS: Long = 3650
}

