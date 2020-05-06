package com.dcide.dcide.security.jwt

import com.dcide.dcide.security.userValidation.CustomUserDetailsService
import com.dcide.dcide.security.jwt.SecurityConstants.HEADER_STRING
import com.dcide.dcide.security.jwt.SecurityConstants.TOKEN_PREFIX
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import java.util.*


class JwtAuthenticationFilter: OncePerRequestFilter() {

    @Autowired
    lateinit var tokenProvider: JwtTokenProvider

    @Autowired
    lateinit var customUserDetailsService: CustomUserDetailsService

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse,
                                  filterChain: FilterChain) {

        try {

            val jwt = getJWTFromRequest(httpServletRequest)

            if (!jwt.isNullOrEmpty() && tokenProvider.validateToken(jwt)) {
                val userId = tokenProvider.getUserIdFromJWT(token = jwt)
                val userDetails = customUserDetailsService.loadUserById(userId)

                val authentication = UsernamePasswordAuthenticationToken(
                        userDetails, null, Collections.emptyList())

                authentication.details = WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                SecurityContextHolder.getContext().authentication = authentication

            }

        } catch (ex: Exception) {
            logger.error("Could not set user authentication in security context", ex)
        }


        filterChain.doFilter(httpServletRequest, httpServletResponse)

    }


    private fun getJWTFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(HEADER_STRING)

        return if (!bearerToken.isNullOrEmpty() && bearerToken.startsWith(TOKEN_PREFIX)) {
            bearerToken.substring(TOKEN_PREFIX.length, bearerToken.length)
        } else null

    }
}