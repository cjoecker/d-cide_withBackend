package com.dcide.dcide.service


import com.dcide.dcide.model.*
import org.springframework.beans.factory.annotation.Autowired


import org.springframework.stereotype.Service


@Service
internal class DecisionOptionsService(private val decisionOptionRepository: DecisionOptionRepository) {


    @Autowired
    lateinit var decisionRepository: DecisionRepository

    @Autowired
    lateinit var weightedCriteriaRepository: WeightedCriteriaRepository

    @Autowired
    lateinit var ratedOptionRepository: RatedOptionRepository

    @Autowired
    lateinit var selectionCriteriaRepository: SelectionCriteriaRepository

    @Autowired
    lateinit var decisionsService: DecisionsService

    fun getDecisionOptions(username: String, decisionId: Long): Iterable<DecisionOption> {

        val decisionOptions = decisionOptionRepository.findAll().filter {
            it.decision?.user?.username == username &&
                    it.decision?.id == decisionId
        }

        return decisionOptions.toList().sortedByDescending { it.id }
    }


    fun getDecisionOptionsById(username: String, decisionId: Long, optionId: Long): DecisionOption? {

        return decisionOptionRepository.findById(optionId).filter {
            it.decision?.user?.username == username &&
                    it.decision?.id == decisionId
        }.orElse(null)
    }

    fun saveDecisionOption(username: String, decisionId: Long, decisionOption: DecisionOption): DecisionOption? {

        //Authenticate Decision
        val decisionLocal  = decisionsService.getDecisionById(username, decisionId)

        if (decisionLocal != null && decisionLocal.user?.username != username)
            return null

        //Authenticate Decision Option
        val decisionOptionLocal = decisionOptionRepository.findById(decisionOption.id).orElse(null)

        if (decisionOptionLocal != null && decisionOptionLocal.decision?.user?.username != username)
            return null

        //Save Decision Option
        decisionOption.decision = decisionLocal

        return decisionOptionRepository.save(decisionOption)

    }

    fun deleteDecisionOption(username: String, decisionId: Long, optionId:Long): Boolean {

        val decisionOptionLocal = getDecisionOptionsById(username, decisionId, optionId)

        return if (decisionOptionLocal != null){
            decisionOptionRepository.deleteById(optionId)
            true
        }else{
            false
        }


    }


}