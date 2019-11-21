package com.dcide.dcide.security

import com.dcide.dcide.security.SecurityConstants.EXPIRATION_TIME
import com.dcide.dcide.security.SecurityConstants.SECRET
import io.jsonwebtoken.*
import org.springframework.stereotype.Component
import org.springframework.security.core.Authentication
import java.util.*
import io.jsonwebtoken.Jwts





@Component
class JwtTokenProvider {


    //Generate the token

    fun generateToken(authentication: Authentication): String {
        val user: User= authentication.principal as User
        val now = Date(System.currentTimeMillis())

        val expiryDate = Date(now.time+ EXPIRATION_TIME)

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
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact()
    }

    //Validate the token
    fun validateToken(token: String): Boolean {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token)
            return true
        } catch (ex: SignatureException) {
            println("Invalid JWT Signature")
        } catch (ex: MalformedJwtException) {
            println("Invalid JWT Token")
        } catch (ex: ExpiredJwtException) {
            println("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            println("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            println("JWT claims string is empty")
        }

        return false
    }

    //Get user Id from token
    fun getUserIdFromJWT(token: String): Long? {
        val claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).body
        val id = claims["id"].toString()

        return id.toLong()
    }

}