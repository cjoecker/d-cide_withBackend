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

    //    @GetMapping("/weightedCriteria/{criteria1_id}/{criteria2_id}")
//    fun getWeightedCriteria(@PathVariable criteria1_id: Long,
//                            @PathVariable criteria2_id: Long,
//                            principal: Principal): ResponseEntity<*> {
//
//
//        val weightedCriteria = weightedCriteriaRepository.findAll().filter {
//            it.selectionCriteria1.decision!!.user!!.username == principal.name &&
//                    it.selectionCriteria2.decision!!.user!!.username == principal.name &&
//                    it.selectedCriteria.decision!!.user!!.username == principal.name
//        }
//
//        return if (weightedCriteria.isEmpty()) {
//            ResponseEntity<Any>(weightedCriteria, HttpStatus.OK)
//        } else {
//            ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
//        }
//
//    }
//
//
//
    @PutMapping("")
    fun saveAllWeightedCriteria(@PathVariable decisionId: Long, @Valid @RequestBody weightedCriteriaList: List<WeightedCriteria>,
                                principal: Principal): ResponseEntity<*> {

        weightedCriteriaService.saveWeightedCriteria(principal.name, decisionId, weightedCriteriaList)

        return ResponseEntity<Any>(null, HttpStatus.OK)

    }

    @DeleteMapping("/{criteriaId}")
    fun deleteWeightedCriteria(@PathVariable decisionId: Long, @PathVariable criteriaId: Long,
                               principal: Principal): ResponseEntity<*> {

        weightedCriteriaService.deleteWeightedCriteria(principal.name, decisionId, criteriaId)

        return ResponseEntity<Any>(null, HttpStatus.OK)

    }


}