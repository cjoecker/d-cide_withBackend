package com.dcide.dcide.control


import com.dcide.dcide.model.*


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.validation.Valid
import java.net.URISyntaxException


import org.springframework.web.bind.annotation.GetMapping
import java.security.Principal
import com.dcide.dcide.model.UserRepository
import com.dcide.dcide.service.DecisionsService
import org.springframework.beans.factory.annotation.Autowired


@RestController
@RequestMapping("/api/decisions")
@CrossOrigin
internal class DecisionsController(private val decisionsRepository: DecisionsRepository) {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var decisionOptionRepository: DecisionOptionRepository

    @Autowired
    lateinit var selectionCriteriaRepository: SelectionCriteriaRepository

    @Autowired
    lateinit var decisionsService: DecisionsService


    //opid-get-decisions
    @GetMapping("")
    fun getDecisions(principal: Principal): ResponseEntity<*>  {

        val decisions  = decisionsService.getDecisions(principal.name)

        return ResponseEntity<Any>(decisions, HttpStatus.OK)

    }


    //opid-get-decisions-decisionId
    @GetMapping("/{decisionsId}")
    fun getDecision(@PathVariable decisionsId: Long, principal: Principal): ResponseEntity<*> {

        val decision  = decisionsService.getDecisionById(principal.name, decisionsId)

        return ResponseEntity<Any>(decision, HttpStatus.OK)

    }

    //opid-post-decisions
    @PostMapping("/")
    @Throws(URISyntaxException::class)
    fun createDecision(@Valid @RequestBody decision: Decision, principal: Principal): ResponseEntity<*> {

        val decision  = decisionsService.saveDecision(principal.name, decision)

        return if (decision != null) {
            ResponseEntity<Any>(decision, HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }
    }

    //opid-delete-decisions-{decisionId}
    @DeleteMapping("/{decisionsId}")
    fun deleteDecision(@PathVariable decisionsId: Long, principal: Principal): ResponseEntity<*> {

        return if (decisionsService.deleteDecision(principal.name, decisionsId)) {
            ResponseEntity<Any>(null, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }

    }

    //opid-put-decisions
    @PutMapping("/")
    @Throws(URISyntaxException::class)
    fun updateDecision(@Valid @RequestBody decision: Decision, principal: Principal): ResponseEntity<*> {

        val decision  = decisionsService.saveDecision(principal.name, decision)

        return if (decision != null) {
            ResponseEntity<Any>(decision, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }
    }



}