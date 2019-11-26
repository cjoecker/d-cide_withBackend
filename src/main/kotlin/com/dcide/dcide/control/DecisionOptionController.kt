package com.dcide.dcide.control


import com.dcide.dcide.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.validation.Valid


import org.springframework.web.bind.annotation.GetMapping
import java.lang.Math.abs
import java.security.Principal


@RestController
@RequestMapping("/api")
@CrossOrigin
internal class DecisionOptionController(private val decisionOptionRepository: DecisionOptionRepository) {


    @Autowired
    lateinit var decisionsRepository: DecisionsRepository

    @Autowired
    lateinit var weightedCriteriaRepository: WeightedCriteriaRepository

    @Autowired
    lateinit var ratedOptionRepository: RatedOptionRepository

    @Autowired
    lateinit var selectionCriteriaRepository: SelectionCriteriaRepository

    @Autowired
    lateinit var selectionCriteriaController: SelectionCriteriaController


    @GetMapping("/every_decisionOption/{decision_id}")
    fun everyDecisionOption(@PathVariable decision_id : Long , principal : Principal): ResponseEntity<*>  {

        var result: MutableSet<DecisionOption>? = null

        val decision = decisionsRepository.findByIdOrNull(decision_id)

        if (decision != null && decision.user!!.username == principal.name) {
            result = decision.decisionOption.sortedByDescending{ it.id }.toMutableSet()
        }

        return if (result != null) {
            ResponseEntity<Any>(result, HttpStatus.OK)
        } else {
            ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/decisionOption/{option_id}")
    fun getDecisionOption(@PathVariable option_id: Long, principal : Principal): ResponseEntity<*> {

        val decisionOption = decisionOptionRepository.findById(option_id).filter{
            it.decision!!.user!!.username == principal.name
        }

        return decisionOption.map { response -> ResponseEntity.ok().body(response) }
                .orElse(ResponseEntity(HttpStatus.NOT_FOUND))
    }


    @PostMapping("/decisionOption/{decision_id}")
    fun createDecisionOption(@PathVariable decision_id: Long, @Valid @RequestBody decisionOption: DecisionOption,
                             principal: Principal): ResponseEntity<*> {

        var result: DecisionOption? = null

        val decision = decisionsRepository.findByIdOrNull(decision_id)

        if (decision != null && decision.user!!.username == principal.name) {
            decisionOption.decision = decision
            result = decisionOptionRepository.save(decisionOption)
        }
        return if (result != null) {
            ResponseEntity<Any>(result, HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.BAD_REQUEST)
        }

    }

    @DeleteMapping("/decisionOption/{option_id}")
    fun deleteDecisionOption(@PathVariable option_id: Long, principal : Principal): ResponseEntity<*> {

        val decisionOption = decisionOptionRepository.findById(option_id).filter{
            it.decision!!.user!!.username == principal.name
        }.orElse(null)

        return if(decisionOption != null){
            decisionOptionRepository.deleteById(option_id)
            ResponseEntity.ok().build<Any>()
        }else{
            ResponseEntity.notFound().build<Any>()
        }

    }

    @GetMapping("/result/decisionOption/{decision_id}")
    fun decisionOptionSorted(@PathVariable decision_id: Long, principal: Principal): Collection<DecisionOption> {

        //Sum weightedCriteria if it's not already summed
        selectionCriteriaController.weightCriteria(decision_id,principal)

        //Get total sum
        val weightSum =  weightedCriteriaRepository.findAll().filter {
            it.selectedCriteria.decision!!.id == decision_id &&
                    it.selectedCriteria.decision!!.user!!.username == principal.name
        }.sumBy {abs(it.weight)}


        val decisionOptionRepositoryFiltered =  decisionOptionRepository.findAll().filter {
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

            ratedOptionRepositoryFiltered.filter { it.decisionOption.id == decisionOption.id
            }.forEach { ratedOption ->
                val selectionCriteria = selectionCriteriaRepository.findById(ratedOption.selectionCriteria.id).get()
                score += ratedOption.rating * selectionCriteria.score
            }

            //Round to one decimal
            score = if(weightSum == 0) 0.0 else (score/10).toInt().toDouble()/10

            decisionOption.score = score

            decisionOptionRepository.save(decisionOption)

        }

        return decisionOptionRepository.findAll().filter{
            it.decision!!.user!!.username == principal.name &&
            it.decision!!.id == decision_id
        }.sortedWith(compareBy({ it.score }, { it.name})).reversed()
    }

}