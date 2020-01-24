package com.dcide.dcide.service


import com.dcide.dcide.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service


@Service
internal class WeightedCriteriaService(private val weightedCriteriaService: WeightedCriteriaService) {

    @Autowired
    lateinit var decisionService: DecisionService

    @Autowired
    lateinit var weightedCriteriaRepository: WeightedCriteriaRepository

    fun getWeightedCriteria (username: String, decisionId: Long): Iterable<WeightedCriteria>? {

        //Authenticate decision
        val decision = decisionService.getDecisionById(username, decisionId) ?: return null


        //get old wighted criteria
        val weightedCriteriaOld = weightedCriteriaRepository.findAll().filter {
            it.decision!!.id == decision.id
        }

        //get selection criteria



    }


}