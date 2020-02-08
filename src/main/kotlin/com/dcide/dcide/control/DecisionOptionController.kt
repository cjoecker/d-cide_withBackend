package com.dcide.dcide.control


import com.dcide.dcide.model.*
import com.dcide.dcide.service.DecisionOptionService
import org.springframework.beans.factory.annotation.Autowired


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.validation.Valid


import org.springframework.web.bind.annotation.GetMapping
import java.lang.Math.abs
import java.security.Principal


@RestController
@RequestMapping("/api/decisions/{decisionId}/decisionOptions")
@CrossOrigin
internal class DecisionOptionController(private val decisionOptionRepository: DecisionOptionRepository) {


    @Autowired
    lateinit var weightedCriteriaRepository: WeightedCriteriaRepository

    @Autowired
    lateinit var ratedOptionRepository: RatedOptionRepository

    @Autowired
    lateinit var selectionCriteriaRepository: SelectionCriteriaRepository

    @Autowired
    lateinit var selectionCriteriaController: SelectionCriteriaController

    @Autowired
    lateinit var decisionOptionsService: DecisionOptionService


    @GetMapping("")
    fun getDecisionOptions(@PathVariable decisionId: Long, @RequestParam(required = false) calculatedScore: Boolean, principal: Principal): ResponseEntity<*> {

        if (calculatedScore) decisionOptionsService.rateDecisionOptions(principal.name, decisionId)

        val decisions = decisionOptionsService.getDecisionOptions(principal.name, decisionId)

        return if (calculatedScore)
            ResponseEntity<Any>(decisions.sortedWith(compareBy({ it.score }, { it.name })).reversed(), HttpStatus.OK)
        else
            ResponseEntity<Any>(decisions.shuffled(), HttpStatus.OK)

    }

    @GetMapping("/{optionId}")
    fun getDecisionOption(@PathVariable decisionId: Long, @PathVariable optionId: Long, principal: Principal): ResponseEntity<*> {

        val decisionOption = decisionOptionsService.getDecisionOptionsById(principal.name, decisionId, optionId)

        return if (decisionOption != null) {
            ResponseEntity<Any>(decisionOption, HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }
    }


    @PostMapping("/")
    fun createDecisionOption(@PathVariable decisionId: Long, @Valid @RequestBody decisionOption: DecisionOption,
                             principal: Principal): ResponseEntity<*> {

        val decision = decisionOptionsService.saveDecisionOption(principal.name, decisionId, decisionOption)

        return if (decision != null) {
            ResponseEntity<Any>(decision, HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }

    }

    @PutMapping("/")
    fun editDecisionOption(@PathVariable decisionId: Long, @Valid @RequestBody decisionOption: DecisionOption,
                           principal: Principal): ResponseEntity<*> {

        val decision = decisionOptionsService.saveDecisionOption(principal.name, decisionId, decisionOption)

        return if (decision != null) {
            ResponseEntity<Any>(decision, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }

    }

    @DeleteMapping("/{optionId}")
    fun deleteDecisionOption(@PathVariable decisionId: Long, @PathVariable optionId: Long,
                             principal: Principal): ResponseEntity<*> {

        return if (decisionOptionsService.deleteDecisionOption(principal.name, decisionId, optionId)) {
            ResponseEntity<Any>(null, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }

    }

}