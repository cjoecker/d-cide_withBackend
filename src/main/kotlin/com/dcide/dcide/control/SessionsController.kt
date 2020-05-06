package com.dcide.dcide.control

import com.dcide.dcide.security.responses.JwtLoginSucessResponse
import com.dcide.dcide.service.UserService
import com.dcide.dcide.service.WeightedCriteriaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/sessions")
class SessionsController {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var weightedCriteriaService: WeightedCriteriaService



    @PostMapping("/unregistered")
    fun createUnregisteredUsers(): ResponseEntity<*> {

        val jwt = userService.createUnregisteredUser()

        return if (jwt != null) {
            ResponseEntity<Any>(JwtLoginSucessResponse(true, jwt), HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }
    }

}