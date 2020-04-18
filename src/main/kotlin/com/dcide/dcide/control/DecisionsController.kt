package com.dcide.dcide.control


import com.dcide.dcide.model.*


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


import org.springframework.web.bind.annotation.GetMapping
import java.security.Principal
import com.dcide.dcide.service.DecisionService
import org.springframework.beans.factory.annotation.Autowired


@RestController
@RequestMapping("/api/decisions")
@CrossOrigin
internal class DecisionsController(private val decisionRepository: DecisionRepository) {

    @Autowired
    lateinit var decisionService: DecisionService

    @GetMapping("")
    fun getDecisions(principal: Principal): ResponseEntity<*>  {

        val decisions  = decisionService.getDecisionsByUser(principal.name)

        return ResponseEntity<Any>(decisions, HttpStatus.OK)

    }

}