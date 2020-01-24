package com.dcide.dcide.control


import com.dcide.dcide.model.*
import com.dcide.dcide.service.SelectionCriteriaService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.validation.Valid
import java.security.Principal


@RestController
@RequestMapping("/api/decisions/{decisionId}/selectionCriteria")
internal class SelectionCriteriaController(private val selectionCriteriaRepository: SelectionCriteriaRepository) {

    @Autowired
    lateinit var weightedCriteriaRepository: WeightedCriteriaRepository

    @Autowired
    lateinit var selectionCriteriaService: SelectionCriteriaService


    @GetMapping("")
    fun everySelectionCriteria(@PathVariable decisionId: Long, principal: Principal): ResponseEntity<*> {

        val decisions = selectionCriteriaService.getSelectionCriteria(principal.name, decisionId)

        return ResponseEntity<Any>(decisions, HttpStatus.OK)
    }

    @GetMapping("/{optionId}")
    fun getSelectionCriteria(@PathVariable decisionId: Long, @PathVariable optionId: Long, principal: Principal): ResponseEntity<*> {

        val selectionCriteria = selectionCriteriaService.getSelectionCriteriaById(principal.name, decisionId, optionId)

        return if (selectionCriteria != null) {
            ResponseEntity<Any>(selectionCriteria, HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }
    }


    @PostMapping("/")
    fun createSelectionCriteria(@PathVariable decisionId: Long, @Valid @RequestBody selectionCriteria: SelectionCriteria,
                             principal: Principal): ResponseEntity<*> {

        val decision  = selectionCriteriaService.saveSelectionCriteria(principal.name, decisionId, selectionCriteria)

        return if (decision != null) {
            ResponseEntity<Any>(decision, HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }

    }

    @PutMapping("/")
    fun editSelectionCriteria(@PathVariable decisionId: Long, @Valid @RequestBody selectionCriteria: SelectionCriteria,
                           principal: Principal): ResponseEntity<*> {

        val decision  = selectionCriteriaService.saveSelectionCriteria(principal.name, decisionId, selectionCriteria)

        return if (decision != null) {
            ResponseEntity<Any>(decision, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }

    }

    @DeleteMapping("/{optionId}")
    fun deleteSelectionCriteria(@PathVariable decisionId: Long, @PathVariable optionId: Long,
                             principal: Principal): ResponseEntity<*> {

        return if (selectionCriteriaService.deleteSelectionCriteria(principal.name, decisionId, optionId)) {
            ResponseEntity<Any>(null, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }

    }

// refactor
//    fun weightCriteria(decision_id: Long, principal: Principal): Collection<SelectionCriteria> {
//
//        //Get max sum of weighted criteria
//        val weightSum = weightedCriteriaRepository.findAll().filter {
//            it.selectedCriteria.decision!!.id == decision_id &&
//                    it.selectedCriteria.decision!!.user!!.username == principal.name
//        }.sumBy { Math.abs(it.weight) }
//
//
//        //Sum Values
//        selectionCriteriaRepository.findAll().filter {
//            it.decision!!.id == decision_id &&
//                    it.decision!!.user!!.username == principal.name
//        }.forEach { selectionCriteria ->
//
//            var score = weightedCriteriaRepository.findAll().filter {
//                it.selectedCriteria.id == selectionCriteria.id &&
//                it.selectionCriteria1.decision!!.user!!.username == principal.name &&
//                        it.selectionCriteria2.decision!!.user!!.username == principal.name
//            }.sumBy { Math.abs(it.weight) }.toDouble()
//
//            //Make value 0.0 - 10.0 scale
//            score = Math.round(score / weightSum * 100) / 10.0
//
//            val newSelectionCriteria = selectionCriteria.copy(score = score)
//
//            selectionCriteriaRepository.save(newSelectionCriteria)
//        }
//
//        return selectionCriteriaRepository.findAll().filter {
//            it.decision!!.user!!.username == principal.name &&
//                    it.decision!!.id == decision_id
//        }.sortedWith(compareBy { it.score }).reversed()
//    }
}