package com.dcide.dcide.control


import com.dcide.dcide.model.DecisionRepository
import com.dcide.dcide.service.DecisionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal


@RestController
@RequestMapping("/api/decisions")
@CrossOrigin
internal class DecisionsController(private val decisionRepository: DecisionRepository) {

    @Autowired
    lateinit var decisionService: DecisionService

    @GetMapping("")
    fun getDecisions(principal: Principal): ResponseEntity<*>  {

        return ResponseEntity<Any>(decisionService.getDecisionsByUser(principal.name), HttpStatus.OK)

    }

}