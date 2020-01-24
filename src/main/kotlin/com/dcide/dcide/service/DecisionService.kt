package com.dcide.dcide.service


import com.dcide.dcide.model.*
import com.dcide.dcide.service.WeightedCriteriaService

import com.dcide.dcide.model.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.stream.Stream


@Service
class DecisionService(private val decisionRepository: DecisionRepository) {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var decisionOptionRepository: DecisionOptionRepository

    @Autowired
    lateinit var selectionCriteriaRepository: SelectionCriteriaRepository

    @Autowired
    lateinit var weightedCriteriaRepository: WeightedCriteriaRepository

    @Autowired
    lateinit var weightedCriteriaService: WeightedCriteriaService


    fun getDecisions(username: String): Iterable<Decision> {

        val decisions = decisionRepository.findAll().filter {
            it.user?.username == username
        }

        return decisions.toList().sortedByDescending { it.id }
    }

    fun getDecisionById(username: String, id: Long): Decision? {

        return decisionRepository.findById(id).filter {
            it.user?.username == username
        }.orElse(null)
    }


    fun saveDecision(username: String, decision: Decision): Decision? {

        //Authenticate Decision
        if (decision.id != null && getDecisionById(username, decision.id!!) == null)
            return null

        if(decision.user == null){
            //Save to user
            decision.user = userRepository.findByUsername(username)
        }else{
            //Transfer to user
            if(userService.authenticateUser(decision.user!!.username, decision.user!!.password).isAuthenticated){
                decision.user = userRepository.findByUsername(decision.user!!.username)
           }
        }

        return decisionRepository.save(decision)
    }

    // refactor
    fun deleteDecision(username: String, decisionsId: Long): Boolean {

        val decision = getDecisionById(username, decisionsId)

        return if (decision?.id != null) {

            //Delete Decision
            decisionRepository.deleteById(decisionsId)
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




}