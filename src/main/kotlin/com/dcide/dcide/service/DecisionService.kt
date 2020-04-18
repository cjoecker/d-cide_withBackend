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



    fun getDecisionsByUser(username: String): Iterable<Decision> {

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
        if (decision.id != null && getDecisionById(username, decision.id!!) == null)
            return null

        if(decision.user == null){
            decision.user = userRepository.findByUsername(username)
        }else{
            if(userService.authenticateUser(decision.user!!.username, decision.user!!.password).isAuthenticated){
                decision.user = userRepository.findByUsername(decision.user!!.username)
           }
        }

        return decisionRepository.save(decision)
    }


    fun createExampleData(username: String, decision: Decision) {
        createTestDecisionOptions(username, decision)
        createTestSelectionCriteria(username, decision)
    }

    fun createTestDecisionOptions(username: String, decision: Decision) {
        Stream.of("House 3", "House 2", "House 1").forEach { decisionOption ->
            decisionOptionRepository.save(
                    DecisionOption(0, decisionOption, 0.0, decision)
            )
        }
    }

    fun createTestSelectionCriteria(username: String, decision: Decision) {
        Stream.of("Garden", "Kitchen", "Neighborhood", "Size").forEach { selectionCriteria ->
            selectionCriteriaRepository.save(
                    SelectionCriteria(0, selectionCriteria, 0.0, decision)
            )
        }
    }



}