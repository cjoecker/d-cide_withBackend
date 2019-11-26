package com.dcide.dcide.service


import com.dcide.dcide.model.*


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


import java.security.Principal
import com.dcide.dcide.model.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.stream.Stream


@Service
class DecisionsService(private val decisionsRepository: DecisionsRepository) {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var decisionOptionRepository: DecisionOptionRepository

    @Autowired
    lateinit var selectionCriteriaRepository: SelectionCriteriaRepository

    @Autowired
    lateinit var weightedCriteriaRepository: WeightedCriteriaRepository


    fun getDecisions(username: String): Iterable<Decision> {

        val decisions = decisionsRepository.findAll().filter {
            it.user!!.username == username
        }

        return decisions.toList().sortedByDescending { it.id }
    }

    fun getDecisionById(username: String, id: Long): Decision? {

        val decisions = decisionsRepository.findById(id).filter {
            it.user!!.username == username
        }.orElse(null)

        return decisions
    }


    fun createDecision(username: String, decision: Decision): Decision? {

        //Authenticate Decision
        if (decision.id != null && getDecisionById(username, decision.id) == null)
            return null

        //Save decision
        val user = userRepository.findByUsername(username)
        decision.user = user
        return decisionsRepository.save(decision)
    }

    // refactor
    fun deleteDecision(username: String, decisionsId: Long): Boolean {

        val decision = getDecisionById(username, decisionsId)

        return if (decision != null) {

            //Delete Selection Criteria
            val weightedCriterias = weightedCriteriaRepository.findAll().filter {
                it.selectedCriteria.decision!!.id == decisionsId &&
                        it.selectionCriteria1.decision!!.id == decisionsId &&
                        it.selectionCriteria2.decision!!.id == decisionsId
            }

            weightedCriteriaRepository.deleteAll(weightedCriterias)

            //Delete Decision
            decisionsRepository.deleteById(decisionsId)
            true
        } else {
            false
        }

    }

    //refactor
    fun createExampleData(username: String, decision: Decision) {


        //Create test decision options
        Stream.of("House in pleasant street", "House in seldom seen avenue", "House in yellowsnow road").forEach { decisionOption ->
            decisionOptionRepository.save(
                    DecisionOption(0, decisionOption, 0.0, decision)
            )
        }


        //Create test selection criteria
        Stream.of("Garden", "Kitchen", "Neighborhood", "Size").forEach { selectionCriteria ->
            selectionCriteriaRepository.save(
                    SelectionCriteria(0, selectionCriteria, 0.0, decision)
            )
        }


    }

    @PutMapping("/transferPecisionToUser")
    fun transferPecision(@RequestBody username: String, principal: Principal): ResponseEntity<*> {

        var result: Decision? = null

        //get unregistered user from token

        val decision = decisionsRepository.findAll().filter {
            it.user!!.username == username
        }

        if (decision.count() == 1) {
            val user = userRepository.findByUsername(principal.name)

            decision[0].user = user

            result = decisionsRepository.save(decision[0])
        }

        return if (result != null) {
            ResponseEntity<Any>(result, HttpStatus.CREATED)
        } else {
            ResponseEntity<Any>(null, HttpStatus.NOT_FOUND)
        }

    }


}