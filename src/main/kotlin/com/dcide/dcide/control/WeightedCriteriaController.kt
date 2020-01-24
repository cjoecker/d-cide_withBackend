package com.dcide.dcide.control


import com.dcide.dcide.model.*
import com.dcide.dcide.service.WeightedCriteriaService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal


@RestController
@RequestMapping("/api")
internal class WeightedCriteriaController(private val weightedCriteriaRepository: WeightedCriteriaRepository) {


    @Autowired
    lateinit var selectionCriteriaRepository: SelectionCriteriaRepository

    @Autowired
    lateinit var decisionRepository: DecisionRepository

    @Autowired
    lateinit var weightedCriteriaService: WeightedCriteriaService


    @GetMapping("/decisions/{decisionId}/weightedCriteria")
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
//    @PostMapping("/weightedCriteria")
//    fun createWeightedCriteria(@Valid @RequestBody weightedCriteria: WeightedCriteria,
//                               principal: Principal): ResponseEntity<*> {
//
//        var result: WeightedCriteria? = null
//
//
//        val weightedCriteriaLocal = weightedCriteriaRepository.findAll().filter {
//            it.id == weightedCriteria.id &&
//                    it.selectionCriteria1.decision!!.user!!.username == principal.name &&
//                    it.selectionCriteria2.decision!!.user!!.username == principal.name &&
//                    it.selectedCriteria.decision!!.user!!.username == principal.name
//        }
//
//        if (weightedCriteriaLocal.isNotEmpty()) {
//            weightedCriteriaLocal[0].weight = weightedCriteria.weight
//            result = weightedCriteriaRepository.save(weightedCriteria)
//        }
//
//        //Give user
//        return if (result != null) {
//            ResponseEntity<Any>(result, HttpStatus.CREATED)
//        } else {
//            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
//        }
//
//    }
//
//    @PostMapping("/every_weightedCriteria")
//    fun postEveryWeightedCriteria(@Valid @RequestBody weightedCriteriaList: List<WeightedCriteria>,
//                                  principal: Principal): ResponseEntity<*> {
//
//        var result = true
//
//        weightedCriteriaList.forEach {
//            var weightedCriteria = weightedCriteriaRepository.findByIdOrNull(it.id)
//
//            if (weightedCriteria != null &&
//                    weightedCriteria.selectionCriteria1.decision!!.user!!.username == principal.name &&
//                    weightedCriteria.selectionCriteria2.decision!!.user!!.username == principal.name &&
//                    weightedCriteria.selectedCriteria.decision!!.user!!.username == principal.name
//            ) {
//                weightedCriteria.weight = it.weight
//                weightedCriteriaRepository.save(weightedCriteria)
//            }else{
//                result = false
//            }
//        }
//
//        //Give user
//        return if (result) {
//            ResponseEntity<Any>(result, HttpStatus.CREATED)
//        } else {
//            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
//        }
//
//    }


}