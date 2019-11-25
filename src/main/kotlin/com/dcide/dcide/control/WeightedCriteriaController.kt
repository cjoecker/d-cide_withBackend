package com.dcide.dcide.control


import com.dcide.dcide.model.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

import javax.validation.Valid

@RestController
@RequestMapping("/api")
internal class WeightedCriteriaController(private val weightedCriteriaRepository: WeightedCriteriaRepository) {


    @Autowired
    lateinit var selectionCriteriaRepository: SelectionCriteriaRepository

    @Autowired
    lateinit var decisionsRepository: DecisionsRepository


    @GetMapping("/every_weightedCriteria/{project_id}")
    fun everyWeightedCriteria(@PathVariable project_id: Long, principal: Principal): ResponseEntity<*> {


        //Check user and decision id
        val project = decisionsRepository.findByIdOrNull(project_id)

        if (project == null || project.user!!.username != principal.name) {
            return ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }


        val weightedCriteriaOld = weightedCriteriaRepository.findAll().filter {
            it.selectionCriteria1.decision!!.user!!.username == principal.name &&
                    it.selectionCriteria2.decision!!.user!!.username == principal.name
        }

        val selectionCriteriaList = selectionCriteriaRepository.findAll().filter {
            it.decision!!.user!!.username == principal.name &&
                    it.decision!!.id == project_id
        }

//        weightedCriteriaRepository.deleteAll(weightedCriteriaOld)

        val selectionCriteriaNum = selectionCriteriaList.count() - 1

        for (i in 0..selectionCriteriaNum) {
            val nextI = i + 1

            for (j in nextI..selectionCriteriaNum) {

                val filteredCriteria = weightedCriteriaOld.filter {
                    it.selectionCriteria1.id == selectionCriteriaList[i].id &&
                            it.selectionCriteria2.id == selectionCriteriaList[j].id
                }

                var id: Long = 0
                var weight = 0
                var selectedCriteria: SelectionCriteria? = selectionCriteriaList[i]

                if (filteredCriteria.count() > 0) {
                    id = filteredCriteria[0].id
                    weight = filteredCriteria[0].weight
                    selectedCriteria = filteredCriteria[0].selectedCriteria
                }

                val weightedCriteria = WeightedCriteria(
                        id,
                        weight,
                        selectionCriteriaList[i],
                        selectionCriteriaList[j],
                        selectedCriteria!!
                )

                weightedCriteriaRepository.save(weightedCriteria)
            }
        }

        return ResponseEntity<Any>(weightedCriteriaRepository.findAll().filter {
            it.selectionCriteria1.decision!!.user!!.username == principal.name &&
                    it.selectionCriteria2.decision!!.user!!.username == principal.name &&
                    it.selectionCriteria1.decision!!.id == project_id &&
                    it.selectionCriteria2.decision!!.id == project_id
        }.shuffled(), HttpStatus.OK)

    }

    @GetMapping("/weightedCriteria/{criteria1_id}/{criteria2_id}")
    fun getWeightedCriteria(@PathVariable criteria1_id: Long,
                            @PathVariable criteria2_id: Long,
                            principal: Principal): ResponseEntity<*> {


        val weightedCriteria = weightedCriteriaRepository.findAll().filter {
            it.selectionCriteria1.decision!!.user!!.username == principal.name &&
                    it.selectionCriteria2.decision!!.user!!.username == principal.name &&
                    it.selectedCriteria.decision!!.user!!.username == principal.name
        }

        return if (weightedCriteria.isEmpty()) {
            ResponseEntity<Any>(weightedCriteria, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }

    }


    @PostMapping("/weightedCriteria")
    fun createWeightedCriteria(@Valid @RequestBody weightedCriteria: WeightedCriteria,
                               principal: Principal): ResponseEntity<*> {

        var result: WeightedCriteria? = null


        val weightedCriteriaLocal = weightedCriteriaRepository.findAll().filter {
            it.id == weightedCriteria.id &&
                    it.selectionCriteria1.decision!!.user!!.username == principal.name &&
                    it.selectionCriteria2.decision!!.user!!.username == principal.name &&
                    it.selectedCriteria.decision!!.user!!.username == principal.name
        }

        if (weightedCriteriaLocal.isNotEmpty()) {
            weightedCriteriaLocal[0].weight = weightedCriteria.weight
            result = weightedCriteriaRepository.save(weightedCriteria)
        }

        //Give user
        return if (result != null) {
            ResponseEntity<Any>(result, HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }

    }

    @PostMapping("/every_weightedCriteria")
    fun postEveryWeightedCriteria(@Valid @RequestBody weightedCriteriaList: List<WeightedCriteria>,
                                  principal: Principal): ResponseEntity<*> {

        var result = true

        weightedCriteriaList.forEach {
            var weightedCriteria = weightedCriteriaRepository.findByIdOrNull(it.id)

            if (weightedCriteria != null &&
                    weightedCriteria.selectionCriteria1.decision!!.user!!.username == principal.name &&
                    weightedCriteria.selectionCriteria2.decision!!.user!!.username == principal.name &&
                    weightedCriteria.selectedCriteria.decision!!.user!!.username == principal.name
            ) {
                weightedCriteria.weight = it.weight
                weightedCriteriaRepository.save(weightedCriteria)
            }else{
                result = false
            }
        }

        //Give user
        return if (result) {
            ResponseEntity<Any>(result, HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }

    }


}