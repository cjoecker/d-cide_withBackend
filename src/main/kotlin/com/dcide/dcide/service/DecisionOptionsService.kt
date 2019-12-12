package com.dcide.dcide.service


import com.dcide.dcide.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*

import javax.validation.Valid


import org.springframework.web.bind.annotation.GetMapping
import java.lang.Math.abs
import java.security.Principal


@Service
internal class DecisionOptionsService(private val decisionOptionRepository: DecisionOptionRepository) {


    @Autowired
    lateinit var decisionsRepository: DecisionsRepository

    @Autowired
    lateinit var weightedCriteriaRepository: WeightedCriteriaRepository

    @Autowired
    lateinit var ratedOptionRepository: RatedOptionRepository

    @Autowired
    lateinit var selectionCriteriaRepository: SelectionCriteriaRepository

    fun getDecisionOptions(username: String, decisionId: Long): Iterable<DecisionOption> {

        val decisionOptions = decisionOptionRepository.findAll().filter {
            it.decision?.user?.username  == username &&
                    it.decision?.id == decisionId
        }

        return decisionOptions.toList().sortedByDescending { it.id }
    }



}