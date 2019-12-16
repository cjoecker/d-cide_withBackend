package com.dcide.dcide.control


import com.dcide.dcide.model.*
import com.dcide.dcide.service.DecisionOptionsService
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
    lateinit var decisionRepository: DecisionRepository

    @Autowired
    lateinit var weightedCriteriaRepository: WeightedCriteriaRepository

    @Autowired
    lateinit var ratedOptionRepository: RatedOptionRepository

    @Autowired
    lateinit var selectionCriteriaRepository: SelectionCriteriaRepository

    @Autowired
    lateinit var selectionCriteriaController: SelectionCriteriaController

    @Autowired
    lateinit var decisionOptionsService: DecisionOptionsService


    @GetMapping("")
    fun everyDecisionOptions(@PathVariable decisionId: Long, principal: Principal): ResponseEntity<*> {

        val decisions = decisionOptionsService.getDecisionOptions(principal.name, decisionId)

        return ResponseEntity<Any>(decisions, HttpStatus.OK)
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

        val decision  = decisionOptionsService.saveDecisionOption(principal.name, decisionId, decisionOption)

        return if (decision != null) {
            ResponseEntity<Any>(decision, HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }

    }

    @PutMapping("/")
    fun editDecisionOption(@PathVariable decisionId: Long, @Valid @RequestBody decisionOption: DecisionOption,
                             principal: Principal): ResponseEntity<*> {

        val decision  = decisionOptionsService.saveDecisionOption(principal.name, decisionId, decisionOption)

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

    //refactor
    @GetMapping("/result/decisionOption/{decision_id}")
    fun decisionOptionSorted(@PathVariable decision_id: Long, principal: Principal): Collection<DecisionOption> {

        //Sum weightedCriteria if it's not already summed
        selectionCriteriaController.weightCriteria(decision_id, principal)

        //Get total sum
        val weightSum = weightedCriteriaRepository.findAll().filter {
            it.selectedCriteria.decision!!.id == decision_id &&
                    it.selectedCriteria.decision!!.user!!.username == principal.name
        }.sumBy { abs(it.weight) }


        val decisionOptionRepositoryFiltered = decisionOptionRepository.findAll().filter {
            it.decision!!.user!!.username == principal.name &&
                    it.decision!!.id == decision_id
        }

        val ratedOptionRepositoryFiltered = ratedOptionRepository.findAll().filter {
            it.decisionOption.decision!!.user!!.username == principal.name &&
                    it.selectionCriteria.decision!!.user!!.username == principal.name &&

                    it.decisionOption.decision!!.id == decision_id &&
                    it.selectionCriteria.decision!!.id == decision_id
        }




        decisionOptionRepositoryFiltered.forEach { decisionOption ->

            var score = 0.0

            ratedOptionRepositoryFiltered.filter {
                it.decisionOption.id == decisionOption.id
            }.forEach { ratedOption ->
                val selectionCriteria = selectionCriteriaRepository.findById(ratedOption.selectionCriteria.id).get()
                score += ratedOption.rating * selectionCriteria.score
            }

            //Round to one decimal
            score = if (weightSum == 0) 0.0 else (score / 10).toInt().toDouble() / 10

            decisionOption.score = score

            decisionOptionRepository.save(decisionOption)

        }

        return decisionOptionRepository.findAll().filter {
            it.decision!!.user!!.username == principal.name &&
                    it.decision!!.id == decision_id
        }.sortedWith(compareBy({ it.score }, { it.name })).reversed()
    }

}