package com.dcide.dcide.security.jwt

import com.dcide.dcide.security.responses.InvalidLoginResponse
import com.google.gson.Gson
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtAuthenticationEntryPoint: AuthenticationEntryPoint {

    override fun commence(httpServletRequest: HttpServletRequest?, httpServletResponse: HttpServletResponse?, authenticationException: AuthenticationException?) {
        val loginResponse = InvalidLoginResponse()
        val jsonLoginResponse = Gson().toJson(loginResponse)


        httpServletResponse?.contentType = "application/json"
        httpServletResponse?.status = 401
        httpServletResponse?.writer!!.print(jsonLoginResponse)
    }
}