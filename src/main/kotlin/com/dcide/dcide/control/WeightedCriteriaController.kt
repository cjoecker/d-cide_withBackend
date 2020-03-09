package com.dcide.dcide.control



import com.dcide.dcide.model.*
import com.dcide.dcide.service.WeightedCriteriaService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.validation.Valid
import java.security.Principal


@RestController
@RequestMapping("/api/decisions/{decisionId}/weightedCriteria")
internal class WeightedCriteriaController(private val weightedCriteriaRepository: WeightedCriteriaRepository) {


    @Autowired
    lateinit var weightedCriteriaService: WeightedCriteriaService


    @GetMapping("")
    fun getWeightedCriteria(@PathVariable decisionId: Long, principal: Principal): ResponseEntity<*> {

        weightedCriteriaService.createWeightedCriteria(principal.name, decisionId)

        val weightedCriteria = weightedCriteriaService.getWeightedCriteria(principal.name, decisionId)

        return ResponseEntity<Any>(weightedCriteria, HttpStatus.OK)
    }

    @PutMapping("/")
    fun saveAllWeightedCriteria(@PathVariable decisionId: Long, @Valid @RequestBody weightedCriteria: WeightedCriteria,
                                principal: Principal): ResponseEntity<*> {

        weightedCriteriaService.saveWeightedCriteria(principal.name, decisionId, weightedCriteria)

        return ResponseEntity<Any>(null, HttpStatus.OK)

    }


}