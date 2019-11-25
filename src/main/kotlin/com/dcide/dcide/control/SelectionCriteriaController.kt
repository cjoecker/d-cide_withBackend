package com.dcide.dcide.control


import com.dcide.dcide.model.DecisionsRepository
import com.dcide.dcide.model.SelectionCriteria
import com.dcide.dcide.model.SelectionCriteriaRepository
import com.dcide.dcide.model.WeightedCriteriaRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.validation.Valid
import java.security.Principal


@RestController
@RequestMapping("/api")
internal class SelectionCriteriaController(private val selectionCriteriaRepository: SelectionCriteriaRepository) {

    @Autowired
    lateinit var decisionsRepository: DecisionsRepository

    @Autowired
    lateinit var weightedCriteriaRepository: WeightedCriteriaRepository

    @GetMapping("/every_selectionCriteria/{project_id}")
    fun everySelectionCriteria(@PathVariable project_id: Long, principal: Principal): ResponseEntity<*> {

        var result: MutableSet<SelectionCriteria>? = null

        val project = decisionsRepository.findByIdOrNull(project_id)

        if (project != null && project.user!!.username == principal.name) {
            result = project.selectionCriteria.sortedByDescending { it.id }.toMutableSet()
        }

        return if (result != null) {
            ResponseEntity<Any>(result, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/selectionCriteria/{criteria_id}")
    fun getSelectionCriteria(@PathVariable criteria_id: Long, principal: Principal): ResponseEntity<*> {

        val selectionCriteria = selectionCriteriaRepository.findById(criteria_id).filter {
            it.decision!!.user!!.username == principal.name
        }

        return selectionCriteria.map { response -> ResponseEntity.ok().body(response) }
                .orElse(ResponseEntity(HttpStatus.NOT_FOUND))
    }


    @PostMapping("/selectionCriteria/{project_id}")
    fun createSelectionCriteria(@PathVariable project_id: Long, @Valid @RequestBody selectionCriteria: SelectionCriteria,
                                principal: Principal): ResponseEntity<*> {

        var result: SelectionCriteria? = null

        val project = decisionsRepository.findByIdOrNull(project_id)

        if (project != null && project.user!!.username == principal.name) {
            selectionCriteria.decision = project
            result = selectionCriteriaRepository.save(selectionCriteria)
        }
        return if (result != null) {
            ResponseEntity<Any>(result, HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }

    }

    @DeleteMapping("/selectionCriteria/{criteria_id}")
    fun deleteSelectionCriteria(@PathVariable criteria_id: Long, principal: Principal): ResponseEntity<*> {

        val selectionCriteria = selectionCriteriaRepository.findById(criteria_id).filter {
            it.decision!!.user!!.username == principal.name
        }.orElse(null)

        return if (selectionCriteria != null) {

            //Delete WeightedCriteria
            val weightedCriteriaToDelete = weightedCriteriaRepository.findAll().filter {
                it.selectionCriteria1.decision!!.user!!.username == principal.name &&
                        it.selectionCriteria2.decision!!.user!!.username == principal.name &&
                        it.selectedCriteria.decision!!.user!!.username == principal.name &&
                        (it.selectionCriteria1.id == criteria_id || it.selectionCriteria2.id == criteria_id)
            }

            weightedCriteriaRepository.deleteInBatch(weightedCriteriaToDelete)

            //Delete Selection Criteria
            selectionCriteriaRepository.deleteById(criteria_id)
            ResponseEntity.ok().build<Any>()
        } else {
            ResponseEntity.notFound().build<Any>()
        }

    }

    @GetMapping("/result/selectionCriteria/{project_id}")
    fun selectionCriteriaSorted(@PathVariable project_id: Long, principal: Principal): Collection<SelectionCriteria> {

        return weightCriteria(project_id, principal)

    }


    fun weightCriteria(project_id: Long, principal: Principal): Collection<SelectionCriteria> {

        //Get max sum of weighted criteria
        val weightSum = weightedCriteriaRepository.findAll().filter {
            it.selectedCriteria.decision!!.id == project_id &&
                    it.selectedCriteria.decision!!.user!!.username == principal.name
        }.sumBy { Math.abs(it.weight) }


        //Sum Values
        selectionCriteriaRepository.findAll().filter {
            it.decision!!.id == project_id &&
                    it.decision!!.user!!.username == principal.name
        }.forEach { selectionCriteria ->

            var score = weightedCriteriaRepository.findAll().filter {
                it.selectedCriteria.id == selectionCriteria.id &&
                it.selectionCriteria1.decision!!.user!!.username == principal.name &&
                        it.selectionCriteria2.decision!!.user!!.username == principal.name
            }.sumBy { Math.abs(it.weight) }.toDouble()

            //Make value 0.0 - 10.0 scale
            score = Math.round(score / weightSum * 100) / 10.0

            val newSelectionCriteria = selectionCriteria.copy(score = score)

            selectionCriteriaRepository.save(newSelectionCriteria)
        }

        return selectionCriteriaRepository.findAll().filter {
            it.decision!!.user!!.username == principal.name &&
                    it.decision!!.id == project_id
        }.sortedWith(compareBy { it.score }).reversed()
    }
}