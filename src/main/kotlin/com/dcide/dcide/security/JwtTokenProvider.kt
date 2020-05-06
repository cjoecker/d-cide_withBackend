package com.dcide.dcide.security

import com.dcide.dcide.model.User
import com.dcide.dcide.security.SecurityConstants.EXPIRATION_TIME_IN_DAYS
import com.dcide.dcide.security.SecurityConstants.SECRET_KEY
import io.jsonwebtoken.*
import org.springframework.stereotype.Component
import org.springframework.security.core.Authentication
import java.util.*
import io.jsonwebtoken.Jwts


@Component
class JwtTokenProvider {


    fun generateToken(authentication: Authentication): String {
        val user: User = authentication.principal as User
        val now = Date(System.currentTimeMillis())

        val expiryDate = Date(now.time + EXPIRATION_TIME_IN_DAYS * 86400000 )

        val userId = user.id

        val claims = HashMap<String, Any>()
        claims["id"] = userId
        claims["username"] = user.username
        claims["fullName"] = user.fullName
        claims["registeredUser"] = user.registeredUser

        return Jwts.builder()
                .setSubject(userId.toString())
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact()
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token)
            return true
        } catch (ex: JwtException) {
            println(ex.message)
        }

        return false
    }

    fun getUserIdFromJWT(token: String): Long? {
        val claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).body
        val id = claims["id"] as Double

        return id.toLong()
    }

}