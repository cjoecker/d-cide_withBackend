package com.dcide.dcide.control


import com.dcide.dcide.model.*


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.validation.Valid
import java.net.URISyntaxException


import org.springframework.web.bind.annotation.GetMapping
import java.security.Principal
import com.dcide.dcide.security.UserRepository
import com.dcide.dcide.service.DecisionsService
import org.springframework.beans.factory.annotation.Autowired
import java.util.stream.Stream


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

    @GetMapping("")
    fun getDecisions(principal: Principal): ResponseEntity<*>  {

        val decisions  = decisionsService.getDecisions(principal.name)

        return if (decisions.count() > 0) {
            ResponseEntity<Any>(decisions, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }

    }


    @GetMapping("/{decisionsId}")
    fun getDecision(@PathVariable decisionsId: Long, principal: Principal): ResponseEntity<*> {

        val decision  = decisionsService.getDecisionById(principal.name, decisionsId)

        return if (decision != null) {
            ResponseEntity<Any>(decision, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }

    }


    @PostMapping("")
    @Throws(URISyntaxException::class)
    fun createDecision(@Valid @RequestBody decision: Decision, principal: Principal): ResponseEntity<*> {

        val decision  = decisionsService.createDecision(principal.name, decision)

        return if (decision != null) {
            ResponseEntity<Any>(decision, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }
    }


    @DeleteMapping("/{decisionsId}")
    fun deleteDecision(@PathVariable decisionsId: Long, principal: Principal): ResponseEntity<*> {

        return if (decisionsService.deleteDecision(principal.name, decisionsId)) {
            ResponseEntity<Any>(null, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }

    }

    @PostMapping("/createExampleData")
    fun createExampleData(principal: Principal): ResponseEntity<*> {

        val decisions = decisionsRepository.findAll().filter {
            it.user!!.username == principal.name
        }

        if (decisions.count() == 1) {

            //Create test decision options
            Stream.of("House in pleasant street", "House in seldom seen avenue", "House in yellowsnow road").forEach { decisionOption ->
                decisionOptionRepository.save(
                        DecisionOption(0, decisionOption, 0.0, decisions[0])
                )
            }


            //Create test selection criteria
            Stream.of("Garden", "Kitchen", "Neighborhood", "Size").forEach { selectionCriteria ->
                selectionCriteriaRepository.save(
                        SelectionCriteria(0, selectionCriteria, 0.0, decisions[0])
                )
            }

            return ResponseEntity<Any>(null, HttpStatus.CREATED)
        } else {
            return ResponseEntity<Any>(null, HttpStatus.METHOD_NOT_ALLOWED)
        }

    }

    @PutMapping("/transferDecisionToUser")
    fun transferDecision(@RequestBody username: String, principal: Principal): ResponseEntity<*> {

        var result: Decision? = null

        //get unregistered user from token

        val decision = decisionsRepository.findAll().filter {
            it.user!!.username == username
        }

        if (decision.count() == 1) {
            val user = userRepository.findByUsername(principal.name)

            decision[0].user = user

            result = decisionsRepository.save(decision[0])
        }

        return if (result != null) {
            ResponseEntity<Any>(result, HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }

    }


}